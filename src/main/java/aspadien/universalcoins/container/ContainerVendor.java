package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.init.ModContainerTypes;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UCUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerVendor extends AbstractContainerMenu {
    private final Vendor tileEntity;
    private final ContainerData data;

    public ContainerVendor(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, new SimpleContainer(17), new SimpleContainerData(20),
                (Vendor) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerVendor(int windowId, Inventory playerInv, Container inventory, ContainerData data, Vendor te) {
        super(ModContainerTypes.VENDOR.get(), windowId);
        this.tileEntity = te;
        this.data = data;

        checkContainerSize(inventory, 17);

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 96));
        }

        addSlot(new UCSlotTradeOwner(inventory, 9, 9, 17));
        addSlot(new UCSlotCoinInput(inventory, 14, 35, 55));
        addSlot(new UCSlotCard(inventory, 10, 17, 55));
        addSlot(new UCSlotOutput(inventory, 13, 152, 55));

        layoutPlayerInventorySlots(playerInv, 8, 119);
        addDataSlots(data);
    }

    public int getSyncedValue(int index) {
        return this.data.get(index);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.tileEntity != null && this.tileEntity.stillValid(player);
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot instanceof UCSlotTradeOwner) {
                ItemStack cursorStack = this.getCarried();
                if (this.tileEntity != null) {
                    this.tileEntity.setGhostSlot(cursorStack);
                    slot.set(this.tileEntity.getItem(9));
                }
                return;
            }
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 9) return ItemStack.EMPTY;

            if (index < 13) {
                if (!moveItemStackTo(itemstack1, 13, this.slots.size(), true)) return ItemStack.EMPTY;
            } else if (UCUtils.isCard(itemstack1)) {
                if (!moveItemStackTo(itemstack1, 11, 12, false)) return ItemStack.EMPTY;
            } else if (CoinUtils.getCoinValue(itemstack1) > 0) {
                if (!moveItemStackTo(itemstack1, 10, 11, false)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(itemstack1, 0, 9, false)) {
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
    public void removed(Player playerIn) {
        super.removed(playerIn);

        if (this.tileEntity != null && !playerIn.level().isClientSide) {
            this.tileEntity.inUseCleanup();
        }
    }

    public Vendor getTile() {
        return this.tileEntity;
    }
}