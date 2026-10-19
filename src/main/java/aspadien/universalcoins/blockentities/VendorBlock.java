package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VendorBlock extends Vendor {
  private final String[] signLines;

  public VendorBlock(BlockPos pos, BlockState state) {
    super(ModBlockEntities.VENDOR_BLOCK.get(), pos, state);
    this.signLines = new String[] { "", "", "", "" };
  }

  public void updateSigns() {
    if (this.level == null || this.level.isClientSide) return;
    ItemStack tradeStack = getItem(9);
    if (!tradeStack.isEmpty()) {
      String colorCode = "&" + Integer.toHexString(this.textColor);

      this.signLines[0] = colorCode + (this.sellMode ? "Selling" : "Buying");

      if (!this.infiniteMode) {
        if (this.sellMode && this.ooStockWarning) {
          this.signLines[0] = colorCode + Component.translatable("sign.warning.stock").getString();
        } else if (!this.sellMode && this.ooCoinsWarning) {
          this.signLines[0] = colorCode + Component.translatable("sign.warning.coins").getString();
        } else if (!this.sellMode && this.inventoryFullWarning) {
          this.signLines[0] = colorCode + Component.translatable("sign.warning.inventoryfull").getString();
        }
      }

      String displayName = tradeStack.getHoverName().getString();
      this.signLines[1] = colorCode + ((tradeStack.getCount() > 1) ? (tradeStack.getCount() + " " + displayName) : displayName);

      this.signLines[2] = "";
      if (tradeStack.getItem() == ModItems.UC_PACKAGE.get()) {
        this.signLines[2] = colorCode;
        CompoundTag packageTag = tradeStack.getTag();
        if (packageTag != null && packageTag.contains("Inventory", 9)) {
          ListTag itemList = packageTag.getList("Inventory", 10);
          for (int i = 0; i < itemList.size(); i++) {
            ItemStack innerStack = ItemStack.of(itemList.getCompound(i));
            this.signLines[2] = this.signLines[2] + innerStack.getCount() + ":" + innerStack.getHoverName().getString() + " ";
          }
        }
      }

      this.signLines[3] = colorCode + "Price: " + this.itemPrice;

      updateAdjacentSigns();
    }
  }

  private void updateAdjacentSigns() {
    for (Direction dir : Direction.Plane.HORIZONTAL) {
      BlockPos signPos = this.worldPosition.relative(dir).below();
      BlockEntity te = this.level.getBlockEntity(signPos);

      if (te instanceof UCSign ucsign) {
        for (int i = 0; i < 4; i++) {
          ucsign.setMessage(i, Component.literal(this.signLines[i]));
        }
        ucsign.updateSign();
        ucsign.setChanged();
      }
    }
  }

  @Override
  public void tick() {
    super.tick();
  }
}