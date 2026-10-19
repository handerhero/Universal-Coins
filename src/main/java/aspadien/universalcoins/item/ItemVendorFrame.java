package aspadien.universalcoins.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemFrameItem;

public class ItemVendorFrame extends ItemFrameItem {
    // Если рамка ванильного типа:
    public ItemVendorFrame(Item.Properties properties) {
        super(EntityType.ITEM_FRAME, properties);
    }

    // Если у вас зарегистрирована своя кастомная сущность рамки:
    public ItemVendorFrame(EntityType<? extends ItemFrame> entityType, Item.Properties properties) {
        super(entityType, properties);
    }
}