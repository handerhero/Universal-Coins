package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerPowerReceiver;
import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UniversalAccounts;
import aspadien.universalcoins.util.UniversalPower;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerReceiver extends Protected implements WorldlyContainer, IEnergyStorage, MenuProvider {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
    public static final int itemCardSlot = 0;
    public static final int itemCoinSlot = 1;
    public static final int itemOutputSlot = 2;

    public long coinSum = 0L;
    public int feLevel = 0;
    public int feOutput = 0;
    public long wfeLevel = 0L;
    public Direction orientation = null;
    public boolean publicAccess = false;

    protected final ContainerData receiverData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return (int) (PowerReceiver.this.coinSum & 0xFFFFFFFFL);
                case 1: return (int) (PowerReceiver.this.coinSum >> 32L);
                case 2: return PowerReceiver.this.feLevel;
                case 3: return PowerReceiver.this.feOutput;
                case 4: return (int) (PowerReceiver.this.wfeLevel & 0xFFFFFFFFL);
                case 5: return (int) (PowerReceiver.this.wfeLevel >> 32L);
                case 6: return PowerReceiver.this.publicAccess ? 1 : 0;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: PowerReceiver.this.coinSum = (PowerReceiver.this.coinSum & 0xFFFFFFFF00000000L) | (value & 0xFFFFFFFFL); break;
                case 1: PowerReceiver.this.coinSum = (PowerReceiver.this.coinSum & 0xFFFFFFFFL) | ((long) value << 32L); break;
                case 2: PowerReceiver.this.feLevel = value; break;
                case 3: PowerReceiver.this.feOutput = value; break;
                case 4: PowerReceiver.this.wfeLevel = (PowerReceiver.this.wfeLevel & 0xFFFFFFFF00000000L) | (value & 0xFFFFFFFFL); break;
                case 5: PowerReceiver.this.wfeLevel = (PowerReceiver.this.wfeLevel & 0xFFFFFFFFL) | ((long) value << 32L); break;
                case 6: PowerReceiver.this.publicAccess = (value == 1); break;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    private final LazyOptional<IEnergyStorage> energyHolder;

    public PowerReceiver(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POWER_RECEIVER.get(), pos, state);
        this.energyHolder = LazyOptional.of(() -> this);
    }

    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            absorbCoinsFromSlot();
            buyPower();
            sendPower();
            setChanged();
        }
    }

    private void absorbCoinsFromSlot() {
        ItemStack stack = this.inventory.get(1);
        if (!stack.isEmpty()) {
            int val = CoinUtils.getCoinValue(stack);
            if (val > 0) {
                this.coinSum += (long) val * stack.getCount();
                this.inventory.set(1, ItemStack.EMPTY);
                setChanged();
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    protected void buyPower() {
        this.wfeLevel = UniversalPower.getInstance().getFeLevel();
        if (this.feLevel == 0 && UniversalPower.getInstance().extractEnergy(10, true) > 0L) {
            int rate = UniversalCoins.rfRetailRate;
            if (debitAccount(rate)) {
                UniversalPower.getInstance().extractEnergy(10, false);
                this.feLevel += 10000;
                updateTE();
            } else if (this.coinSum >= rate) {
                this.coinSum -= rate;
                UniversalPower.getInstance().extractEnergy(10, false);
                this.feLevel += 10000;
                updateTE();
            }
        }
    }

    protected void sendPower() {
        if (this.orientation == null) {
            resetPowerDirection();
            return;
        }
        BlockEntity te = this.level.getBlockEntity(this.worldPosition.relative(this.orientation));
        if (te != null) {
            te.getCapability(ForgeCapabilities.ENERGY, this.orientation.getOpposite()).ifPresent(storage -> {
                if (this.feLevel > 0) {
                    int sent = storage.receiveEnergy(Math.min(1000, this.feLevel), false);
                    if (sent > 0) {
                        this.feOutput = sent;
                        this.feLevel -= sent;
                        setChanged();
                    }
                } else {
                    this.feOutput = 0;
                }
            });
        } else {
            this.orientation = null;
            this.feOutput = 0;
        }
    }

    private void resetPowerDirection() {
        for (Direction dir : Direction.values()) {
            BlockEntity te = this.level.getBlockEntity(this.worldPosition.relative(dir));
            if (te != null && te.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).isPresent()) {
                this.orientation = dir;
                return;
            }
        }
    }

    private boolean debitAccount(int amount) {
        ItemStack card = getItem(0);
        if (card.isEmpty() || !card.hasTag()) return false;
        String acc = card.getTag().getString("Account");
        return UniversalAccounts.getInstance().debitAccount(acc, amount, false);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyHolder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyHolder.invalidate();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = Math.min(this.feLevel, maxExtract);
        if (!simulate && extracted > 0) {
            this.feLevel -= extracted;
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return this.feLevel;
    }

    @Override
    public int getMaxEnergyStored() {
        return 1000000;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.inventory.get(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        if (this.level != null && !this.level.isClientSide) {
            setChanged();
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.universalcoins.power_receiver");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ContainerPowerReceiver(id, inv, this, this.receiverData, this);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack s = ContainerHelper.removeItem(this.inventory, index, count);
        setChanged();
        updateTE();
        return s;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.inventory, index);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[] { 1 };
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction dir) {
        return (index == 1);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inventory);
        this.coinSum = tag.getLong("coinSum");
        this.feLevel = tag.getInt("feLevel");
        this.publicAccess = tag.getBoolean("publicAccess");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory);
        tag.putLong("coinSum", this.coinSum);
        tag.putInt("feLevel", this.feLevel);
        tag.putBoolean("publicAccess", this.publicAccess);
    }

    public void onButtonPressed(int buttonId, boolean shift) {
        if (this.level == null || this.level.isClientSide) return;
        if (buttonId == 0) {
            fillOutputSlot();
        } else if (buttonId == 1) {
            this.publicAccess = !this.publicAccess;
        }
        updateTE();
    }

    private void fillOutputSlot() {
        if (getItem(2).isEmpty() && this.coinSum > 0L) {
            for (int i = 4; i >= 0; i--) {
                int val = UniversalCoins.coinValues[i];
                if (this.coinSum >= val) {
                    ItemStack coins = new ItemStack(ModItems.getCoinByIndex(i));
                    int count = (int) Math.min(this.coinSum / val, 64L);
                    coins.setCount(count);

                    this.inventory.set(2, coins);
                    this.coinSum -= (long) count * val;
                    break;
                }
            }
        }
        updateTE();
    }
}