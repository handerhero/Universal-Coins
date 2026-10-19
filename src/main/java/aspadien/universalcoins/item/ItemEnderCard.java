package aspadien.universalcoins.item;

import aspadien.universalcoins.util.UniversalAccounts;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.text.DecimalFormat;

public class ItemEnderCard extends ItemUCCard {
    public ItemEnderCard(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack heldItem = context.getItemInHand();

        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        if (!heldItem.hasTag()) {
            createNBT(heldItem, level, player);
        }

        DecimalFormat formatter = new DecimalFormat("#,###,###,###");
        NonNullList<ItemStack> inventory = player.getInventory().items;
        String accountNumber = heldItem.getTag() != null ? heldItem.getTag().getString("Account") : "";

        long accountBalance = UniversalAccounts.getInstance().getAccountBalance(accountNumber);
        if (accountBalance == -1L) {
            return InteractionResult.SUCCESS;
        }

        long coinsDeposited = 0L;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack instack = inventory.get(i);
            int coinValue = aspadien.universalcoins.util.CoinUtils.getCoinValue(instack);

            if (coinValue > 0 && !instack.isEmpty()) {
                int depositAmount;
                long currentBalance = UniversalAccounts.getInstance().getAccountBalance(accountNumber);
                long spaceOnAccount = Long.MAX_VALUE - currentBalance;

                if (spaceOnAccount > (long) coinValue * instack.getCount()) {
                    depositAmount = instack.getCount();
                } else {
                    depositAmount = (int) (spaceOnAccount / coinValue);
                }

                if (depositAmount > 0) {
                    UniversalAccounts.getInstance().creditAccount(accountNumber, (long) coinValue * depositAmount, false);
                    coinsDeposited += (long) coinValue * depositAmount;
                    instack.shrink(depositAmount);
                }
            }
        }

        if (coinsDeposited > 0L) {
            Component depositMsg = Component.translatable("item.card.deposit")
                    .append(Component.literal(" " + formatter.format(coinsDeposited) + " "))
                    .append(Component.translatable((coinsDeposited > 1L) ? "general.currency.multiple" : "general.currency.single"))
                    .withStyle(ChatFormatting.GREEN);

            player.sendSystemMessage(depositMsg);
        }

        long finalBalance = UniversalAccounts.getInstance().getAccountBalance(accountNumber);

        Component balanceMsg = Component.translatable("item.card.balance")
                .append(Component.literal(" " + formatter.format(finalBalance)))
                .withStyle(ChatFormatting.GOLD);

        player.sendSystemMessage(balanceMsg);

        return InteractionResult.SUCCESS;
    }
}
