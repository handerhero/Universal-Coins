package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.Packager;
import aspadien.universalcoins.init.ModContainerTypes;
import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.item.ItemCoin;
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

public class ContainerPackager extends AbstractContainerMenu {
    private final Packager tileEntity;
    private final ContainerData data;

    public ContainerPackager(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, new SimpleContainer(12), new SimpleContainerData(9),
                (Packager) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerPackager(int windowId, Inventory playerInv, Container inventory, ContainerData data, Packager te) {
        super(ModContainerTypes.PACKAGER.get(), windowId);
        this.tileEntity = te;
        this.data = data;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                int slotIndex = Packager.itemPackageSlots[i * 2 + j];
                addSlot(new Slot(inventory, slotIndex, 8 + i * 18, 22 + j * 18));
            }
        }

        addSlot(new UCSlotCard(inventory, 8, 8, 73));
        addSlot(new UCSlotCoinInput(inventory, 9, 26, 73));
        addSlot(new UCSlotOutput(inventory, 10, 152, 73));
        addSlot(new UCSlotPackage(inventory, 11, 172, 22));

        layoutPlayerInventorySlots(playerInv, 8, 108);
        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.tileEntity != null && this.tileEntity.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 12) {
                if (!moveItemStackTo(itemstack1, 12, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                int packageSize = this.data.get(2);
                int isSendMode = this.data.get(3);

                if (itemstack1.getItem() instanceof ItemCoin) {
                    if (!moveItemStackTo(itemstack1, 9, 10, false)) {
                        if (isSendMode == 0) {
                            if (!moveIntoPackingSlots(itemstack1, packageSize)) return ItemStack.EMPTY;
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (isSendMode == 1 && itemstack1.getItem() == ModItems.UC_PACKAGE.get()) {
                    if (!moveItemStackTo(itemstack1, 11, 12, false)) return ItemStack.EMPTY;
                } else if (slot.mayPlace(itemstack1) && index >= 12) {
                    if (!moveItemStackTo(itemstack1, 8, 9, false)) {
                        if (isSendMode == 0) {
                            if (!moveIntoPackingSlots(itemstack1, packageSize)) return ItemStack.EMPTY;
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (isSendMode == 0) {
                    if (!moveIntoPackingSlots(itemstack1, packageSize)) return ItemStack.EMPTY;
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    private boolean moveIntoPackingSlots(ItemStack stack, int size) {
        int startSlot = (size == 0) ? 4 : ((size == 1) ? 2 : 0);
        return moveItemStackTo(stack, startSlot, 8, false);
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
        if (this.tileEntity != null && !player.level().isClientSide) {
            this.tileEntity.inUseCleanup();
        }
    }

    public int getSyncedValue(int index) {
        return this.data.get(index);
    }

    public Packager getTile() {
        return this.tileEntity;
    }
}