package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerTradeStation;
import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.item.ItemCoin;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.EmissionThrottle;
import aspadien.universalcoins.util.UCItemPricer;
import aspadien.universalcoins.util.UniversalAccounts;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TradeStation extends Protected implements WorldlyContainer, MenuProvider {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);

    public static final int itemInputSlot = 0;
    public static final int itemOutputSlot = 1;
    public static final int itemCardSlot = 2;
    public static final int itemCoinSlot = 3;

    public int coinSum = 0;
    public int itemPrice = 0;
    public boolean buyButtonActive = false;
    public boolean sellButtonActive = false;
    public boolean autoModeButtonActive = true;
    public boolean publicAccess = true;
    public int autoMode = 0;
    public int coinMode = 0;
    private int autoDelayCounter = 0;

    public boolean ironCoinBtnActive;
    public boolean goldCoinBtnActive;
    public boolean emeraldCoinBtnActive;
    public boolean diamondCoinBtnActive;
    public boolean obsidianCoinBtnActive;

    public final ContainerData tradeData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return TradeStation.this.coinSum;
                case 1: return TradeStation.this.itemPrice;
                case 2: return TradeStation.this.autoMode;
                case 3: return TradeStation.this.coinMode;
                case 4: return TradeStation.this.publicAccess ? 1 : 0;
                case 5: return TradeStation.this.buyButtonActive ? 1 : 0;
                case 6: return TradeStation.this.sellButtonActive ? 1 : 0;
                case 7: return TradeStation.this.ironCoinBtnActive ? 1 : 0;
                case 8: return TradeStation.this.goldCoinBtnActive ? 1 : 0;
                case 9: return TradeStation.this.emeraldCoinBtnActive ? 1 : 0;
                case 10: return TradeStation.this.diamondCoinBtnActive ? 1 : 0;
                case 11: return TradeStation.this.obsidianCoinBtnActive ? 1 : 0;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: TradeStation.this.coinSum = value; break;
                case 1: TradeStation.this.itemPrice = value; break;
                case 2: TradeStation.this.autoMode = value; break;
                case 3: TradeStation.this.coinMode = value; break;
                case 4: TradeStation.this.publicAccess = (value == 1); break;
                case 5: TradeStation.this.buyButtonActive = (value == 1); break;
                case 6: TradeStation.this.sellButtonActive = (value == 1); break;
                case 7: TradeStation.this.ironCoinBtnActive = (value == 1); break;
                case 8: TradeStation.this.goldCoinBtnActive = (value == 1); break;
                case 9: TradeStation.this.emeraldCoinBtnActive = (value == 1); break;
                case 10: TradeStation.this.diamondCoinBtnActive = (value == 1); break;
                case 11: TradeStation.this.obsidianCoinBtnActive = (value == 1); break;
            }
        }

        @Override
        public int getCount() {
            return 12;
        }
    };

    public TradeStation(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRADE_STATION.get(), pos, state);
    }

    public long getAccountBalance() {
        ItemStack card = getItem(2);
        if (card.hasTag() && card.getTag() != null) {
            return UniversalAccounts.getInstance().getAccountBalance(card.getTag().getString("Account"));
        }
        return 0L;
    }

    private void activateBuySellButtons() {
        ItemStack input = getItem(0);
        if (input.isEmpty()) {
            this.itemPrice = 0;
            this.buyButtonActive = false;
            this.sellButtonActive = false;
        } else {
            this.itemPrice = UCItemPricer.getInstance().getItemPrice(input);
            if (this.itemPrice <= 0) {
                this.itemPrice = 0;
                this.buyButtonActive = false;
                this.sellButtonActive = false;
            } else {
                this.sellButtonActive = (Integer.MAX_VALUE - this.coinSum >= this.itemPrice);
                boolean hasFunds = (this.coinSum >= this.itemPrice || getAccountBalance() >= this.itemPrice);
                boolean canStack = (getItem(1).isEmpty() || (getItem(1).is(input.getItem()) && getItem(1).getCount() < input.getMaxStackSize()));
                this.buyButtonActive = (hasFunds && canStack);
            }
        }
    }

    public boolean creditAccount(int amount) {
        ItemStack card = getItem(2);
        if (card.isEmpty() || !card.hasTag() || card.getTag() == null) return false;
        String acc = card.getTag().getString("Account");
        return UniversalAccounts.getInstance().creditAccount(acc, amount, false);
    }

    public void onSellPressed(int amount) {
        if (this.level == null || this.level.isClientSide || amount <= 0) return;
        ItemStack input = getItem(0);
        if (input.isEmpty() || input.getCount() < amount) return;

        int price = UCItemPricer.getInstance().getItemPrice(input);
        if (price <= 0) return;

        if (input.isDamaged()) {
            price = price * (input.getMaxDamage() - input.getDamageValue()) / input.getMaxDamage();
        }

        int coinFromSale = (int) ((price * amount) * UniversalCoins.itemSellRatio);
        if (coinFromSale <= 0) coinFromSale = 1;

        ItemStack card = getItem(2);
        if (!card.isEmpty() && card.hasTag()) {
            long referenceBalance = getAccountBalance();
            int throttledAmount = EmissionThrottle.apply(coinFromSale, referenceBalance);
            if (creditAccount(throttledAmount)) {
                input.shrink(amount);
            }
        } else if (this.coinSum + coinFromSale <= 2147483647L) {
            int throttledAmount = EmissionThrottle.apply(coinFromSale, this.coinSum);
            if (this.coinSum + throttledAmount <= 2147483647L) {
                this.coinSum += throttledAmount;
                input.shrink(amount);
            }
        }

        setChanged();
        updateTE();
    }

    public void onSellMaxPressed() {
        ItemStack input = getItem(0);
        if (input.isEmpty() || this.itemPrice <= 0) return;

        int sellPrice = (int) (this.itemPrice * UniversalCoins.itemSellRatio);
        int amountToSell = input.getCount();

        if (getItem(2).isEmpty()) {
            long spaceInBlock = 2147483647L - this.coinSum;
            int maxByBlock = (int) (spaceInBlock / sellPrice);
            amountToSell = Math.min(amountToSell, maxByBlock);
        }

        if (amountToSell > 0) onSellPressed(amountToSell);
    }

    private boolean isSameItem(ItemStack stackA, ItemStack stackB) {
        if (stackA.isEmpty() || stackB.isEmpty()) return false;
        return stackA.is(stackB.getItem()) && ItemStack.isSameItemSameTags(stackA, stackB) && stackA.getDamageValue() == stackB.getDamageValue();
    }

    public boolean debitAccount(int amount) {
        ItemStack card = getItem(2);
        if (card.isEmpty() || !card.hasTag() || card.getTag() == null) return false;
        String acc = card.getTag().getString("Account");
        return UniversalAccounts.getInstance().debitAccount(acc, amount, false);
    }

    public void onBuyPressed(int amount) {
        if (this.level == null || this.level.isClientSide || amount <= 0) return;
        ItemStack template = getItem(0);
        if (template.isEmpty()) return;

        int price = UCItemPricer.getInstance().getItemPrice(template);
        if (price <= 0) return;

        long totalCost = (long) price * amount;
        boolean useCard = false;
        long cardBalance = getAccountBalance();

        if (cardBalance >= totalCost) {
            useCard = true;
        } else if (this.coinSum >= totalCost) {
            useCard = false;
        } else {
            return;
        }

        ItemStack output = getItem(1);
        if (!output.isEmpty() && (!isSameItem(template, output) || output.getCount() + amount > output.getMaxStackSize())) {
            return;
        }

        if (useCard) {
            if (!debitAccount((int) totalCost)) return;
        } else {
            this.coinSum -= (int) totalCost;
        }

        ItemStack result = template.copy();
        result.setCount(amount);
        if (output.isEmpty()) {
            this.inventory.set(1, result);
        } else {
            output.grow(amount);
        }

        setChanged();
        updateTE();
    }

    public void onBuyMaxPressed() {
        ItemStack input = getItem(0);
        if (input.isEmpty() || this.itemPrice <= 0) return;

        long cardBal = getAccountBalance();
        long funds = (cardBal >= this.itemPrice) ? cardBal : this.coinSum;
        int maxByMoney = (int) (funds / this.itemPrice);

        ItemStack output = getItem(1);
        int maxBySpace = input.getMaxStackSize();
        if (!output.isEmpty()) {
            if (!isSameItem(input, output)) return;
            maxBySpace = output.getMaxStackSize() - output.getCount();
        }

        int amountToBuy = Math.min(maxByMoney, maxBySpace);
        if (amountToBuy > 0) onBuyPressed(amountToBuy);
    }

    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            processCoinAbsorption();

            ItemStack input = getItem(0);
            this.itemPrice = input.isEmpty() ? 0 : UCItemPricer.getInstance().getItemPrice(input);

            activateBuySellButtons();
            activateRetrieveButtons();

            if (this.autoMode > 0) {
                this.autoDelayCounter++;
                if (this.autoDelayCounter >= 20) {
                    this.autoDelayCounter = 0;
                    if (this.autoMode == 1) {
                        onBuyMaxPressed();
                    } else if (this.autoMode == 2) {
                        onSellMaxPressed();
                    }
                }
            }

            if (this.coinMode > 0 && this.level.getGameTime() % 10L == 0L) {
                onRetrieveButtonsPressed(2 + this.coinMode - 1, true);
            }

            setChanged();
        }
    }

    private void processCoinAbsorption() {
        int[] slotsToProcess = { 0, 3 };
        for (int slotID : slotsToProcess) {
            ItemStack stack = getItem(slotID);
            int coinValue = CoinUtils.getCoinValue(stack);
            if (coinValue > 0) {
                long totalToAdd = (long) coinValue * stack.getCount();
                if (this.coinSum + totalToAdd < 2147483647L) {
                    this.coinSum += (int) totalToAdd;
                    setItem(slotID, ItemStack.EMPTY);
                    setChanged();
                }
            }
        }
    }

    private void activateRetrieveButtons() {
        this.ironCoinBtnActive = canWithdrawCoin(0);
        this.goldCoinBtnActive = canWithdrawCoin(1);
        this.emeraldCoinBtnActive = canWithdrawCoin(2);
        this.diamondCoinBtnActive = canWithdrawCoin(3);
        this.obsidianCoinBtnActive = canWithdrawCoin(4);
    }

    private boolean canWithdrawCoin(int index) {
        int val = UniversalCoins.coinValues[index];
        ItemStack output = getItem(1);
        ItemCoin itemCoin = ModItems.getCoinByIndex(index);
        return (this.coinSum >= val && (output.isEmpty() || (output.getItem() == itemCoin && output.getCount() < 64)));
    }

    public void onButtonPressed(int buttonId, boolean shiftPressed, int mouseButton) {
        if (this.level == null || this.level.isClientSide) return;
        if (buttonId == 0) {
            if (shiftPressed) { onBuyMaxPressed(); } else { onBuyPressed(1); }
        } else if (buttonId == 1) {
            if (shiftPressed) { onSellMaxPressed(); } else { onSellPressed(1); }
        } else if (buttonId == 8) {
            this.autoMode = (this.autoMode + 1) % 3;
        } else if (buttonId == 7) {
            if (mouseButton == 1) {
                this.coinMode = (this.coinMode == 0) ? 5 : (this.coinMode - 1);
            } else {
                this.coinMode = (this.coinMode + 1) % 6;
            }
        } else if (buttonId >= 2 && buttonId < 7) {
            onRetrieveButtonsPressed(buttonId, shiftPressed);
        } else if (buttonId == 9) {
            this.publicAccess = !this.publicAccess;
        }

        setChanged();
        updateTE();
    }

    public void onRetrieveButtonsPressed(int buttonId, boolean shiftPressed) {
        int index = buttonId - 2;
        if (index < 0 || index > 4) return;
        int val = UniversalCoins.coinValues[index];
        if (this.coinSum < val) return;

        ItemStack output = getItem(1);
        ItemCoin itemCoin = ModItems.getCoinByIndex(index);

        if (output.isEmpty() || (output.getItem() == itemCoin && output.getCount() < 64)) {
            int count = shiftPressed ? Math.min(this.coinSum / val, 64 - output.getCount()) : 1;
            if (count <= 0) return;

            if (output.isEmpty()) {
                this.inventory.set(1, new ItemStack(itemCoin, count));
            } else {
                output.grow(count);
            }
            this.coinSum -= count * val;

            setChanged();
            updateTE();
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        if (this.level != null && !this.level.isClientSide) {
            if (index == 0 && !stack.isEmpty()) {
                this.itemPrice = UCItemPricer.getInstance().getItemPrice(stack);
            }
            setChanged();
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[] { 0, 1, 3 };
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack s, @Nullable Direction d) {
        return (i == 0 || (i == 3 && s.getItem() instanceof ItemCoin));
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack s, Direction d) {
        return (i == 1);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.inventory.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack s = ContainerHelper.removeItem(this.inventory, index, count);
        setChanged();
        return s;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.inventory, index);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public boolean stillValid(Player p) {
        return Container.stillValidBlockEntity(this, p);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.universalcoins.tradestation");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
        return new ContainerTradeStation(id, inv, this, this.tradeData, this);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory);
        this.coinSum = nbt.getInt("CoinSum");
        this.autoMode = nbt.getInt("AutoMode");
        this.coinMode = nbt.getInt("CoinMode");
        this.publicAccess = nbt.getBoolean("PublicAccess");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, this.inventory);
        nbt.putInt("CoinSum", this.coinSum);
        nbt.putInt("AutoMode", this.autoMode);
        nbt.putInt("CoinMode", this.coinMode);
        nbt.putBoolean("PublicAccess", this.publicAccess);
    }
}