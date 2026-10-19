package aspadien.universalcoins.item;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.container.ContainerVendorWrench;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

public class ItemVendorWrench extends Item {

    public ItemVendorWrench(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;
        if (world.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof Vendor vendor) {
            // SimpleNamedContainerProvider -> SimpleMenuProvider
            SimpleMenuProvider provider = new SimpleMenuProvider(
                    (windowId, playerInv, playerEntity) -> new ContainerVendorWrench(windowId, playerInv, vendor, vendor.wrenchData),
                    Component.translatable("vendor.wrench.owner")
            );

            // NetworkHooks.openGui -> openScreen
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, provider, pos);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}