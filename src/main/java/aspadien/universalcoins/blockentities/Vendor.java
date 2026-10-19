package aspadien.universalcoins.blockentities;
import java.util.Arrays;
import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerVendor;
import aspadien.universalcoins.container.ContainerVendorBuy;
import aspadien.universalcoins.container.ContainerVendorSell;
import aspadien.universalcoins.init.ModBlocks;
import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.net.UCVendorServerMessage;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UCItemPricer;
import aspadien.universalcoins.util.UCUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

// IInventory -> Container, INamedContainerProvider -> MenuProvider, ITickableTileEntity удален
public abstract class Vendor extends Protected implements Container, MenuProvider {
    protected NonNullList<ItemStack> inventory = NonNullList.withSize(17, ItemStack.EMPTY);

    public static final int itemStorageSlot1 = 0;
    public static final int itemStorageSlot9 = 8;
    public static final int itemTradeSlot = 9;
    public static final int itemCardSlot = 10;
    public static final int itemSellSlot = 11;
    public static final int itemOutputSlot = 12;
    public static final int itemCoinOutputSlot = 13;
    public static final int itemCoinInputSlot = 14;
    public static final int itemUserCoinInputSlot = 15;
    public static final int itemUserCardSlot = 16;
    public static final int idSellButton = 10;
    public static final int idBuyButton = 11;

    public int coinSum = 0;
    public int userCoinSum = 0;
    public int itemPrice = 0;
    public int textColor = 0;

    public boolean infiniteMode = false, sellMode = true, ooStockWarning = false, ooCoinsWarning = false, inventoryFullWarning = false;
    public boolean buyButtonActive = false;
    public boolean sellButtonActive = false;
    public boolean ironCoinBtnActive;
    public boolean goldCoinBtnActive;
    public boolean emeraldCoinBtnActive;
    public boolean diamondCoinBtnActive;
    public boolean obsidianCoinBtnActive;
    public boolean uironCoinBtnActive;
    public boolean ugoldCoinBtnActive;
    public boolean uemeraldCoinBtnActive;
    public boolean udiamondCoinBtnActive;
    public boolean uobsidianCoinBtnActive;

    private BlockPos remoteStoragePos = null;
    private boolean needsShopInit = false;

    private static final List<Item> BLACKLIST = Arrays.asList(
            Items.BEDROCK, Items.BARRIER, Items.COMMAND_BLOCK, Items.STRUCTURE_BLOCK,
            Items.STRUCTURE_VOID, Items.JIGSAW, Items.SPAWNER,
            ModItems.IRON_COIN.get(), ModItems.GOLD_COIN.get(), ModItems.EMERALD_COIN.get(),
            ModItems.DIAMOND_COIN.get(), ModItems.OBSIDIAN_COIN.get(), ModItems.NETHERITE_COIN.get()
    );

    // IIntArray -> ContainerData
    public final ContainerData wrenchData = new ContainerData() {
        @Override
        public int get(int index) {
            return Vendor.this.infiniteMode ? 1 : 0;
        }

        @Override
        public void set(int index, int value) {
            Vendor.this.infiniteMode = (value == 1);
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public final ContainerData vendorData = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 -> Vendor.this.coinSum;
                case 1 -> Vendor.this.userCoinSum;
                case 2 -> Vendor.this.itemPrice;
                case 3 -> Vendor.this.infiniteMode ? 1 : 0;
                case 4 -> Vendor.this.sellMode ? 1 : 0;
                case 5 -> Vendor.this.ooStockWarning ? 1 : 0;
                case 6 -> Vendor.this.ooCoinsWarning ? 1 : 0;
                case 7 -> Vendor.this.inventoryFullWarning ? 1 : 0;
                case 8 -> Vendor.this.buyButtonActive ? 1 : 0;
                case 9 -> Vendor.this.sellButtonActive ? 1 : 0;
                case 10 -> Vendor.this.ironCoinBtnActive ? 1 : 0;
                case 11 -> Vendor.this.goldCoinBtnActive ? 1 : 0;
                case 12 -> Vendor.this.emeraldCoinBtnActive ? 1 : 0;
                case 13 -> Vendor.this.diamondCoinBtnActive ? 1 : 0;
                case 14 -> Vendor.this.obsidianCoinBtnActive ? 1 : 0;
                case 15 -> Vendor.this.uironCoinBtnActive ? 1 : 0;
                case 16 -> Vendor.this.ugoldCoinBtnActive ? 1 : 0;
                case 17 -> Vendor.this.uemeraldCoinBtnActive ? 1 : 0;
                case 18 -> Vendor.this.udiamondCoinBtnActive ? 1 : 0;
                case 19 -> Vendor.this.uobsidianCoinBtnActive ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int i, int v) {
            switch (i) {
                case 0 -> Vendor.this.coinSum = v;
                case 1 -> Vendor.this.userCoinSum = v;
                case 2 -> Vendor.this.itemPrice = v;
                case 3 -> Vendor.this.infiniteMode = (v == 1);
                case 4 -> Vendor.this.sellMode = (v == 1);
            }
        }

        @Override
        public int getCount() {
            return 20;
        }
    };

    // Конструктор в 1.20.1 обязательно принимает BlockPos и BlockState
    public Vendor(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if (this.level == null || this.level.isClientSide) return;

        if (this.needsShopInit && !this.level.hasNeighborSignal(this.worldPosition)) {
            this.needsShopInit = false;
            randomizeShop();
            setChanged();
        }

        if (this.inUse && this.level.getGameTime() % 40L == 0L) {
            // PlayerEntity -> Player, AxisAlignedBB -> AABB
            if (this.level.getEntitiesOfClass(Player.class, (new AABB(this.worldPosition)).inflate(8.0D)).isEmpty()) {
                this.inUse = false;
            }
        }

        absorbCoins();

        ItemStack tradeItem = getItem(9);
        if (!tradeItem.isEmpty() && this.itemPrice <= 0) {
            this.itemPrice = UCItemPricer.getInstance().getItemPrice(tradeItem);
        }

        updateLogic();

        if (this.ooStockWarning && this.remoteStoragePos != null) checkRemoteStorage();

        if (this.level.getGameTime() % 10L == 0L) setChanged();
    }

    private void absorbCoins() {
        absorbFromSlot(14, true);
        absorbFromSlot(15, false);
        setChanged();
        updateTE();
    }

    private void absorbFromSlot(int slot, boolean isOwner) {
        ItemStack stack = this.inventory.get(slot);
        int val = CoinUtils.getCoinValue(stack);
        if (val > 0) {
            long total = (long) val * stack.getCount();
            if (isOwner) {
                this.coinSum = (int) (this.coinSum + total);
            } else {
                this.userCoinSum = (int) (this.userCoinSum + total);
            }
            this.inventory.set(slot, ItemStack.EMPTY);
        }
    }

    private void updateLogic() {
        ItemStack tradeItem = getItem(9);
        if (tradeItem.isEmpty()) {
            this.buyButtonActive = this.sellButtonActive = false;
            this.ooStockWarning = false;
            return;
        }
        long uFunds = UCUtils.getCardBalance(getItem(16)) + this.userCoinSum;
        if (uFunds < 0L) uFunds = this.userCoinSum;
        this.ooStockWarning = (!this.infiniteMode && !UCUtils.hasStock(this.inventory, tradeItem, tradeItem.getCount()));
        this.buyButtonActive = (!this.ooStockWarning && uFunds >= this.itemPrice && this.itemPrice > 0);

        long oFunds = UCUtils.getCardBalance(getItem(10)) + this.coinSum;
        if (oFunds < 0L) oFunds = this.coinSum;
        this.sellButtonActive = (UCUtils.isSameItem(getItem(11), tradeItem) && oFunds >= this.itemPrice && (this.infiniteMode || hasInventorySpace()));

        updateCoinButtons();
    }

    private void updateCoinButtons() {
        this.ironCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[0]);
        this.goldCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[1]);
        this.emeraldCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[2]);
        this.diamondCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[3]);
        this.obsidianCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[4]);

        this.uironCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[0]);
        this.ugoldCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[1]);
        this.uemeraldCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[2]);
        this.udiamondCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[3]);
        this.uobsidianCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[4]);
    }

    public void onButtonPressed(int id, boolean shift, int mouseButton) {
        if (this.level == null || this.level.isClientSide) return;
        if (id == 0) {
            this.sellMode = !this.sellMode;
        } else if (id == 2) {
            sendServerUpdateMessage();
        } else if (id == 8) {
            if (this.textColor > 0) this.textColor--;
        } else if (id == 9) {
            if (this.textColor < 15) this.textColor++;
        } else if (id == 10) {
            onSellPressed(shift ? getItem(11).getCount() : 1);
        } else if (id == 11) {
            if (shift) {
                onBuyMaxPressed();
            } else {
                onBuyPressed(1);
            }
        } else if (isCoinButton(id)) {
            onRetrieveButtonsPressed(id, shift);
        }

        setChanged();
        updateTE();
    }

    private boolean isCoinButton(int id) {
        return ((id >= 3 && id <= 7) || (id >= 12 && id <= 16));
    }

    public void onRetrieveButtonsPressed(int id, boolean shift) {
        int idx = (id >= 12) ? (id - 12) : (id - 3);
        if (id < 10) {
            this.coinSum = (int) UCUtils.withdrawCoins(this.coinSum, this, 13, idx, shift);
        } else {
            this.userCoinSum = (int) UCUtils.withdrawCoins(this.userCoinSum, this, 13, idx, shift);
        }
    }

    public void onBuyPressed(int amount) {
        if (this.level == null || this.level.isClientSide || amount <= 0) return;
        ItemStack tradeItem = getItem(9);
        if (tradeItem.isEmpty() || this.itemPrice <= 0) return;
        int totalItemsToGive = tradeItem.getCount() * amount;
        int totalCost = this.itemPrice * amount;

        if (!this.infiniteMode && !UCUtils.hasStock(this.inventory, tradeItem, totalItemsToGive)) {
            this.ooStockWarning = true;
            setChanged();
            return;
        }

        ItemStack outputSlotStack = this.inventory.get(12);
        if (!outputSlotStack.isEmpty() && (
                !UCUtils.isSameItem(outputSlotStack, tradeItem) || outputSlotStack.getCount() + totalItemsToGive > tradeItem.getMaxStackSize())) {
            return;
        }

        long cardBalance = UCUtils.getCardBalance(getItem(16));
        if (cardBalance < 0L) cardBalance = 0L;
        long totalUserFunds = cardBalance + this.userCoinSum;

        if (totalUserFunds >= totalCost) {
            long remainingToPay = totalCost;

            if (this.userCoinSum > 0) {
                int takeFromCash = (int) Math.min(this.userCoinSum, remainingToPay);
                this.userCoinSum -= takeFromCash;
                remainingToPay -= takeFromCash;
            }

            if (remainingToPay > 0L) {
                UCUtils.debitCard(getItem(16), remainingToPay);
            }

            if (!UCUtils.creditCard(getItem(10), totalCost)) {
                this.coinSum += totalCost;
            }

            if (!this.infiniteMode) {
                UCUtils.decreaseStock(this.inventory, tradeItem, totalItemsToGive);
            }

            if (this.inventory.get(12).isEmpty()) {
                ItemStack result = tradeItem.copy();
                result.setCount(totalItemsToGive);
                this.inventory.set(12, result);
            } else {
                this.inventory.get(12).grow(totalItemsToGive);
            }

            setChanged();
            updateTE();
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
        }
    }

    public void onSellPressed(int amount) {
        if (this.level == null || this.level.isClientSide || amount <= 0) return;
        ItemStack sample = getItem(9);
        ItemStack incoming = getItem(11);

        if (incoming.isEmpty() || !UCUtils.isSameItem(incoming, sample)) return;
        if (incoming.getCount() < amount) return;

        int totalPay = this.itemPrice * amount;

        boolean ownerPaid = false;
        long ownerBal = UCUtils.getCardBalance(getItem(10));
        if (ownerBal >= totalPay) {
            ownerPaid = UCUtils.debitCard(getItem(10), totalPay);
        } else if (this.coinSum >= totalPay) {
            this.coinSum -= totalPay;
            ownerPaid = true;
        }

        if (ownerPaid) {
            if (!this.infiniteMode && !hasInventorySpace()) {
                return;
            }
            if (!UCUtils.creditCard(getItem(16), totalPay)) {
                this.userCoinSum += totalPay;
            }

            if (!this.infiniteMode) {
                ItemStack toStore = incoming.copy();
                toStore.setCount(amount);
                UCUtils.storeItems(this.inventory, toStore);
            }

            incoming.shrink(amount);
            if (incoming.isEmpty()) this.inventory.set(11, ItemStack.EMPTY);

            setChanged();
            updateTE();
            this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
        }
    }

    public void onBuyMaxPressed() {
        ItemStack tradeItem = getItem(9);
        if (tradeItem.isEmpty() || this.itemPrice <= 0) return;
        long uFunds = UCUtils.getCardBalance(getItem(16)) + this.userCoinSum;
        if (uFunds < 0L) uFunds = this.userCoinSum;
        int amount = (int) (uFunds / this.itemPrice);

        if (!this.infiniteMode) {
            int found = 0;
            for (int i = 0; i <= 8; i++) {
                if (UCUtils.isSameItem(getItem(i), tradeItem)) found += getItem(i).getCount();
            }
            amount = Math.min(amount, found / tradeItem.getCount());
        }
        amount = Math.min(amount, 64 / tradeItem.getCount());
        if (amount > 0) onBuyPressed(amount);
    }

    public void onSellMaxPressed() {
        ItemStack userStack = getItem(11);
        if (userStack.isEmpty() || this.itemPrice <= 0) return;
        long oFunds = UCUtils.getCardBalance(getItem(10)) + this.coinSum;
        if (oFunds < 0L) oFunds = this.coinSum;
        int maxByMoney = (int) (oFunds / this.itemPrice);
        int amount = Math.min(userStack.getCount(), maxByMoney);

        if (amount > 0) onSellPressed(amount);
    }

    public void setGhostSlot(ItemStack s) {
        if (!s.isEmpty()) {
            ItemStack g = s.copy();
            g.setCount(1);
            this.inventory.set(9, g);
            this.itemPrice = UCItemPricer.getInstance().getItemPrice(g);
        } else {
            this.inventory.set(9, ItemStack.EMPTY);
            this.itemPrice = 0;
        }
        setChanged();
        updateTE();
        if (this.level != null) this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void setRemoteStorage(int[] coords) {
        this.remoteStoragePos = new BlockPos(coords[0], coords[1], coords[2]);
        setChanged();
    }

    public void checkRemoteStorage() {
        if (this.remoteStoragePos == null || !this.level.isLoaded(this.remoteStoragePos)) return;
        // TileEntity -> BlockEntity, IInventory -> Container
        BlockEntity te = this.level.getBlockEntity(this.remoteStoragePos);
        if (te instanceof Container chest) {
            ItemStack tradeItem = getItem(9);
            for (int i = 0; i < chest.getContainerSize(); i++) {
                if (UCUtils.isSameItem(chest.getItem(i), tradeItem)) {
                    UCUtils.storeItems(this.inventory, chest.getItem(i));
                    chest.setItem(i, ItemStack.EMPTY);
                    setChanged();
                    return;
                }
            }
        }
    }

    public boolean hasInventorySpace() {
        ItemStack tradeItem = getItem(9);
        for (int i = 0; i <= 8; i++) {
            ItemStack s = this.inventory.get(i);
            if (s.isEmpty() || (UCUtils.isSameItem(s, tradeItem) && s.getCount() < s.getMaxStackSize())) return true;
        }
        return false;
    }

    // Методы интерфейса Container
    @Override
    public int getContainerSize() {
        return 17;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int i) {
        return this.inventory.get(i);
    }

    @Override
    public void setItem(int i, ItemStack s) {
        if (i == 9) return;
        this.inventory.set(i, s);
        setChanged();
    }

    // ItemStackHelper -> ContainerHelper
    @Override
    public ItemStack removeItem(int i, int c) {
        ItemStack s = ContainerHelper.removeItem(this.inventory, i, c);
        setChanged();
        return s;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(this.inventory, i);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
        setChanged();
    }

    @Override
    public boolean stillValid(Player p) {
        return Container.stillValidBlockEntity(this, p);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack s) {
        if (i == 11) {
            ItemStack sample = getItem(9);
            return (!sample.isEmpty() && (s.isEmpty() || UCUtils.isSameItem(s, sample)));
        }
        if (i == 9) return false;
        if (i == 14 || i == 15) return (CoinUtils.getCoinValue(s) > 0);
        return (i <= 8 || i == 12 || i == 10 || i == 16);
    }

    // В 1.20.1 load принимает только CompoundTag (без BlockState)
    @Override
    public void load(CompoundTag t) {
        super.load(t);
        this.inventory = NonNullList.withSize(17, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(t, this.inventory);
        this.coinSum = t.getInt("CoinSum");
        this.userCoinSum = t.getInt("UserSum");
        this.itemPrice = t.getInt("Price");
        this.infiniteMode = t.getBoolean("Inf");
        this.sellMode = t.getBoolean("Mode");
        if (t.contains("RX")) {
            this.remoteStoragePos = new BlockPos(t.getInt("RX"), t.getInt("RY"), t.getInt("RZ"));
        }
        if (t.contains("NeedsShopInit")) {
            this.needsShopInit = t.getBoolean("NeedsShopInit");
        }
    }

    // Вместо save() переопределяем saveAdditional()
    @Override
    protected void saveAdditional(CompoundTag t) {
        super.saveAdditional(t);
        ContainerHelper.saveAllItems(t, this.inventory);
        t.putInt("CoinSum", this.coinSum);
        t.putInt("UserSum", this.userCoinSum);
        t.putInt("Price", this.itemPrice);
        t.putBoolean("Inf", this.infiniteMode);
        t.putBoolean("Mode", this.sellMode);
        if (this.remoteStoragePos != null) {
            t.putInt("RX", this.remoteStoragePos.getX());
            t.putInt("RY", this.remoteStoragePos.getY());
            t.putInt("RZ", this.remoteStoragePos.getZ());
        }
        if (this.needsShopInit) {
            t.putBoolean("NeedsShopInit", true);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(ModBlocks.VENDOR.get().getDescriptionId());
    }

    public void sendServerUpdateMessage() {
        if (this.level != null && this.level.isClientSide) {
            ModPacketHandler.sendToServer(new UCVendorServerMessage(this.worldPosition, this.itemPrice, this.blockOwner, this.infiniteMode));
        }
    }

    // Container -> AbstractContainerMenu, PlayerInventory -> Inventory, PlayerEntity -> Player
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
        this.inUse = true;

        boolean isOwner = p.getUUID().toString().equals(this.blockOwner);
        if (isOwner) {
            return new ContainerVendor(id, inv, this, this.vendorData, this);
        }
        return this.sellMode
                ? new ContainerVendorBuy(id, inv, this, this.vendorData, this)
                : new ContainerVendorSell(id, inv, this, this.vendorData, this);
    }

    public void inUseCleanup() {
        this.inUse = false;
        setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
        }
    }

    private void randomizeShop() {
        this.infiniteMode = true;
        this.sellMode = true;

        assert this.level != null;
        // Random -> RandomSource
        RandomSource random = this.level.getRandom();

        int max = Math.max(UniversalCoins.shopMaxPrice, UniversalCoins.shopMinPrice + 1);
        int priceModifier = random.nextInt(max - UniversalCoins.shopMinPrice) + UniversalCoins.shopMinPrice;

        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < 500; i++) {
            ItemStack candidate = UCItemPricer.getInstance().getRandomPricedStack();
            if (candidate != null && !candidate.isEmpty() && !BLACKLIST.contains(candidate.getItem())) {
                stack = candidate;
                break;
            }
        }

        if (!stack.isEmpty()) {
            ItemStack ghostStack = stack.copy();
            ghostStack.setCount(1);
            this.inventory.set(9, ghostStack);

            int basePrice = UCItemPricer.getInstance().getItemPrice(stack);
            this.itemPrice = Math.max(1, basePrice * priceModifier / 100);
        } else {
            if (!this.level.isClientSide && this.level.getServer() != null) {
                String errorText = "[UniversalCoins] Ошибка: Магазин на координатах X:" + this.worldPosition.getX() +
                        " Y:" + this.worldPosition.getY() +
                        " Z:" + this.worldPosition.getZ() +
                        " не смог найти предмет для продажи (в списке только монеты?)";

                // Отправка системного сообщения игрокам на сервере
                this.level.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal(errorText),
                        false
                );
            }
        }
        updateTE();
        this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
    }
}