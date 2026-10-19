package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerSafe;
import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.init.ModItems;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class Safe extends Protected implements MenuProvider {
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public String currentSessionAccount = "none";
    public long accountBalance = 0L;
    public String blockOwner = "nobody";

    protected final ContainerData safeData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return (int) (Safe.this.accountBalance & 0xFFFFFFFFL);
                case 1: return (int) (Safe.this.accountBalance >> 32L);
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) Safe.this.accountBalance = (Safe.this.accountBalance & 0xFFFFFFFF00000000L) | (value & 0xFFFFFFFFL);
            if (index == 1) Safe.this.accountBalance = (Safe.this.accountBalance & 0xFFFFFFFFL) | ((long) value << 32L);
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public Safe(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SAFE.get(), pos, state);
    }

    public void tick() {
        if (this.level == null || this.level.isClientSide) return;

        if (this.currentSessionAccount.equals("none")) {
            if (!this.items.get(1).isEmpty()) {
                this.items.set(1, ItemStack.EMPTY);
                setChanged();
            }
            return;
        }

        ItemStack inputStack = this.items.get(0);
        if (!inputStack.isEmpty()) {
            int coinValue = CoinUtils.getCoinValue(inputStack);
            if (coinValue > 0) {
                long totalToDeposit = (long) coinValue * inputStack.getCount();
                if (Long.MAX_VALUE - this.accountBalance >= totalToDeposit &&
                        UniversalAccounts.getInstance().creditAccount(this.currentSessionAccount, totalToDeposit, false)) {
                    inputStack.setCount(0);
                    this.items.set(0, ItemStack.EMPTY);
                    refreshAccountData();
                    setChanged();
                }
            }
        }

        if (this.items.get(1).isEmpty() && this.accountBalance > 0L) {
            fillOutputSlot();
        }

        if (this.level.getGameTime() % 20L == 0L) {
            refreshAccountData();
        }
    }

    public void fillOutputSlot() {
        if (this.accountBalance <= 0L || !this.items.get(1).isEmpty()) return;

        for (int i = 5; i >= 0; i--) {
            int coinVal = UniversalCoins.coinValues[i];
            if (this.accountBalance >= coinVal) {
                this.items.set(1, new ItemStack(ModItems.getCoinByIndex(i), 1));
                setChanged();
                break;
            }
        }
    }

    public void refreshAccountData() {
        if (this.level != null && !this.level.isClientSide && !this.currentSessionAccount.equals("none")) {
            this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.currentSessionAccount);
            setChanged();
        }
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void inUseCleanup() {
        if (this.level == null || this.level.isClientSide) return;
        this.currentSessionAccount = "none";
        this.accountBalance = 0L;
        this.items.set(1, ItemStack.EMPTY);
        this.inUse = false;
        setChanged();
        updateTE();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        this.currentSessionAccount = UniversalAccounts.getInstance().getOrCreatePlayerAccount(player.getUUID().toString());
        refreshAccountData();
        this.inUse = true;

        Container proxy = new Container() {
            @Override
            public int getContainerSize() {
                return 2;
            }

            @Override
            public boolean isEmpty() {
                return Safe.this.items.stream().allMatch(ItemStack::isEmpty);
            }

            @Override
            public ItemStack getItem(int i) {
                return Safe.this.items.get(i);
            }

            @Override
            public ItemStack removeItem(int i, int c) {
                ItemStack s = ContainerHelper.removeItem(Safe.this.items, i, c);
                setChanged();
                return s;
            }

            @Override
            public ItemStack removeItemNoUpdate(int i) {
                return ContainerHelper.takeItem(Safe.this.items, i);
            }

            @Override
            public void setItem(int i, ItemStack s) {
                Safe.this.items.set(i, s);
                setChanged();
            }

            @Override
            public void setChanged() {
                Safe.this.setChanged();
            }

            @Override
            public boolean stillValid(Player p) {
                return Safe.this.stillValid(p);
            }

            @Override
            public void clearContent() {
                Safe.this.items.clear();
                setChanged();
            }
        };

        return new ContainerSafe(id, playerInv, proxy, this.safeData, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
        if (tag.contains("blockOwner")) {
            this.blockOwner = tag.getString("blockOwner");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putString("blockOwner", this.blockOwner);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.universalcoins.safe");
    }
}