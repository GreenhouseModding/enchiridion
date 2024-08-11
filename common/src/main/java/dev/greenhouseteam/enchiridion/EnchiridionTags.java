package dev.greenhouseteam.enchiridion;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;



public class EnchiridionTags {

    public static class BlockTags {
        public static final TagKey<Block> BASE_STONE = TagKey.create(Registries.BLOCK, Enchiridion.asResource("base_stone"));
        public static final TagKey<Block> HARDER_STONE = TagKey.create(Registries.BLOCK, Enchiridion.asResource("harder_stone"));
    }

    public static class EnchantmentTags {
        public static final TagKey<Enchantment> PRIMARY_CATEGORY = TagKey.create(Registries.ENCHANTMENT, Enchiridion.asResource("category/primary"));
        public static final TagKey<Enchantment> SECONDARY_CATEGORY = TagKey.create(Registries.ENCHANTMENT, Enchiridion.asResource("category/secondary"));
        public static final TagKey<Enchantment> TERTIARY_CATEGORY = TagKey.create(Registries.ENCHANTMENT, Enchiridion.asResource("category/tertiary"));
        public static final TagKey<Enchantment> UNCATEGORISED_CATEGORY = TagKey.create(Registries.ENCHANTMENT, Enchiridion.asResource("category/uncategorised"));

        public static final TagKey<Enchantment> ELEMENTAL_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, Enchiridion.asResource("exclusive_set/elemental"));
        public static final TagKey<Enchantment> FISHING_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, Enchiridion.asResource("exclusive_set/fishing"));
    }

    public static class EntityTypeTags {
        public static final TagKey<EntityType<?>> IGNORES_BARDING = TagKey.create(Registries.ENTITY_TYPE, Enchiridion.asResource("ignores_barding"));
        public static final TagKey<EntityType<?>> PREVENTS_JOUSTING = TagKey.create(Registries.ENTITY_TYPE, Enchiridion.asResource("prevents_jousting"));
    }

    public static class ItemTags {
        public static final TagKey<Item> ASHES_ENCHANTABLE = TagKey.create(Registries.ITEM, Enchiridion.asResource("enchantable/ashes"));
        public static final TagKey<Item> AXE_ENCHANTABLE = TagKey.create(Registries.ITEM, Enchiridion.asResource("enchantable/axe"));
        public static final TagKey<Item> PICKAXE_ENCHANTABLE = TagKey.create(Registries.ITEM, Enchiridion.asResource("enchantable/pickaxe"));
        public static final TagKey<Item> ICE_STRIKE_ENCHANTABLE = TagKey.create(Registries.ITEM, Enchiridion.asResource("enchantable/ice_strike"));
        public static final TagKey<Item> ICE_STRIKE_PRIMARY_ENCHANTABLE = TagKey.create(Registries.ITEM, Enchiridion.asResource("enchantable/ice_strike_primary"));

        public static final TagKey<Item> INCLUSIVE_ENCHANTABLES = TagKey.create(Registries.ITEM, Enchiridion.asResource("inclusive_enchantables"));
    }

    public static class FluidTags {
        public static final TagKey<Fluid> ACTIVATES_IMPALING = TagKey.create(Registries.FLUID, Enchiridion.asResource("activates_impaling"));
    }

}
