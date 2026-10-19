package aspadien.universalcoins.init;

import aspadien.universalcoins.net.UCAccountBalanceTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class Triggers {

    public static final UCAccountBalanceTrigger BALANCE_CHANGE = new UCAccountBalanceTrigger();

    public static void register() {
        // Регистрируем в ванильную систему Minecraft
        CriteriaTriggers.register(BALANCE_CHANGE);
    }
}
