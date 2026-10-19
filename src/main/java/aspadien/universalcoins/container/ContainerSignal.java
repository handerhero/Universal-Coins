package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.Signal;
import aspadien.universalcoins.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerSignal extends AbstractContainerMenu {
    private final Signal tileEntity;
    private final ContainerData data;

    public ContainerSignal(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, new SimpleContainer(1), new SimpleContainerData(5),
                (Signal) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerSignal(int windowId, Inventory playerInv, Container inventory, ContainerData data, Signal te) {
        super(ModContainerTypes.SIGNAL.get(), windowId);
        this.tileEntity = te;
        this.data = data;

        checkContainerSize(inventory, 1);
        addSlot(new UCSlotOutput(inventory, 0, 148, 73));
        layoutPlayerInventorySlots(playerInv, 8, 108);
        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.tileEntity != null && this.tileEntity.stillValid(player);
    }

    public int getSyncedValue(int index) {
        return this.data.get(index);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 0) {
                if (!moveItemStackTo(itemstack1, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    private void layoutPlayerInventorySlots(Inventory playerInventory, int xStart, int yStart) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlot(new Slot(playerInventory, k, xStart + k * 18, yStart + 58));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide && this.tileEntity != null) {
            this.tileEntity.inUseCleanup();
        }
    }

    public Signal getTile() {
        return this.tileEntity;
    }
}