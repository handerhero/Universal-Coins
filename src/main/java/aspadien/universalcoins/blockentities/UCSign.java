package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.block.BlockUCWallSign;
import aspadien.universalcoins.init.ModBlockEntities;
import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.net.UCSignServerMessage;
import aspadien.universalcoins.util.UCUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class UCSign extends SignBlockEntity {
    public String blockOwner = "";

    public UCSign(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UC_SIGN.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.UC_SIGN.get();
    }

    // Вспомогательные методы для работы со строками лицевой стороны таблички в 1.20+
    public Component getMessage(int line) {
        return this.getFrontText().getMessage(line, false);
    }

    public void setMessage(int line, Component message) {
        this.setText(this.getFrontText().setMessage(line, message), true);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putString("blockOwner", this.blockOwner);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.blockOwner = tag.getString("blockOwner");
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    public void updateSign() {
        if (this.level != null && !this.level.isClientSide) {
            setChanged();
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public void sendServerUpdateMessage() {
        Component[] itext = new Component[4];
        for (int i = 0; i < 4; i++) {
            itext[i] = getMessage(i);
        }
        ModPacketHandler.sendToServer(new UCSignServerMessage(this.worldPosition, itext));
    }

    public void scanChestContents(BlockState state, @Nullable BlockPos fromPos) {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        BlockPos targetPos = fromPos;
        if (targetPos == null) {
            if (state.getBlock() instanceof BlockUCWallSign) {
                Direction facing = state.getValue(BlockUCWallSign.FACING);
                targetPos = this.worldPosition.relative(facing.getOpposite());
            } else {
                targetPos = this.worldPosition.below();
            }
        }

        BlockEntity te = this.level.getBlockEntity(targetPos);
        if (te == null) {
            return;
        }

        if (te instanceof Vendor vendor) {
            ItemStack tradeStack = vendor.getItem(9);

            if (tradeStack.isEmpty()) {
                setMessage(0, Component.literal("---"));
                setMessage(1, Component.literal("Out of Order"));
                setMessage(2, Component.literal("---"));
                setMessage(3, Component.literal(""));
            } else {
                setMessage(0, Component.literal(tradeStack.getHoverName().getString()));

                String mode = vendor.sellMode ? "Selling" : "Buying";
                setMessage(1, Component.literal(mode));
                setMessage(2, Component.literal("Price: " + vendor.itemPrice));

                if (vendor.infiniteMode) {
                    setMessage(3, Component.literal("Stock: Infinite"));
                } else {
                    int stock = 0;
                    for (int i = 0; i <= 8; i++) {
                        ItemStack s = vendor.getItem(i);
                        if (!s.isEmpty() && UCUtils.isSameItem(s, tradeStack)) {
                            stock += s.getCount();
                        }
                    }
                    setMessage(3, Component.literal("In Stock: " + stock));
                }
            }
            updateSign();
            return;
        }

        Container inventory = null;
        if (te instanceof Container container) {
            inventory = container;
        } else {
            BlockState targetState = this.level.getBlockState(targetPos);
            if (targetState.getBlock() instanceof ChestBlock chestBlock) {
                inventory = ChestBlock.getContainer(chestBlock, targetState, this.level, targetPos, true);
            }
        }

        if (inventory != null) {
            Map<String, Integer> counts = new LinkedHashMap<>();
            int emptySlots = 0;

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty()) {
                    emptySlots++;
                } else {
                    String name = stack.getHoverName().getString();
                    counts.put(name, counts.getOrDefault(name, 0) + stack.getCount());
                }
            }

            int line = 0;
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                if (line < 4) {
                    setMessage(line++, Component.literal(entry.getValue() + " " + entry.getKey()));
                }
            }
            if (line < 4 && emptySlots > 0) {
                setMessage(line++, Component.literal(emptySlots + " Empty Slots"));
            }
            while (line < 4) {
                setMessage(line++, Component.literal(""));
            }

            updateSign();
        }
    }
}