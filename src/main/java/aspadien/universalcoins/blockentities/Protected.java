package aspadien.universalcoins.blockentities;

import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.net.UCButtonMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Protected extends BlockEntity {
  public String blockOwner = "none";
  public String playerName = "";
  public boolean inUse = false;

  public Protected(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    this.inUse = tag.getBoolean("InUse");
    this.playerName = tag.contains("playerName") ? tag.getString("playerName") : "none";
    this.blockOwner = tag.contains("blockOwner") ? tag.getString("blockOwner") : "none";
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putBoolean("InUse", this.inUse);
    tag.putString("playerName", this.playerName);
    tag.putString("blockOwner", this.blockOwner);
  }

  public void inUseCleanup() {
    if (this.level != null && this.level.isClientSide) return;
    this.inUse = false;
  }

  @Override
  public @NotNull CompoundTag getUpdateTag() {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag);
    return tag;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    load(tag);
  }

  @Nullable
  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
    CompoundTag tag = pkt.getTag();
    if (tag != null) {
      load(tag);
    }
  }

  public void updateTE() {
    if (this.level != null) {
      BlockState state = getBlockState();
      this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
      setChanged();
    }
  }

  public void sendButtonMessage(int functionID, boolean shiftPressed) {
    sendButtonMessage(functionID, shiftPressed, 0);
  }

  public void sendButtonMessage(int functionID, boolean shiftPressed, int mouseButton) {
    ModPacketHandler.sendToServer(new UCButtonMessage(this.worldPosition, functionID, shiftPressed, mouseButton));
  }
}