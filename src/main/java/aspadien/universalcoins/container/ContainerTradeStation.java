package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.TradeStation;
import aspadien.universalcoins.init.ModContainerTypes;
import aspadien.universalcoins.util.UCUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerTradeStation extends AbstractContainerMenu {
    private final TradeStation tileEntity;
    private final ContainerData data;

    public ContainerTradeStation(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, extraData.readBlockPos());
    }

    private ContainerTradeStation(int windowId, Inventory playerInv, BlockPos pos) {
        this(windowId, playerInv, (TradeStation) playerInv.player.level().getBlockEntity(pos));
    }

    private ContainerTradeStation(int windowId, Inventory playerInv, TradeStation te) {
        this(windowId, playerInv, (Container) te, te != null ? te.tradeData : new SimpleContainerData(13), te);
    }

    public ContainerTradeStation(int windowId, Inventory playerInv, Container inventory, ContainerData data, TradeStation te) {
        super(ModContainerTypes.TRADE_STATION.get(), windowId);
        this.tileEntity = te;
        this.data = data;

        addSlot(new UCSlotCard(inventory, 2, 12, 27));
        addSlot(new Slot(inventory, 0, 31, 27));
        addSlot(new UCSlotOutput(inventory, 1, 155, 27));

        layoutPlayerInventorySlots(playerInv, 12, 119);
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

            if (index < 3) {
                if (!moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (UCUtils.isCard(itemstack1)) {
                if (!moveItemStackTo(itemstack1, 0, 1, false)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(itemstack1, 1, 2, false)) {
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

    public TradeStation getTile() {
        return this.tileEntity;
    }
}