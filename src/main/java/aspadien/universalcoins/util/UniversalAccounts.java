 package aspadien.universalcoins.util;


 import net.minecraft.nbt.CompoundTag;
 import net.minecraft.server.MinecraftServer;
 import net.minecraft.server.level.ServerPlayer;

 import static net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer;

 public class UniversalAccounts
 {
   private static final UniversalAccounts instance = new UniversalAccounts();

   public static UniversalAccounts getInstance() {
/*  10 */     return instance;
   }


   public long getAccountBalance(String accountNumber) {
     if (hasKey(accountNumber)) {
       return getWorldLong(accountNumber);
     }
     return -1L;
   }

   public boolean debitAccount(String accountNumber, long amount, boolean simulate) {
     if (hasKey(accountNumber)) {
       long balance = getWorldLong(accountNumber);
       if (amount <= balance) {
         if (!simulate) {
           balance -= amount;
           setWorldData(accountNumber, balance);
         }
         return true;
       }
     }
     return false;
   }

     public boolean creditAccount(String accountNumber, long amount, boolean simulate) {
         if (hasKey(accountNumber)) {
             long balance = getWorldLong(accountNumber);
             if (Long.MAX_VALUE - balance >= amount) {
                 if (!simulate) {
                     balance += amount;
                     setWorldData(accountNumber, balance);

                     // >>> ИЗВЛЕКАЕМ UUID И ТРИГГЕРИМ АЧИВКУ <<<
                     String ownerUID = getAccountOwner(accountNumber);
                     if (!ownerUID.isEmpty()) {
                         try {
                             java.util.UUID uuid = java.util.UUID.fromString(ownerUID);
                             MinecraftServer server = getCurrentServer();
                             if (server != null) {
                                 ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                                 // Если игрок сейчас на сервере (онлайн)
                                 if (player != null) {
                                     aspadien.universalcoins.net.UCAccountBalanceTrigger.trigger(player, balance);
                                 }
                             }
                         } catch (IllegalArgumentException ignored) {}
                     }
                 }
                 return true;
             }
         }
         return false;
     }


   public String getPlayerAccount(String playerUID) {
/*  54 */     return getWorldString(playerUID);
   }

     public String getOrCreatePlayerAccount(String playerUID) {
         String accountNumber = getWorldString(playerUID);
         if (accountNumber.isEmpty()) {
             while (!hasKey(playerUID)) {
                 accountNumber = String.valueOf(generateAccountNumber());
                 if (getWorldString(accountNumber).isEmpty()) {
                     setWorldData(playerUID, accountNumber);
                     setWorldData(accountNumber, 0L);

                     // >>> ЗАПИСЫВАЕМ ВЛАДЕЛЬЦА <<<
                     setWorldData(accountNumber + "_owner", playerUID);
                 }
             }
         }
         return accountNumber;
     }

     public boolean addPlayerAccount(String playerUID) {
         if (getWorldString(playerUID).isEmpty())
             while (true) {
                 String accountNumber = String.valueOf(generateAccountNumber());
                 if (!hasKey(accountNumber)) {
                     setWorldData(playerUID, accountNumber);
                     setWorldData(accountNumber, 0L);

                     // >>> ЗАПИСЫВАЕМ ВЛАДЕЛЬЦА <<<
                     setWorldData(accountNumber + "_owner", playerUID);
                     return true;
                 }
             }
         return false;
     }

     // Получить UUID владельца по номеру счёта
     public String getAccountOwner(String accountNumber) {
         return getWorldString(accountNumber + "_owner");
     }

     public void transferPlayerAccount(String playerUID) {
         String oldAccount = getWorldString(playerUID);
         long oldBalance = getAccountBalance(oldAccount);

         delWorldData(playerUID);
         delWorldData(oldAccount);
         delWorldData(oldAccount + "_owner"); // Удаляем привязку старого счёта

         String accountNumber = oldAccount;
         while (true) {
             accountNumber = String.valueOf(generateAccountNumber());
             if (!hasKey(accountNumber)) {
                 setWorldData(playerUID, accountNumber);
                 setWorldData(accountNumber, oldBalance);

                 // >>> ПЕРЕПИСЫВАЕМ НА НОВЫЙ СЧЁТ <<<
                 setWorldData(accountNumber + "_owner", playerUID);
                 return;
             }
         }
     }
     private int generateAccountNumber() {
     return (int)(Math.floor(Math.random() * 9.9999999E7D) + 1.1111111E7D);
   }

   private boolean hasKey(String tag) {
     UCWorldData wData = UCWorldData.getInstance();
     CompoundTag wdTag = wData.getData();
     return wdTag.contains(tag);
   }

   private void setWorldData(String tag, String data) {
     UCWorldData wData = UCWorldData.getInstance();
     CompoundTag wdTag = wData.getData();
     wdTag.putString(tag, data);
     wData.markDirty();
   }

   private void setWorldData(String tag, long data) {
     UCWorldData wData = UCWorldData.getInstance();
     CompoundTag wdTag = wData.getData();
     wdTag.putLong(tag, data);
     wData.markDirty();
   }

   private long getWorldLong(String tag) {
     UCWorldData wData = UCWorldData.getInstance();
     CompoundTag wdTag = wData.getData();
     return wdTag.getLong(tag);
   }

   private String getWorldString(String tag) {
     UCWorldData wData = UCWorldData.getInstance();
     CompoundTag wdTag = wData.getData();
     return wdTag.getString(tag);
   }

   private void delWorldData(String tag) {
     UCWorldData wData = UCWorldData.getInstance();
     CompoundTag wdTag = wData.getData();
     wdTag.remove(tag);
     wData.markDirty();
   }
 }