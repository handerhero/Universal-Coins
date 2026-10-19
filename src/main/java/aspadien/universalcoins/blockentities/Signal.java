package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerSignal;
import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.init.ModItems;
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

public class Signal extends Protected implements Container, MenuProvider {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public static final int itemOutputSlot = 0;
    public int coinSum = 0;
    public int fee = 1;
    public int duration = 1;
    public int counter = 0;
    public int secondsLeft = 0;
    public String customName = "";

    public boolean canProvidePower = false;

    protected final ContainerData signalData = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return Signal.this.coinSum;
                case 1: return Signal.this.fee;
                case 2: return Signal.this.duration;
                case 3: return Signal.this.secondsLeft;
                case 4: return Signal.this.canProvidePower ? 1 : 0;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: Signal.this.coinSum = value; break;
                case 1: Signal.this.fee = value; break;
                case 2: Signal.this.duration = value; break;
                case 3: Signal.this.secondsLeft = value; break;
                case 4: Signal.this.canProvidePower = (value == 1); break;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public Signal(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGNAL.get(), pos, state);
    }

    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            if (this.counter > 0) {
                this.counter--;
                int currentSeconds = (int) Math.ceil(this.counter / 20.0D);
                if (currentSeconds != this.secondsLeft) {
                    this.secondsLeft = currentSeconds;
                    setChanged();
                }
            } else if (this.canProvidePower) {
                this.canProvidePower = false;
                this.secondsLeft = 0;
                updateNeighbors();
                setChanged();
            }
        }
    }

    public void onButtonPressed(int buttonId, boolean shift) {
        if (this.level == null || this.level.isClientSide) return;
        switch (buttonId) {
            case 0:
                fillOutputSlot();
                break;
            case 1:
                this.duration = Math.max(1, this.duration - (shift ? 10 : 1));
                break;
            case 2:
                this.duration = Math.min(1000000, this.duration + (shift ? 10 : 1));
                break;
            case 3:
                this.fee = Math.max(1, this.fee - (shift ? 10 : 1));
                break;
            case 4:
                this.fee = Math.min(1000000, this.fee + (shift ? 10 : 1));
                break;
        }
        setChanged();
    }

    public void activateSignal() {
        this.canProvidePower = true;
        this.counter += this.duration * 20;
        this.coinSum += this.fee;
        updateNeighbors();
        setChanged();
    }

    private void updateNeighbors() {
        if (this.level != null) {
            this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
        }
    }

    private void fillOutputSlot() {
        if (getItem(0).isEmpty() && this.coinSum > 0) {
            for (int i = 4; i >= 0; i--) {
                int val = UniversalCoins.coinValues[i];
                if (this.coinSum >= val) {
                    ItemStack coins = new ItemStack(ModItems.getCoinByIndex(i));
                    int count = Math.min(this.coinSum / val, 64);
                    coins.setCount(count);
                    setItem(0, coins);
                    this.coinSum -= count * val;
                    break;
                }
            }
        }
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
        return ContainerHelper.removeItem(this.inventory, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.inventory, index);
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
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inventory);
        this.coinSum = tag.getInt("coinSum");
        this.fee = tag.getInt("fee");
        this.duration = tag.getInt("duration");
        this.secondsLeft = tag.getInt("secondsLeft");
        this.counter = this.secondsLeft * 20;
        this.canProvidePower = tag.getBoolean("canProvidePower");
        this.customName = tag.getString("customName");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory);
        tag.putInt("coinSum", this.coinSum);
        tag.putInt("fee", this.fee);
        tag.putInt("duration", this.duration);
        tag.putInt("secondsLeft", this.secondsLeft);
        tag.putBoolean("canProvidePower", this.canProvidePower);
        tag.putString("customName", this.customName);
    }

    @Override
    public Component getDisplayName() {
        return this.customName.isEmpty()
                ? Component.translatable("block.aspadien.universalcoins.signal")
                : Component.literal(this.customName);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ContainerSignal(id, inv, this, this.signalData, this);
    }
}