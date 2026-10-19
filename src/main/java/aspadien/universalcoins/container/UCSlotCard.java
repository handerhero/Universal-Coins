package aspadien.universalcoins.container;

import aspadien.universalcoins.init.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class UCSlotCard extends Slot {
    public UCSlotCard(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        Item item = stack.getItem();
        return (item == ModItems.UC_CARD.get() || item == ModItems.ENDER_CARD.get());
    }
}