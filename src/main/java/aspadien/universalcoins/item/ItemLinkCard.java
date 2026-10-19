package aspadien.universalcoins.item;

import aspadien.universalcoins.blockentities.Vendor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemLinkCard extends Item {
    public ItemLinkCard(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("storageLocation")) {
            tooltip.add(Component.translatable("item.link_card.stored").withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("item.link_card.blank").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (world.isClientSide || player == null) return InteractionResult.SUCCESS;

        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof ChestBlockEntity) {
            CompoundTag nbt = stack.getOrCreateTag();
            nbt.putIntArray("storageLocation", new int[]{pos.getX(), pos.getY(), pos.getZ()});

            player.sendSystemMessage(Component.translatable("item.link_card.message.stored")
                    .append(" " + pos.getX() + " " + pos.getY() + " " + pos.getZ())
                    .withStyle(ChatFormatting.AQUA));
            return InteractionResult.SUCCESS;
        }

        if (tile instanceof Vendor vendor) {
            if (stack.hasTag() && stack.getTag().contains("storageLocation")) {
                int[] coords = stack.getTag().getIntArray("storageLocation");
                vendor.setRemoteStorage(coords);

                player.sendSystemMessage(Component.translatable("item.link_card.message.set")
                        .withStyle(ChatFormatting.GOLD));

                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}