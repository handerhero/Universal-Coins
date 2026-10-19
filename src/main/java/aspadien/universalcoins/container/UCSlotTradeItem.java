package aspadien.universalcoins.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class UCSlotTradeItem extends Slot {
    public UCSlotTradeItem(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.container.canPlaceItem(this.getSlotIndex(), stack);
    }
}