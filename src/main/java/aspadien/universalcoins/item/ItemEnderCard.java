 package aspadien.universalcoins.item;
 import java.text.DecimalFormat;
 import net.minecraft.entity.player.PlayerEntity;
 import net.minecraft.item.Item;
 import net.minecraft.item.ItemStack;
 import net.minecraft.item.ItemUseContext;
 import net.minecraft.util.ActionResultType;
 import net.minecraft.util.NonNullList;
 import net.minecraft.util.Util;
 import net.minecraft.util.text.*;



 import net.minecraft.world.World;
 import aspadien.universalcoins.util.UniversalAccounts;

 public class ItemEnderCard extends aspadien.universalcoins.item.ItemUCCard {
   public ItemEnderCard(Item.Properties properties) {
     super(properties);
   }


   public ActionResultType useOn(ItemUseContext context) {
     World world = context.getLevel();
     PlayerEntity player = context.getPlayer();
     ItemStack heldItem = context.getItemInHand();


     if (world.isClientSide || player == null) {
       return ActionResultType.SUCCESS;
     }


     if (!heldItem.hasTag()) {
       createNBT(heldItem, world, player);
     }

     DecimalFormat formatter = new DecimalFormat("#,###,###,###");
     NonNullList<ItemStack> inventory = player.inventory.items;
     String accountNumber = heldItem.getTag().getString("Account");


     long accountBalance = UniversalAccounts.getInstance().getAccountBalance(accountNumber);


     if (accountBalance == -1L) {
       return ActionResultType.SUCCESS;
     }

     long coinsDeposited = 0L;


     for (int i = 0; i < inventory.size(); i++) {
       ItemStack instack = inventory.get(i);


       int coinValue = aspadien.universalcoins.util.CoinUtils.getCoinValue(instack);

       if (coinValue > 0 && !instack.isEmpty()) {



         int depositAmount = 0;
         long currentBalance = UniversalAccounts.getInstance().getAccountBalance(accountNumber);
         long spaceOnAccount = Long.MAX_VALUE - currentBalance;


         if (spaceOnAccount > coinValue * instack.getCount()) {
           depositAmount = instack.getCount();
         } else {
           depositAmount = (int)(spaceOnAccount / coinValue);
         }

         if (depositAmount > 0) {

           UniversalAccounts.getInstance().creditAccount(accountNumber, coinValue * depositAmount, false);
           coinsDeposited += coinValue * depositAmount;


           instack.shrink(depositAmount);
         }
       }
     }

     if (coinsDeposited > 0L) {

       IFormattableTextComponent iFormattableTextComponent1 = (new TranslationTextComponent("item.card.deposit")).append(new StringTextComponent(" " + formatter.format(coinsDeposited) + " ")).append((ITextComponent)new TranslationTextComponent((coinsDeposited > 1L) ? "general.currency.multiple" : "general.currency.single")).withStyle(TextFormatting.GREEN);

       player.sendMessage(iFormattableTextComponent1, Util.NIL_UUID);
     }


     long finalBalance = UniversalAccounts.getInstance().getAccountBalance(accountNumber);


     IFormattableTextComponent iFormattableTextComponent = (new TranslationTextComponent("item.card.balance")).append(new StringTextComponent(" " + formatter.format(finalBalance))).withStyle(TextFormatting.GOLD);

     player.sendMessage(iFormattableTextComponent, Util.NIL_UUID);

     return ActionResultType.SUCCESS;
   }
 }

