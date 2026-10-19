package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerATM;
import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.net.ATMWithdrawalMessage;
import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UniversalAccounts;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ATM extends Protected implements Container, MenuProvider {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    public static final int itemCoinSlot = 0;
    public static final int itemCardSlot = 1;

    public String playerUID = "";
    public String cardOwner = "";
    public boolean depositCoins = false;
    public boolean withdrawCoins = false;
    public boolean accountError = false;
    public int coinWithdrawalAmount = 0;
    public String accountNumber = "none";
    public long accountBalance = 0L;

    private final ContainerData atmData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return ATM.this.inUse ? 1 : 0;
                case 1: return ATM.this.depositCoins ? 1 : 0;
                case 2: return ATM.this.withdrawCoins ? 1 : 0;
                case 3: return ATM.this.coinWithdrawalAmount;
                case 4: return (int) (ATM.this.accountBalance & 0xFFFFFFFFL);
                case 5: return (int) (ATM.this.accountBalance >> 32L);
                case 6: return ATM.this.accountError ? 1 : 0;
                case 7: return (ATM.this.playerUID != null && ATM.this.playerUID.equals(ATM.this.cardOwner)) ? 1 : 0;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: ATM.this.inUse = (value == 1); break;
                case 1: ATM.this.depositCoins = (value == 1); break;
                case 2: ATM.this.withdrawCoins = (value == 1); break;
                case 3: ATM.this.coinWithdrawalAmount = value; break;
                case 4: ATM.this.accountBalance = (ATM.this.accountBalance & 0xFFFFFFFF00000000L) | (value & 0xFFFFFFFFL); break;
                case 5: ATM.this.accountBalance = (ATM.this.accountBalance & 0xFFFFFFFFL) | ((long) value << 32L); break;
                case 6: ATM.this.accountError = (value == 1); break;
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    public ATM(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATM.get(), pos, state);
    }

    public void tick() {
        if (this.level == null || this.level.isClientSide) return;

        if (this.inUse) {
            if (this.depositCoins && !getItem(0).isEmpty()) {
                processDeposit();
            }

            if (this.withdrawCoins && this.coinWithdrawalAmount > 0 && getItem(0).isEmpty()) {
                fillCoinSlot();
            }

            if (this.level.getGameTime() % 10L == 0L && !this.accountNumber.equals("none")) {
                long realBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
                if (this.accountBalance != realBalance) {
                    this.accountBalance = realBalance;
                    setChanged();
                }
            }
        }
    }

    private void processDeposit() {
        ItemStack stack = getItem(0);
        int coinValue = CoinUtils.getCoinValue(stack);
        if (coinValue > 0 && !this.accountNumber.equals("none")) {
            long space = Long.MAX_VALUE - this.accountBalance;
            int depositAmount = (int) Math.min(stack.getCount(), space / coinValue);

            if (depositAmount > 0) {
                UniversalAccounts.getInstance().creditAccount(this.accountNumber, (long) depositAmount * coinValue, false);
                this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);

                stack.shrink(depositAmount);
                if (stack.isEmpty()) {
                    this.inventory.set(0, ItemStack.EMPTY);
                }

                setChanged();
                updateTE();
            }
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        if (this.level == null || this.level.isClientSide) return;

        if (index == 1) {
            if (!stack.isEmpty() && stack.hasTag()) {
                CompoundTag tag = stack.getTag();
                if (tag != null) {
                    this.accountNumber = tag.getString("Account");
                    this.cardOwner = tag.getString("Owner");
                    this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
                }
            } else {
                String personalAcc = UniversalAccounts.getInstance().getPlayerAccount(this.playerUID);
                if (!personalAcc.isEmpty()) {
                    this.accountNumber = personalAcc;
                    this.cardOwner = this.playerUID;
                    this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
                } else {
                    this.accountNumber = "none";
                    this.cardOwner = "";
                    this.accountBalance = 0L;
                }
                this.coinWithdrawalAmount = 0;
            }
            setChanged();
            updateTE();
        }
    }

    public void sendServerUpdatePacket(int withdrawalAmount) {
        ModPacketHandler.sendToServer(new ATMWithdrawalMessage(this.worldPosition, withdrawalAmount));
    }

    public void startCoinWithdrawal(int amount) {
        if (this.level == null || this.level.isClientSide) return;
        this.accountError = false;

        if (UniversalAccounts.getInstance().debitAccount(this.accountNumber, amount, true)) {
            this.coinWithdrawalAmount = amount;
            fillCoinSlot();
        } else {
            this.accountError = true;
        }
        setChanged();
    }

    @Override
    public void inUseCleanup() {
        if (this.level == null || this.level.isClientSide) return;
        this.inUse = false;
        this.withdrawCoins = false;
        this.depositCoins = false;
        this.coinWithdrawalAmount = 0;
        this.accountNumber = "none";
        this.accountBalance = 0L;
        updateTE();
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = ContainerHelper.removeItem(this.inventory, index, count);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.inventory, index);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.cardOwner = tag.getString("cardOwner");
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inventory);
        this.playerUID = tag.getString("playerUID");
        this.depositCoins = tag.getBoolean("depositCoins");
        this.withdrawCoins = tag.getBoolean("withdrawCoins");
        this.coinWithdrawalAmount = tag.getInt("coinWithdrawalAmount");
        this.accountNumber = tag.getString("accountNumber");
        this.accountBalance = tag.getLong("accountBalance");
        this.accountError = tag.getBoolean("accountError");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory);
        tag.putString("cardOwner", this.cardOwner);
        tag.putString("playerUID", this.playerUID);
        tag.putBoolean("depositCoins", this.depositCoins);
        tag.putBoolean("withdrawCoins", this.withdrawCoins);
        tag.putInt("coinWithdrawalAmount", this.coinWithdrawalAmount);
        tag.putString("accountNumber", this.accountNumber);
        tag.putLong("accountBalance", this.accountBalance);
        tag.putBoolean("accountError", this.accountError);
    }

    public void onButtonPressed(int functionId) {
        if (this.level == null || this.level.isClientSide) return;
        UniversalAccounts accounts = UniversalAccounts.getInstance();
        this.accountError = false;

        switch (functionId) {
            case 1:
                this.accountNumber = accounts.getOrCreatePlayerAccount(this.playerUID);
                long currentBal = accounts.getAccountBalance(this.accountNumber);
                ItemStack currentStack = getItem(1);

                Item targetCardItem = ModItems.UC_CARD.get();
                if (!currentStack.isEmpty() && currentStack.getItem() == ModItems.ENDER_CARD.get()) {
                    targetCardItem = ModItems.ENDER_CARD.get();
                }

                ItemStack newCard = new ItemStack(targetCardItem);
                CompoundTag nbt = newCard.getOrCreateTag();
                nbt.putString("Name", this.playerName);
                nbt.putString("Owner", this.playerUID);
                nbt.putString("Account", this.accountNumber);

                this.inventory.set(1, newCard);
                this.accountBalance = currentBal;
                this.cardOwner = this.playerUID;
                break;

            case 2:
                String oldAccount = accounts.getPlayerAccount(this.playerUID);
                if (!oldAccount.isEmpty()) {
                    accounts.transferPlayerAccount(this.playerUID);
                    String newAccountNumber = accounts.getPlayerAccount(this.playerUID);
                    long balance = accounts.getAccountBalance(newAccountNumber);

                    ItemStack itemStack1 = getItem(1);
                    Item item = ModItems.UC_CARD.get();
                    if (!itemStack1.isEmpty() && itemStack1.getItem() == ModItems.ENDER_CARD.get()) {
                        item = ModItems.ENDER_CARD.get();
                    }

                    ItemStack transferCard = new ItemStack(item);
                    CompoundTag tNbt = transferCard.getOrCreateTag();
                    tNbt.putString("Name", this.playerName);
                    tNbt.putString("Owner", this.playerUID);
                    tNbt.putString("Account", newAccountNumber);

                    this.inventory.set(1, transferCard);
                    this.accountNumber = newAccountNumber;
                    this.accountBalance = balance;
                    this.cardOwner = this.playerUID;
                }
                break;

            case 3:
                this.depositCoins = true;
                this.withdrawCoins = false;
                if (this.accountNumber.equals("none") && !getItem(1).isEmpty() && getItem(1).hasTag()) {
                    this.accountNumber = getItem(1).getTag().getString("Account");
                }
                break;

            case 4:
                this.withdrawCoins = true;
                this.depositCoins = false;
                fillCoinSlot();
                break;

            case 5:
                String storedAcc = accounts.getPlayerAccount(this.playerUID);
                if (!storedAcc.isEmpty()) {
                    this.accountNumber = storedAcc;
                    this.accountBalance = accounts.getAccountBalance(this.accountNumber);
                    this.cardOwner = this.playerUID;
                }
                break;

            case 6:
                this.inventory.set(1, ItemStack.EMPTY);
                inUseCleanup();
                break;
        }

        updateTE();
        setChanged();
    }

    public void fillCoinSlot() {
        if (this.level == null || this.level.isClientSide || this.accountNumber.equals("none")) return;

        if (getItem(0).isEmpty() && this.coinWithdrawalAmount > 0) {
            for (int i = UniversalCoins.coinValues.length - 1; i >= 0; i--) {
                int coinVal = UniversalCoins.coinValues[i];

                if (this.coinWithdrawalAmount >= coinVal) {
                    int count = Math.min(this.coinWithdrawalAmount / coinVal, 64);
                    int totalToDebit = count * coinVal;

                    if (UniversalAccounts.getInstance().debitAccount(this.accountNumber, totalToDebit, false)) {
                        ItemStack payoutStack = new ItemStack(ModItems.getCoinByIndex(i), count);
                        this.inventory.set(0, payoutStack);

                        this.coinWithdrawalAmount -= totalToDebit;
                        this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);

                        setChanged();
                        updateTE();
                        break;
                    }
                }
            }
        }
        if (this.coinWithdrawalAmount <= 0) {
            this.withdrawCoins = false;
        }
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.universalcoins.atm");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        refreshDataOnOpen();
        return new ContainerATM(windowId, playerInv, this, this.atmData, this);
    }

    private void refreshDataOnOpen() {
        ItemStack card = getItem(1);
        if (!card.isEmpty() && card.hasTag()) {
            this.accountNumber = card.getTag().getString("Account");
            this.cardOwner = card.getTag().getString("Owner");
        } else {
            String personalAcc = UniversalAccounts.getInstance().getPlayerAccount(this.playerUID);
            if (!personalAcc.isEmpty()) {
                this.accountNumber = personalAcc;
                this.cardOwner = this.playerUID;
            } else {
                this.accountNumber = "none";
                this.cardOwner = "";
            }
        }
        this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
        setChanged();
    }
}