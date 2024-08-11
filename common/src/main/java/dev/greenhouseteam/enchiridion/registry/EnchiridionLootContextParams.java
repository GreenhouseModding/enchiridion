package dev.greenhouseteam.enchiridion.registry;

import dev.greenhouseteam.enchiridion.Enchiridion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class EnchiridionLootContextParams {
    public static final LootContextParam<ItemEntity> ITEM_ENTITY = new LootContextParam<>(Enchiridion.asResource("item_entity"));
    public static final LootContextParam<Entity> FIRST_PASSENGER = new LootContextParam<>(Enchiridion.asResource("first_passenger"));
    public static final LootContextParam<Entity> VEHICLE = new LootContextParam<>(Enchiridion.asResource("vehicle"));
}
