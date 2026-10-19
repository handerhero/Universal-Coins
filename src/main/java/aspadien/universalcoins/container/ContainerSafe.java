package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.Safe;
import aspadien.universalcoins.init.ModContainerTypes;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UniversalAccounts;
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

public class ContainerSafe extends AbstractContainerMenu {
    private final Safe tileEntity;
    private final ContainerData data;

    public ContainerSafe(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, new SimpleContainer(2), new SimpleContainerData(2),
                (Safe) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerSafe(int windowId, Inventory playerInv, Container inventory, ContainerData data, Safe te) {
        super(ModContainerTypes.SAFE.get(), windowId);
        this.tileEntity = te;
        this.data = data;

        addSlot(new Slot(inventory, 0, 27, 37));

        addSlot(new Slot(inventory, 1, 134, 37) {
            @Override
            public void onTake(Player player, ItemStack stack) {
                if (!player.level().isClientSide) {
                    int val = CoinUtils.getCoinValue(stack);
                    if (UniversalAccounts.getInstance().debitAccount(ContainerSafe.this.tileEntity.currentSessionAccount, val, false)) {
                        ContainerSafe.this.tileEntity.refreshAccountData();
                        ContainerSafe.this.syncClient(player);
                    } else {
                        stack.setCount(0);
                    }
                }
                super.onTake(player, stack);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        layoutPlayerInventorySlots(playerInv, 8, 70);
        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 1) {
                int coinVal = CoinUtils.getCoinValue(stackInSlot);
                int space = getSpaceForStack(player.getInventory(), stackInSlot);
                int maxAffordable = (coinVal > 0) ? (int) (this.tileEntity.accountBalance / coinVal) : 0;
                int amountToTake = Math.min(Math.min(space, maxAffordable), 64);

                if (amountToTake > 0 && !player.level().isClientSide) {
                    long totalCost = (long) coinVal * amountToTake;
                    if (UniversalAccounts.getInstance().debitAccount(this.tileEntity.currentSessionAccount, totalCost, false)) {
                        ItemStack resultStack = stackInSlot.copy();
                        resultStack.setCount(amountToTake);
                        if (moveItemStackTo(resultStack, 2, 38, true)) {
                            slot.set(ItemStack.EMPTY);
                            this.tileEntity.refreshAccountData();
                            syncClient(player);
                        }
                    }
                }

                return ItemStack.EMPTY;
            }

            if (index == 0) {
                if (!moveItemStackTo(stackInSlot, 2, 38, true)) return ItemStack.EMPTY;
            } else if (CoinUtils.getCoinValue(stackInSlot) > 0) {
                if (!moveItemStackTo(stackInSlot, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    private void syncClient(Player player) {
        if (player instanceof ServerPlayer) {
            this.broadcastChanges();
            this.sendAllDataToRemote();
        }
    }

    private int getSpaceForStack(Inventory inv, ItemStack stack) {
        int space = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack invStack = inv.getItem(i);
            if (invStack.isEmpty()) {
                space += 64;
            } else if (ItemStack.isSameItemSameTags(invStack, stack)) {
                space += 64 - invStack.getCount();
            }
        }
        return space;
    }

    public int getSyncedValue(int index) {
        return this.data.get(index);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.tileEntity != null && this.tileEntity.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide && this.tileEntity != null) {
            this.tileEntity.inUseCleanup();
        }
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

    public Safe getTile() {
        return this.tileEntity;
    }
}