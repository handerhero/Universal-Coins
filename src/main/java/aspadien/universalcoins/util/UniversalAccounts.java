 package aspadien.universalcoins.util;

 import net.minecraft.nbt.CompoundNBT;

 public class UniversalAccounts
 {
/*   7 */   private static final UniversalAccounts instance = new UniversalAccounts();

   public static UniversalAccounts getInstance() {
/*  10 */     return instance;
   }


   public long getAccountBalance(String accountNumber) {
/*  18 */     if (hasKey(accountNumber)) {
/*  19 */       return getWorldLong(accountNumber);
     }
/*  21 */     return -1L;
   }

   public boolean debitAccount(String accountNumber, long amount, boolean simulate) {
/*  25 */     if (hasKey(accountNumber)) {
/*  26 */       long balance = getWorldLong(accountNumber);
/*  27 */       if (amount <= balance) {
/*  28 */         if (!simulate) {
/*  29 */           balance -= amount;
/*  30 */           setWorldData(accountNumber, balance);
         }
/*  32 */         return true;
       }
     }
/*  35 */     return false;
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
                             net.minecraft.server.MinecraftServer server = net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer();
                             if (server != null) {
                                 net.minecraft.entity.player.ServerPlayerEntity player = server.getPlayerList().getPlayer(uuid);
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
/*  98 */     return (int)(Math.floor(Math.random() * 9.9999999E7D) + 1.1111111E7D);
   }

   private boolean hasKey(String tag) {
/* 102 */     UCWorldData wData = UCWorldData.getInstance();
/* 103 */     CompoundNBT wdTag = wData.getData();
/* 104 */     return wdTag.contains(tag);
   }

   private void setWorldData(String tag, String data) {
/* 108 */     UCWorldData wData = UCWorldData.getInstance();
/* 109 */     CompoundNBT wdTag = wData.getData();
/* 110 */     wdTag.putString(tag, data);
/* 111 */     wData.markDirty();
   }

   private void setWorldData(String tag, long data) {
/* 115 */     UCWorldData wData = UCWorldData.getInstance();
/* 116 */     CompoundNBT wdTag = wData.getData();
/* 117 */     wdTag.putLong(tag, data);
/* 118 */     wData.markDirty();
   }

   private long getWorldLong(String tag) {
/* 122 */     UCWorldData wData = UCWorldData.getInstance();
/* 123 */     CompoundNBT wdTag = wData.getData();
/* 124 */     return wdTag.getLong(tag);
   }

   private String getWorldString(String tag) {
/* 128 */     UCWorldData wData = UCWorldData.getInstance();
/* 129 */     CompoundNBT wdTag = wData.getData();
/* 130 */     return wdTag.getString(tag);
   }

   private void delWorldData(String tag) {
/* 134 */     UCWorldData wData = UCWorldData.getInstance();
/* 135 */     CompoundNBT wdTag = wData.getData();
/* 136 */     wdTag.remove(tag);
/* 137 */     wData.markDirty();
   }
 }