package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.ATM;
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

public class ContainerATM extends AbstractContainerMenu {
    private final ATM tileEntity;
    private final ContainerData data;

    public ContainerATM(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, new SimpleContainer(2), new SimpleContainerData(10),
                (ATM) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerATM(int windowId, Inventory playerInv, Container inventory, ContainerData data, ATM te) {
        super(ModContainerTypes.ATM.get(), windowId);
        this.tileEntity = te;
        this.data = data;

        addSlot(new UCSlotCoinInput(inventory, 0, 172, 40));
        addSlot(new UCSlotCard(inventory, 1, 172, 60));

        layoutPlayerInventorySlots(playerInv, 18, 119);
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

            if (index < 2) {
                if (!moveItemStackTo(itemstack1, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.getItem() instanceof ItemCoin) {
                if (!moveItemStackTo(itemstack1, 0, 1, false)) return ItemStack.EMPTY;
            } else if (itemstack1.getItem() == ModItems.UC_CARD.get() || itemstack1.getItem() == ModItems.ENDER_CARD.get()) {
                if (!moveItemStackTo(itemstack1, 1, 2, false)) return ItemStack.EMPTY;
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

    public int getSyncedValue(int index) {
        return this.data.get(index);
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

    public ATM getTile() {
        return this.tileEntity;
    }
}