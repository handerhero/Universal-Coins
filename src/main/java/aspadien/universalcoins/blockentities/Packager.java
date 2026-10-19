package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerPackager;
import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.net.UCPackagerServerMessage;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UCUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class Packager extends Protected implements Container, MenuProvider {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(12, ItemStack.EMPTY);

    public static final int[] itemPackageSlots = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
    public static final int itemCardSlot = 8;
    public static final int itemCoinSlot = 9;
    public static final int itemOutputSlot = 10;
    public static final int itemPackageInputSlot = 11;

    public long coinSum = 0L;
    public int packageSize = 0;
    public boolean sendMode = false;
    public boolean buyButtonActive = false;
    public boolean coinButtonActive = false;
    public String packageTarget = "";
    public int[] packageCost = new int[] { 10, 20, 40 };

    protected final ContainerData packagerData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return (int) (Packager.this.coinSum & 0xFFFFFFFFL);
                case 1: return (int) (Packager.this.coinSum >> 32L);
                case 2: return Packager.this.packageSize;
                case 3: return Packager.this.sendMode ? 1 : 0;
                case 4: return Packager.this.buyButtonActive ? 1 : 0;
                case 5: return Packager.this.coinButtonActive ? 1 : 0;
                case 6: return Packager.this.packageCost[0];
                case 7: return Packager.this.packageCost[1];
                case 8: return Packager.this.packageCost[2];
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: Packager.this.coinSum = (Packager.this.coinSum & 0xFFFFFFFF00000000L) | (value & 0xFFFFFFFFL); break;
                case 1: Packager.this.coinSum = (Packager.this.coinSum & 0xFFFFFFFFL) | ((long) value << 32L); break;
                case 2: Packager.this.packageSize = value; break;
                case 3: Packager.this.sendMode = (value == 1); break;
                case 4: Packager.this.buyButtonActive = (value == 1); break;
                case 5: Packager.this.coinButtonActive = (value == 1); break;
            }
        }

        @Override
        public int getCount() {
            return 9;
        }
    };

    public Packager(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PACKAGER.get(), pos, state);
        this.packageCost[0] = UniversalCoins.smallPackagePrice;
        this.packageCost[1] = UniversalCoins.medPackagePrice;
        this.packageCost[2] = UniversalCoins.largePackagePrice;
    }

    public void tick() {
        if (this.level == null || this.level.isClientSide) return;
        absorbCoins();
        updateButtonStates();
        if (this.level.getGameTime() % 10L == 0L) setChanged();
    }

    private void absorbCoins() {
        ItemStack coinStack = getItem(9);
        int val = CoinUtils.getCoinValue(coinStack);
        if (val > 0) {
            this.coinSum += (long) val * coinStack.getCount();
            coinStack.setCount(0);
            setItem(9, ItemStack.EMPTY);
            setChanged();
            updateTE();
        }
    }

    private void updateButtonStates() {
        this.coinButtonActive = (this.coinSum > 0L);
        if (!this.sendMode) {
            long funds = UCUtils.getCardBalance(getItem(8));
            if (funds < 0L) funds = 0L;
            funds += this.coinSum;
            this.buyButtonActive = (funds >= this.packageCost[this.packageSize] && getItem(10).isEmpty());
        } else {
            this.buyButtonActive = (!getItem(11).isEmpty() && !this.packageTarget.isEmpty());
        }
    }

    public void onButtonPressed(int id, boolean shift) {
        if (this.level == null || this.level.isClientSide) return;

        if (id == 0) {
            if (this.sendMode) {
                sendPackage(this.packageTarget);
            } else {
                createPackage();
            }
        } else if (id == 1) {
            fillOutputSlot();
        } else if (id >= 2 && id <= 4) {
            int newSize = id - 2;
            if (newSize < this.packageSize) {
                ejectRedundantSlots(newSize);
            }
            this.packageSize = newSize;
        } else if (id == 5) {
            this.sendMode = !this.sendMode;
            if (this.sendMode) {
                ejectRedundantSlots(-1);
            }
        }

        setChanged();
        updateTE();
    }

    private void ejectRedundantSlots(int newSize) {
        if (this.level == null || this.level.isClientSide || this.level.getServer() == null) {
            return;
        }
        int firstVisibleSlot = (newSize == 0) ? 4 : ((newSize == 1) ? 2 : ((newSize == 2) ? 0 : 8));

        for (int i = 0; i < firstVisibleSlot; i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                ServerPlayer serverPlayer = this.level.getServer().getPlayerList().getPlayerByName(this.playerName);

                if (serverPlayer != null && serverPlayer.getInventory().add(stack.copy())) {
                    stack.setCount(0);
                } else {
                    ItemEntity itemEntity = new ItemEntity(this.level,
                            this.worldPosition.getX() + 0.5D,
                            this.worldPosition.getY() + 1.0D,
                            this.worldPosition.getZ() + 0.5D,
                            stack.copy());
                    this.level.addFreshEntity(itemEntity);
                    stack.setCount(0);
                }
                this.inventory.set(i, ItemStack.EMPTY);
            }
        }
        setChanged();
        updateTE();
    }

    private void createPackage() {
        if (!getItem(10).isEmpty()) return;
        int cost = this.packageCost[this.packageSize];
        if (!pay(cost)) return;

        ListTag list = new ListTag();
        int startSlot = (this.packageSize == 0) ? 4 : ((this.packageSize == 1) ? 2 : 0);

        for (int i = startSlot; i < 8; i++) {
            ItemStack s = getItem(i);
            if (!s.isEmpty()) {
                list.add(s.save(new CompoundTag()));
                s.setCount(0);
                this.inventory.set(i, ItemStack.EMPTY);
            }
        }

        if (!list.isEmpty()) {
            ItemStack pkg = new ItemStack(ModItems.UC_PACKAGE.get());
            pkg.getOrCreateTag().put("Inventory", list);
            this.inventory.set(10, pkg);
        } else {
            this.coinSum += cost;
        }
        setChanged();
        updateTE();
    }

    private boolean pay(int cost) {
        if (this.coinSum >= cost) {
            this.coinSum -= cost;
            return true;
        }
        ItemStack card = getItem(8);
        return (!card.isEmpty() && UCUtils.debitCard(card, cost));
    }

    public void sendPackage(String targetName) {
        if (this.level == null || this.level.isClientSide || this.level.getServer() == null) return;

        ItemStack pkg = getItem(11);
        ServerPlayer serverPlayer = this.level.getServer().getPlayerList().getPlayerByName(targetName);
        if (serverPlayer != null && !pkg.isEmpty()) {
            if (!serverPlayer.getInventory().add(pkg.copy())) {
                serverPlayer.drop(pkg.copy(), false);
            }
            serverPlayer.sendSystemMessage(Component.literal(this.playerName)
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.translatable("packager.message.sent")));
            this.inventory.set(11, ItemStack.EMPTY);
        }
    }

    private void fillOutputSlot() {
        if (getItem(10).isEmpty() && this.coinSum > 0L) {
            for (int i = 4; i >= 0; i--) {
                int val = UniversalCoins.coinValues[i];
                if (this.coinSum >= val) {
                    ItemStack coins = new ItemStack(ModItems.getCoinByIndex(i), (int) Math.min(this.coinSum / val, 64L));
                    setItem(10, coins);
                    this.coinSum -= (long) coins.getCount() * val;
                    break;
                }
            }
        }
    }

    public void playerLookup(String name, boolean tab) {
        if (this.level == null || this.level.getServer() == null) return;

        if (tab) {
            List<ServerPlayer> players = this.level.getServer().getPlayerList().getPlayers();
            for (ServerPlayer p : players) {
                if (p.getName().getString().toLowerCase().startsWith(name.toLowerCase())) {
                    this.packageTarget = p.getName().getString();
                    break;
                }
            }
        } else {
            this.packageTarget = name;
        }
        setChanged();
        updateTE();
    }

    public void sendServerUpdateMessage(String target, boolean tab) {
        ModPacketHandler.sendToServer(new UCPackagerServerMessage(this.worldPosition, target, tab));
    }

    @Override
    public int getContainerSize() {
        return 12;
    }

    @Override
    public ItemStack getItem(int i) {
        return this.inventory.get(i);
    }

    @Override
    public void setItem(int i, ItemStack s) {
        this.inventory.set(i, s);
        setChanged();
    }

    @Override
    public boolean stillValid(Player p) {
        return Container.stillValidBlockEntity(this, p);
    }

    @Override
    public void load(CompoundTag t) {
        super.load(t);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(t, this.inventory);
        this.coinSum = t.getLong("coinSum");
        this.packageSize = t.getInt("size");
        this.sendMode = t.getBoolean("mode");
    }

    @Override
    protected void saveAdditional(CompoundTag t) {
        super.saveAdditional(t);
        ContainerHelper.saveAllItems(t, this.inventory);
        t.putLong("coinSum", this.coinSum);
        t.putInt("size", this.packageSize);
        t.putBoolean("mode", this.sendMode);
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack removeItem(int i, int c) {
        return ContainerHelper.removeItem(this.inventory, i, c);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(this.inventory, i);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.universalcoins.packager");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
        return new ContainerPackager(id, inv, this, this.packagerData, this);
    }
}