package dev.greenhouseteam.enchiridion.registry;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class EnchiridionLootContextParamSets {
    public static final LootContextParamSet ENCHANTED_BLOCK = LootContextParamSet.builder()
            .required(LootContextParams.BLOCK_STATE)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.TOOL)
            .optional(LootContextParams.THIS_ENTITY)
            .optional(LootContextParams.BLOCK_ENTITY)
            .build();

    public static final LootContextParamSet ENCHANTED_BLOCK_DROP = LootContextParamSet.builder()
            .required(LootContextParams.TOOL)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(EnchiridionLootContextParams.ITEM_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.BLOCK_STATE)
            .optional(LootContextParams.THIS_ENTITY)
            .optional(LootContextParams.BLOCK_ENTITY)
            .build();

    public static final LootContextParamSet ENCHANTED_ENTITY_DROP = LootContextParamSet.builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(EnchiridionLootContextParams.ITEM_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.DAMAGE_SOURCE)
            .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .optional(LootContextParams.ATTACKING_ENTITY)
            .build();

    public static final LootContextParamSet ENCHANTED_FISHING = LootContextParamSet.builder()
            .required(LootContextParams.TOOL)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .optional(LootContextParams.THIS_ENTITY)
            .build();

    public static final LootContextParamSet VEHICLE_ENCHANTED = LootContextParamSet.builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(EnchiridionLootContextParams.VEHICLE)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .optional(EnchiridionLootContextParams.FIRST_PASSENGER)
            .build();

    public static final LootContextParamSet VEHICLE_ENCHANTED_DAMAGE = LootContextParamSet.builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(EnchiridionLootContextParams.VEHICLE)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.DAMAGE_SOURCE)
            .optional(EnchiridionLootContextParams.FIRST_PASSENGER)
            .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .optional(LootContextParams.ATTACKING_ENTITY)
            .build();
}
