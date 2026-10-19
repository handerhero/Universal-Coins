package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.PowerReceiver;
import aspadien.universalcoins.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerPowerReceiver extends AbstractContainerMenu {
    private final PowerReceiver tileEntity;
    private final ContainerData data;

    public ContainerPowerReceiver(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, new SimpleContainer(3), new SimpleContainerData(7),
                (PowerReceiver) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerPowerReceiver(int windowId, Inventory playerInv, Container inventory, ContainerData data, PowerReceiver te) {
        super(ModContainerTypes.POWER_RECEIVER.get(), windowId);
        this.tileEntity = te;
        this.data = data;

        addSlot(new UCSlotCard(inventory, 0, 14, 70));
        addSlot(new UCSlotCoinInput(inventory, 1, 32, 70));
        addSlot(new UCSlotOutput(inventory, 2, 148, 70));

        layoutPlayerInventorySlots(playerInv, 8, 103);
        addDataSlots(data);
    }

    public int getSyncedValue(int index) {
        return this.data.get(index);
    }

    private void syncClient(Player player) {
        if (player instanceof ServerPlayer) {
            this.broadcastChanges();
            this.sendAllDataToRemote();
        }
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
            if (index < 3) {
                if (!moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
                    syncClient(player);
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 0, 2, false)) {
                syncClient(player);
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        syncClient(player);
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

    public PowerReceiver getTile() {
        return this.tileEntity;
    }
}