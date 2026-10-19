package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.init.ModContainerTypes;
import aspadien.universalcoins.util.UCUtils;
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

public class ContainerVendorSell extends AbstractContainerMenu {
    private final Vendor tileEntity;
    private final ContainerData data;

    public ContainerVendorSell(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, new SimpleContainer(17), new SimpleContainerData(20),
                (Vendor) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerVendorSell(int windowId, Inventory playerInv, Container inventory, ContainerData data, Vendor te) {
        super(ModContainerTypes.VENDOR_SELL.get(), windowId);
        this.tileEntity = te;
        this.data = data;

        addSlot(new UCSlotTradeBuyer(inventory, 9, 8, 24));
        addSlot(new Slot(inventory, 11, 26, 24));
        addSlot(new UCSlotCard(inventory, 16, 43, 64));
        addSlot(new UCSlotOutput(inventory, 13, 152, 64));

        layoutPlayerInventorySlots(playerInv, 8, 117);
        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 4) {
                if (index == 0) return ItemStack.EMPTY;
                if (!moveItemStackTo(itemstack1, 4, this.slots.size(), true)) return ItemStack.EMPTY;
            } else if (UCUtils.isCard(itemstack1)) {
                if (!moveItemStackTo(itemstack1, 2, 3, false)) return ItemStack.EMPTY;
            } else {
                ItemStack sample = this.tileEntity.getItem(9);
                if (!sample.isEmpty() && UCUtils.isSameItem(itemstack1, sample)) {
                    if (!moveItemStackTo(itemstack1, 1, 2, false)) return ItemStack.EMPTY;
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

    private void layoutPlayerInventorySlots(Inventory playerInv, int x, int y) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInv, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            addSlot(new Slot(playerInv, k, x + k * 18, y + 58));
        }
    }

    @Override
    public boolean stillValid(Player p) {
        return this.tileEntity != null && this.tileEntity.stillValid(p);
    }

    public int getSyncedValue(int i) {
        return this.data.get(i);
    }

    public Vendor getTile() {
        return this.tileEntity;
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);

        if (this.tileEntity != null && !playerIn.level().isClientSide) {
            this.tileEntity.inUseCleanup();
        }
    }
}