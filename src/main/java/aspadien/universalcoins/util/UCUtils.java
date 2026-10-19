 package aspadien.universalcoins.util;

 import net.minecraft.inventory.IInventory;
 import net.minecraft.item.ItemStack;
 import net.minecraft.nbt.CompoundNBT;
 import net.minecraft.util.NonNullList;
 import aspadien.universalcoins.UniversalCoins;
 import aspadien.universalcoins.init.ModItems;
 import aspadien.universalcoins.item.ItemCoin;

import java.util.Objects;





 public class UCUtils
 {
   public static boolean isEnderCard(ItemStack stack) {
    return (!stack.isEmpty() && Objects.requireNonNull(stack.getItem().getRegistryName()).toString().contains("ender_card"));
   }

   public static boolean isSameItem(ItemStack stackA, ItemStack stackB) {
    if (stackA.isEmpty() && stackB.isEmpty()) return true;
    if (stackA.isEmpty() || stackB.isEmpty()) return false;
    if (stackA.getItem() != stackB.getItem()) return false;

    if (stackA.getDamageValue() != stackB.getDamageValue()) return false;

    CompoundNBT tagA = stackA.getTag();
    CompoundNBT tagB = stackB.getTag();

    if (tagA == null || tagA.isEmpty()) {
      return (tagB == null || tagB.isEmpty());
     }
    return tagA.equals(tagB);
   }


   public static long getCardBalance(ItemStack card) {
    if (card.isEmpty() || !card.hasTag()) return 0L;
    String acc = card.getTag().getString("Account");
    return UniversalAccounts.getInstance().getAccountBalance(acc);
   }


   public static boolean debitCard(ItemStack card, long amount) {
    if (card.isEmpty() || !card.hasTag()) return false;
    String acc = card.getTag().getString("Account");
    if (acc.isEmpty()) return false;
    return UniversalAccounts.getInstance().debitAccount(acc, amount, false);
   }


   public static boolean creditCard(ItemStack card, long amount) {
    if (card.isEmpty() || !card.hasTag()) return false;
    String acc = card.getTag().getString("Account");
    if (acc.isEmpty()) return false;
    return UniversalAccounts.getInstance().creditAccount(acc, amount, false);
   }


   public static long withdrawCoins(long pool, IInventory inv, int slot, int coinIdx, boolean shift) {
    int val = UniversalCoins.coinValues[coinIdx];
    if (pool < val) return pool;
    ItemStack out = inv.getItem(slot);
    ItemCoin itemCoin = ModItems.getCoinByIndex(coinIdx);
    if (out.isEmpty() || (out.getItem() == itemCoin && out.getCount() < 64)) {
      int count = shift ? (int)Math.min(pool / val, (64 - out.getCount())) : 1;
      if (count <= 0) return pool;
      if (out.isEmpty()) { inv.setItem(slot, new ItemStack(itemCoin, count)); }
      else { out.grow(count); }
       return pool - (long) count * val;
     }
    return pool;
   }


   public static boolean hasStock(NonNullList<ItemStack> inv, ItemStack target, int needed) {
    int found = 0;
    for (int i = 0; i <= 8; i++) {
      ItemStack stackInSlot = inv.get(i);
      if (!stackInSlot.isEmpty() && isSameItem(stackInSlot, target)) {
        found += stackInSlot.getCount();
       }
     }
     return (found >= needed);
   }


   public static void decreaseStock(NonNullList<ItemStack> inv, ItemStack target, int amount) {
    int remaining = amount;
    for (int i = 0; i <= 8; i++) {
      ItemStack stack = inv.get(i);
      if (!stack.isEmpty() && isSameItem(stack, target)) {
        int take = Math.min(stack.getCount(), remaining);
        stack.shrink(take);
        remaining -= take;

        if (stack.isEmpty()) {
          inv.set(i, ItemStack.EMPTY);
         }
       }
      if (remaining <= 0) {
         break;
       }
     }
   }


   public static void storeItems(NonNullList<ItemStack> inv, ItemStack stack) {
    for (int i = 0; i <= 8; i++) {
      if (inv.get(i).isEmpty()) {
        inv.set(i, stack.copy());
        stack.setCount(0); return;
       }
      if (isSameItem(inv.get(i), stack)) {
        int canAdd = Math.min(stack.getCount(), inv.get(i).getMaxStackSize() - inv.get(i).getCount());
        inv.get(i).grow(canAdd);
        stack.shrink(canAdd);
       }
      if (stack.isEmpty())
         break;
     }
   }
   public static boolean isCard(ItemStack itemstack1) {
    return (itemstack1.getItem() == ModItems.UC_CARD.get() || itemstack1.getItem() == ModItems.ENDER_CARD.get());
   }
 }
