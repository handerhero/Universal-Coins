package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.container.ContainerVendor;
import aspadien.universalcoins.container.ContainerVendorBuy;
import aspadien.universalcoins.container.ContainerVendorSell;
import aspadien.universalcoins.init.ModBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VendorFrame extends Vendor {
    private final String[] signLines = new String[] { "", "", "", "" };

    public VendorFrame(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VENDOR_FRAME.get(), pos, state);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        boolean isOwner = player.getName().getString().equals(this.blockOwner);

        if (isOwner) {
            return new ContainerVendor(windowId, playerInv, this, this.vendorData, this);
        }
        return this.sellMode
                ? new ContainerVendorSell(windowId, playerInv, this, this.vendorData, this)
                : new ContainerVendorBuy(windowId, playerInv, this, this.vendorData, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("tile.aspadien.universalcoins.vendor_frame.name");
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level != null && !this.level.isClientSide && this.level.getGameTime() % 40L == 0L) {
            updateSigns();
        }
    }

    public void updateSigns() {
        if (this.level == null || this.level.isClientSide) return;

        ItemStack tradeStack = getItem(9);
        if (tradeStack.isEmpty()) return;

        String colorCode = "&" + Integer.toHexString(this.textColor);
        this.signLines[0] = colorCode + (this.sellMode ? "Selling" : "Buying");

        updateSignAt(this.worldPosition.below());
        updateSignAt(this.worldPosition.above());
    }

    private void updateSignAt(BlockPos targetPos) {
        if (this.level == null || !this.level.isLoaded(targetPos)) return;

        BlockEntity te = this.level.getBlockEntity(targetPos);
        if (te instanceof UCSign ucsign) {
            for (int i = 0; i < 4; i++) {
                ucsign.setMessage(i, Component.literal(this.signLines[i]));
            }
            ucsign.updateSign();
            ucsign.setChanged();
        }
    }
}