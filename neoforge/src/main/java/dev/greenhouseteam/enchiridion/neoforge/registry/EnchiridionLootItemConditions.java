package dev.greenhouseteam.enchiridion.neoforge.registry;

import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.neoforge.loot.condition.BaseStoneTableCondition;
import dev.greenhouseteam.enchiridion.registry.internal.RegistrationCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class EnchiridionLootItemConditions {
    public static void registerAll(RegistrationCallback<LootItemConditionType> callback) {
        callback.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Enchiridion.asResource("base_stone_table"), BaseStoneTableCondition.TYPE);
    }
}
