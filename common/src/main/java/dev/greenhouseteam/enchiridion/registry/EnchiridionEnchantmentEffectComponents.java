package dev.greenhouseteam.enchiridion.registry;

import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.enchantment.effects.PreventHungerConsumptionEffect;
import dev.greenhouseteam.enchiridion.enchantment.effects.RidingConditionalEffect;
import dev.greenhouseteam.enchiridion.enchantment.effects.RunFunctionOnLootEffect;
import dev.greenhouseteam.enchiridion.registry.internal.RegistrationCallback;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

public class EnchiridionEnchantmentEffectComponents {
    public static final DataComponentType<List<ConditionalEffect<ResourceKey<LootTable>>>> ADDITIONAL_FISHING_LOOT = DataComponentType.<List<ConditionalEffect<ResourceKey<LootTable>>>>builder()
            .persistent(ConditionalEffect.codec(ResourceKey.codec(Registries.LOOT_TABLE), EnchiridionLootContextParamSets.ENCHANTED_FISHING).listOf())
            .build();
    public static final DataComponentType<List<Optional<LootItemCondition>>> ALLOW_FIRING_WITHOUT_PROJECTILE = DataComponentType.<List<Optional<LootItemCondition>>>builder()
            .persistent(ConditionalEffect.conditionCodec(LootContextParamSets.ENCHANTED_ITEM).optionalFieldOf("").codec().listOf())
            .build();
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> FISHING_EXPERIENCE_BONUS = DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, EnchiridionLootContextParamSets.ENCHANTED_FISHING).listOf())
            .build();
    public static final DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> POST_BLOCK_DROP = DataComponentType.<List<ConditionalEffect<EnchantmentEntityEffect>>>builder()
            .persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, EnchiridionLootContextParamSets.ENCHANTED_BLOCK_DROP).listOf())
            .build();
    public static final DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>> POST_ENTITY_DROP = DataComponentType.<List<ConditionalEffect<EnchantmentEntityEffect>>>builder()
            .persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, EnchiridionLootContextParamSets.ENCHANTED_ENTITY_DROP).listOf())
            .build();
    public static final DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>> POST_SHIELD_DISABLE = DataComponentType.<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>builder()
            .persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
            .build();
    public static final DataComponentType<List<ConditionalEffect<PreventHungerConsumptionEffect>>> PREVENT_HUNGER_CONSUMPTION = DataComponentType.<List<ConditionalEffect<PreventHungerConsumptionEffect>>>builder()
            .persistent(ConditionalEffect.codec(PreventHungerConsumptionEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf())
            .build();
    public static final DataComponentType<List<ConditionalEffect<List<RunFunctionOnLootEffect>>>> RUN_FUNCTIONS_ON_FISHING_LOOT = DataComponentType.<List<ConditionalEffect<List<RunFunctionOnLootEffect>>>>builder()
            .persistent(ConditionalEffect.codec(RunFunctionOnLootEffect.CODEC.listOf(), EnchiridionLootContextParamSets.ENCHANTED_FISHING).listOf())
            .build();
    public static final DataComponentType<List<ConditionalEffect<EnchantmentLocationBasedEffect>>> TARGET_BLOCK_CHANGED = DataComponentType.<List<ConditionalEffect<EnchantmentLocationBasedEffect>>>builder()
            .persistent(ConditionalEffect.codec(EnchantmentLocationBasedEffect.CODEC, EnchiridionLootContextParamSets.ENCHANTED_BLOCK).listOf())
            .build();
    public static final DataComponentType<List<RidingConditionalEffect<EnchantmentLocationBasedEffect>>> VEHICLE_CHANGED = DataComponentType.<List<RidingConditionalEffect<EnchantmentLocationBasedEffect>>>builder()
            .persistent(RidingConditionalEffect.codec(EnchantmentLocationBasedEffect.CODEC, EnchiridionLootContextParamSets.VEHICLE_ENCHANTED_DAMAGE).listOf())
            .build();
    public static final DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> VEHICLE_DAMAGE_PROTECTION = DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, EnchiridionLootContextParamSets.VEHICLE_ENCHANTED_DAMAGE).listOf())
            .build();

    public static void registerAll(RegistrationCallback<DataComponentType<?>> callback) {
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("additional_fishing_loot"), ADDITIONAL_FISHING_LOOT);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("allow_firing_without_projectile"), ALLOW_FIRING_WITHOUT_PROJECTILE);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("fishing_experience_bonus"), FISHING_EXPERIENCE_BONUS);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("post_block_drop"), POST_BLOCK_DROP);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("post_entity_drop"), POST_ENTITY_DROP);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("post_shield_disable"), POST_SHIELD_DISABLE);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("prevent_hunger_consumption"), PREVENT_HUNGER_CONSUMPTION);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("run_functions_on_block_loot"), RUN_FUNCTIONS_ON_FISHING_LOOT);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("run_functions_on_fishing_loot"), RUN_FUNCTIONS_ON_FISHING_LOOT);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("target_block_changed"), TARGET_BLOCK_CHANGED);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("vehicle_changed"), VEHICLE_CHANGED);
        callback.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Enchiridion.asResource("vehicle_damage_protection"), VEHICLE_DAMAGE_PROTECTION);
    }
}
