package aspadien.universalcoins.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemPackage extends Item {
    public ItemPackage(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("Inventory", Tag.TAG_LIST)) {
            ListTag tagList = stack.getTag().getList("Inventory", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                ItemStack innerStack = ItemStack.of(tagList.getCompound(i));
                if (!innerStack.isEmpty()) {
                    tooltip.add(Component.literal(innerStack.getCount() + " x ")
                            .append(innerStack.getHoverName())
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (!world.isClientSide && heldItem.hasTag() && heldItem.getTag().contains("Inventory", Tag.TAG_LIST)) {
            ListTag tagList = heldItem.getTag().getList("Inventory", Tag.TAG_COMPOUND);

            List<ItemStack> toSpawn = new ArrayList<>();
            for (int i = 0; i < tagList.size(); i++) {
                ItemStack s = ItemStack.of(tagList.getCompound(i));
                if (!s.isEmpty()) {
                    toSpawn.add(s.copy());
                }
            }

            heldItem.setCount(0);
            player.setItemInHand(hand, ItemStack.EMPTY);

            for (ItemStack stack : toSpawn) {
                ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack);
                itemEntity.setNoPickUpDelay();
                itemEntity.setDeltaMovement(Vec3.ZERO);
                world.addFreshEntity(itemEntity);
            }

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.containerMenu.broadcastChanges();
            }

            return InteractionResultHolder.consume(ItemStack.EMPTY);
        }

        return InteractionResultHolder.sidedSuccess(heldItem, world.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }
}