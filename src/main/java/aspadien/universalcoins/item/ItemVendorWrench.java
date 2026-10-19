package aspadien.universalcoins.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import aspadien.universalcoins.container.ContainerVendorWrench;
import aspadien.universalcoins.tileentity.TileVendor;

public class ItemVendorWrench extends Item {

    public ItemVendorWrench(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        PlayerEntity player = context.getPlayer();

        if (player == null) return ActionResultType.PASS;
        if (world.isClientSide) return ActionResultType.SUCCESS;

        TileEntity te = world.getBlockEntity(pos);

        if (te instanceof TileVendor) {
            TileVendor vendor = (TileVendor) te;

            // Создаем провайдер интерфейса "на лету" специально для ключа
            SimpleNamedContainerProvider provider = new SimpleNamedContainerProvider(
                    (windowId, playerInv, playerEntity) -> new ContainerVendorWrench(windowId, playerInv, vendor, vendor.wrenchData),
                    new TranslationTextComponent("vendor.wrench.owner")
            );

            // Открываем GUI и ОБЯЗАТЕЛЬНО передаем pos (чтобы избежать той самой ошибки с пустым пакетом)
            NetworkHooks.openGui((ServerPlayerEntity) player, provider, pos);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}