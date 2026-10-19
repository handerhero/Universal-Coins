package aspadien.universalcoins.net;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

// В 1.20.1 AbstractCriterionTrigger заменен/наследуется через SimpleCriterionTrigger
public class UCAccountBalanceTrigger extends SimpleCriterionTrigger<UCAccountBalanceTrigger.Instance> {

    // ID триггера (в 1.20.1 используем fromNamespaceAndPath)
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("universalcoins", "account_balance_has_change");

    // Регистрация триггера в реестре ванилы
    public static final UCAccountBalanceTrigger INSTANCE = CriteriaTriggers.register(new UCAccountBalanceTrigger());

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    // Десериализация параметров из JSON
    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate playerPredicate, DeserializationContext context) {
        // JSONUtils переименован в GsonHelper
        long minBalance = GsonHelper.getAsLong(json, "min_balance", 0L);
        return new Instance(playerPredicate, minBalance);
    }

    // Статический метод вызова
    public static void trigger(ServerPlayer player, long currentBalance) {
        INSTANCE.trigger(player, instance -> instance.test(currentBalance));
    }

    // Класс условий (критериев)
    public static class Instance extends AbstractCriterionTriggerInstance {
        private final long minBalance;

        public Instance(ContextAwarePredicate playerPredicate, long minBalance) {
            super(UCAccountBalanceTrigger.ID, playerPredicate);
            this.minBalance = minBalance;
        }

        // Проверяем, подходит ли условие по балансу
        public boolean test(long balance) {
            return balance >= this.minBalance;
        }

        // В 1.20.1 обязательно переопределять сериализацию в JSON
        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("min_balance", this.minBalance);
            return json;
        }
    }
}