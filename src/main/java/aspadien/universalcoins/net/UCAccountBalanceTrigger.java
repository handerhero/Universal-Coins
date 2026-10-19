package aspadien.universalcoins.net;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class UCAccountBalanceTrigger extends AbstractCriterionTrigger<UCAccountBalanceTrigger.Instance> {
    // ID триггера, который будет указываться в JSON
    public static final ResourceLocation ID = new ResourceLocation("universalcoins", "account_balance_has_change");

    // Храним зарегистрированный экземпляр прямо тут:
    public static final UCAccountBalanceTrigger INSTANCE = CriteriaTriggers.register(new UCAccountBalanceTrigger());

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    // Десериализация параметров из JSON-файла ачивки
    @Override
    public Instance createInstance(JsonObject json, EntityPredicate.AndPredicate playerPredicate, ConditionArrayParser parser) {
        // Можно передавать дополнительные условия из JSON (например, начальный баланс):
        // Если в JSON параметра нет, значением будет 0
        // В UCAccountBalanceTrigger.java:
        long minBalance = JSONUtils.getAsLong(json, "min_balance", 0L);
        return new Instance(playerPredicate, minBalance);
    }

    // СТАЛО: static метод! Теперь его можно вызывать откуда угодно:
    public static void trigger(ServerPlayerEntity player, long currentBalance) {
        INSTANCE.triggerListeners(player, currentBalance);
    }

    // Внутренний вызов движка Minecraft:
    private void triggerListeners(ServerPlayerEntity player, long currentBalance) {
        super.trigger(player, instance -> instance.test(currentBalance));
    }

    // Класс условий (критериев)
    public static class Instance extends CriterionInstance {
        private final long minBalance;

        public Instance(EntityPredicate.AndPredicate playerPredicate, long minBalance) {
            super(UCAccountBalanceTrigger.ID, playerPredicate);
            this.minBalance = minBalance;
        }

        // Проверяем, подходит ли условие (например, достаточно ли денег)
        public boolean test(long balance) {
            return balance >= this.minBalance;
        }
    }
}
