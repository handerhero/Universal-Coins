package aspadien.universalcoins.util;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobDropHandler {

    // --- параметры кривой ---
    private static final double DEFAULT_ATTACK = 3.0;
    private static final double ATTACK_WEIGHT  = 6.0;
    private static final double K              = 0.0365;
    private static final double EXPONENT       = 1.5;
    private static final double BOSS_MULT      = 10.0;
    private static final double LOOTING_BONUS  = 0.10;
    private static final long   MAX_VALUE      = 5000;

    // --- шанс дропа: P_MIN + (1 - P_MIN) * (1 - exp(-power / TAU)) ---
    private static final double P_MIN = 0.15;   // шанс у самого слабого моба
    private static final double TAU   = 60.0;   // чем меньше, тем быстрее шанс растёт к 100%

    // --- разброс (лог-нормальный множитель) ---
    private static final double SPREAD_SIGMA = 0.35;
    private static final double SPREAD_MIN   = 0.4;
    private static final double SPREAD_MAX   = 2.5;
    private static final double SPREAD_NORM  = Math.exp(SPREAD_SIGMA * SPREAD_SIGMA / 2.0); // чтобы среднее было 1

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide) return;
        if (!(entity instanceof Mob)) return;

        if (!(event.getSource().getEntity() instanceof Player)
                || event.getSource().getEntity() instanceof FakePlayer) return;

        Random rng = (Random) entity.getRandom();

        double power = computePower(entity);
        double value = computeCoinValue(entity, power)
                * (1.0 + LOOTING_BONUS * event.getLootingLevel());

        // 1. нелинейный шанс дропа
        double chance = dropChance(power);
        if (rng.nextDouble() >= chance) return;

        // 2. выплата при дропе: компенсируем шанс, чтобы матожидание осталось = value
        double payout = value / chance;

        // 3. случайный разброс
        double spread = Math.exp(SPREAD_SIGMA * rng.nextGaussian());
        spread = Math.max(SPREAD_MIN, Math.min(SPREAD_MAX, spread)) / SPREAD_NORM;
        payout *= spread;

        // 4. стохастическое округление дробной части
        long total = (long) payout;
        if (rng.nextDouble() < payout - total) total++;
        total = Math.min(total, MAX_VALUE);
        if (total <= 0) return;

        spawnCoins(event, entity, level, total);
    }

    private static double computePower(LivingEntity entity) {
        double hp = entity.getMaxHealth();
        double dmg = entity.getAttribute(Attributes.ATTACK_DAMAGE) != null
                ? entity.getAttributeValue(Attributes.ATTACK_DAMAGE)
                : DEFAULT_ATTACK;
        return hp * (1.0 + dmg / ATTACK_WEIGHT);
    }

    private static double computeCoinValue(LivingEntity entity, double power) {
        double value = K * Math.pow(power, EXPONENT);
        if (entity instanceof EnderDragon || entity instanceof WitherBoss) {
            value *= BOSS_MULT;
        }
        return value;
    }

    private static double dropChance(double power) {
        return P_MIN + (1.0 - P_MIN) * (1.0 - Math.exp(-power / TAU));
    }

    private static double computeCoinValue(LivingEntity entity) {
        double hp = entity.getMaxHealth();
        double dmg = entity.getAttribute(Attributes.ATTACK_DAMAGE) != null
                ? entity.getAttributeValue(Attributes.ATTACK_DAMAGE)
                : DEFAULT_ATTACK;

        double power = hp * (1.0 + dmg / ATTACK_WEIGHT);
        double value = K * Math.pow(power, EXPONENT);

        if (entity instanceof EnderDragon || entity instanceof WitherBoss) {
            value *= BOSS_MULT;
        }
        return value;
    }

    /** Жадно раскладывает сумму по номиналам от старшего к младшему. */
    private static void spawnCoins(LivingDropsEvent event, LivingEntity entity, Level level, long total) {
        Item[] coins = {
                ModItems.IRON_COIN.get(),     ModItems.GOLD_COIN.get(),
                ModItems.EMERALD_COIN.get(),  ModItems.DIAMOND_COIN.get(),
                ModItems.OBSIDIAN_COIN.get(), ModItems.NETHERITE_COIN.get()
        };

        long remaining = total;
        for (int i = coins.length - 1; i >= 0; i--) {
            long cv = UniversalCoins.coinValues[i];
            long count = remaining / cv;
            remaining %= cv;

            while (count > 0) {
                int n = (int) Math.min(count, 64);
                count -= n;
                ItemEntity ie = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(),
                        new ItemStack(coins[i], n));
                ie.setDefaultPickUpDelay();
                event.getDrops().add(ie);
            }
        }
    }
}