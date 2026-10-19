package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerPowerTransmitter;
import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.init.ModItems;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerTransmitter extends Protected implements Container, IEnergyStorage, MenuProvider {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    public static final int itemCardSlot = 0;
    public static final int itemOutputSlot = 1;

    public long coinSum = 0L;
    public int feLevel = 0;
    public int kfeSold = 0;
    public boolean publicAccess = false;

    protected final ContainerData transmitterData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return (int) (PowerTransmitter.this.coinSum & 0xFFFFFFFFL);
                case 1: return (int) (PowerTransmitter.this.coinSum >> 32L);
                case 2: return PowerTransmitter.this.feLevel;
                case 3: return PowerTransmitter.this.kfeSold;
                case 4: return PowerTransmitter.this.publicAccess ? 1 : 0;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: PowerTransmitter.this.coinSum = (PowerTransmitter.this.coinSum & 0xFFFFFFFF00000000L) | (value & 0xFFFFFFFFL); break;
                case 1: PowerTransmitter.this.coinSum = (PowerTransmitter.this.coinSum & 0xFFFFFFFFL) | ((long) value << 32L); break;
                case 2: PowerTransmitter.this.feLevel = value; break;
                case 3: PowerTransmitter.this.kfeSold = value; break;
                case 4: PowerTransmitter.this.publicAccess = (value == 1); break;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    private final LazyOptional<IEnergyStorage> energyHolder;

    public PowerTransmitter(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POWER_TRANSMITTER.get(), pos, state);
        this.energyHolder = LazyOptional.of(() -> this);
    }

    public void tick() {
        if (this.level != null && !this.level.isClientSide && this.level.getGameTime() % 10L == 0L) {
            setChanged();
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int canReceive = Math.min(maxReceive, 1000000 - this.feLevel);
        if (!simulate) {
            this.feLevel += canReceive;
            processEnergySale();
        }
        return canReceive;
    }

    private void processEnergySale() {
        if (this.feLevel >= 10000) {
            int chunks = this.feLevel / 10000;
            int payment = chunks * UniversalCoins.rfWholesaleRate;
            if (creditAccount(payment)) {
                this.kfeSold += chunks * 10;
                this.feLevel -= chunks * 10000;
                UniversalPower.getInstance().receiveEnergy(chunks * 10, false);
            } else {
                this.coinSum += payment;
                this.kfeSold += chunks * 10;
                this.feLevel -= chunks * 10000;
                UniversalPower.getInstance().receiveEnergy(chunks * 10, false);
            }
            setChanged();
        }
    }

    private boolean creditAccount(long amount) {
        ItemStack card = getItem(0);
        if (card.isEmpty() || !card.hasTag()) return false;
        String acc = card.getTag().getString("Account");
        return UniversalAccounts.getInstance().creditAccount(acc, amount, false);
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
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
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
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.inventory.get(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        setChanged();
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
        return ContainerHelper.removeItem(this.inventory, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.inventory, index);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.universalcoins.power_transmitter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ContainerPowerTransmitter(id, inv, this, this.transmitterData, this);
    }

    public void onButtonPressed(int buttonId, boolean shift) {
        if (this.level == null || this.level.isClientSide) return;
        if (buttonId == 0) {
            fillOutputSlot();
        } else if (buttonId == 1) {
            this.publicAccess = !this.publicAccess;
        }
        setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inventory);
        this.coinSum = tag.getLong("coinSum");
        this.feLevel = tag.getInt("feLevel");
        this.kfeSold = tag.getInt("kfeSold");
        this.publicAccess = tag.getBoolean("publicAccess");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory);
        tag.putLong("coinSum", this.coinSum);
        tag.putInt("feLevel", this.feLevel);
        tag.putInt("kfeSold", this.kfeSold);
        tag.putBoolean("publicAccess", this.publicAccess);
    }

    private void fillOutputSlot() {
        if (getItem(1).isEmpty() && this.coinSum > 0L) {
            for (int i = 4; i >= 0; i--) {
                int val = UniversalCoins.coinValues[i];
                if (this.coinSum >= val) {
                    ItemStack coins = new ItemStack(ModItems.getCoinByIndex(i));
                    int count = (int) Math.min(this.coinSum / val, 64L);
                    coins.setCount(count);
                    setItem(1, coins);
                    this.coinSum -= (long) count * val;
                    break;
                }
            }
        }
        updateTE();
    }
}