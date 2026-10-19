package aspadien.universalcoins.container;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class ContainerVendorWrench extends AbstractContainerMenu {
    private final Vendor tileEntity;
    private final ContainerData data;

    public ContainerVendorWrench(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, (Vendor) playerInv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(1));
    }

    public ContainerVendorWrench(int windowId, Inventory playerInv, Vendor te, ContainerData data) {
        super(ModContainerTypes.VENDOR_WRENCH.get(), windowId);
        this.tileEntity = te;
        this.data = data;
        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.tileEntity != null && this.tileEntity.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide && this.tileEntity != null) {
            this.tileEntity.inUseCleanup();
        }
    }

    public Vendor getTile() {
        return this.tileEntity;
    }
}