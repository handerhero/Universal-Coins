package aspadien.universalcoins.container;

import aspadien.universalcoins.item.ItemCoin;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class UCSlotCoinInput extends Slot {
    public UCSlotCoinInput(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof ItemCoin;
    }
}