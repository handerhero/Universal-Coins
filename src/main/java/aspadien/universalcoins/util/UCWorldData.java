package aspadien.universalcoins.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

public class UCWorldData extends SavedData {
    private CompoundTag nbt = new CompoundTag();

    // Конструктор по умолчанию без параметров (super() не принимает строку)
    public UCWorldData() {
        super();
    }

    public static UCWorldData getInstance() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null; // Защита от вызова на стороне чистого клиента или до старта сервера
        }

        ServerLevel world = server.overworld();

        // В 1.20.1 computeIfAbsent принимает: (Loader, Factory, Имя)
        return world.getDataStorage().computeIfAbsent(UCWorldData::load, UCWorldData::new, MOD_ID);
    }

    // Загрузка теперь статическая: принимает тег и возвращает готовый объект
    public static UCWorldData load(CompoundTag compound) {
        UCWorldData data = new UCWorldData();
        if (compound.contains(MOD_ID)) {
            data.nbt = compound.getCompound(MOD_ID);
        } else {
            data.nbt = new CompoundTag();
        }
        return data;
    }

    // Сохранение переопределяет метод базового класса SavedData
    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.put(MOD_ID, this.nbt);
        return compound;
    }

    public CompoundTag getData() {
        return this.nbt;
    }

    // Оставляем как алиас, чтобы не переписывать вызовы в остальном коде
    public void markDirty() {
        this.setDirty();
    }
}