package aspadien.universalcoins.item;

import aspadien.universalcoins.util.UniversalAccounts;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class ItemUCCard extends Item {
    public ItemUCCard(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tooltip.add(Component.literal(tag.getString("Name")).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal(tag.getString("Account")).withStyle(ChatFormatting.AQUA));
            }
        } else {
            tooltip.add(Component.translatable("item.card.warning").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (world.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        if (!stack.hasTag()) {
            createNBT(stack, world, player);
        }

        String accountId = stack.getTag() != null ? stack.getTag().getString("Account") : "";
        long accountCoins = UniversalAccounts.getInstance().getAccountBalance(accountId);

        DecimalFormat formatter = new DecimalFormat("###,###,###,###,###,###,###");

        Component msg = Component.translatable("item.card.balance")
                .append(Component.literal(formatter.format(accountCoins)))
                .withStyle(ChatFormatting.GOLD);

        player.sendSystemMessage(msg);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        if (!world.isClientSide) {
            createNBT(stack, world, player);
        }
    }

    protected void createNBT(ItemStack stack, Level world, Player player) {
        String accountNumber = UniversalAccounts.getInstance().getOrCreatePlayerAccount(player.getUUID().toString());

        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("Name", player.getName().getString());
        tag.putString("Owner", player.getUUID().toString());
        tag.putString("Account", accountNumber);
    }
}