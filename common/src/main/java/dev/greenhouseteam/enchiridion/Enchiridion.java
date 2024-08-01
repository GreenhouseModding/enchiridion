package dev.greenhouseteam.enchiridion;

import dev.greenhouseteam.enchiridion.platform.EnchiridionPlatformHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Enchiridion {
    public static final String MOD_ID = "enchiridion";
    public static final String MOD_NAME = "Enchiridion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static EnchiridionPlatformHelper helper;

    public static void init(EnchiridionPlatformHelper helper) {
        Enchiridion.helper = helper;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static EnchiridionPlatformHelper getHelper() {
        return helper;
    }

    public static class BlockTags {
        public static final TagKey<Block> BASE_STONE = TagKey.create(Registries.BLOCK, asResource("base_stone"));
        public static final TagKey<Block> HARDER_STONE = TagKey.create(Registries.BLOCK, asResource("harder_stone"));
    }

    public static class EnchantmentTags {
        public static final TagKey<Enchantment> PRIMARY_CATEGORY = TagKey.create(Registries.ENCHANTMENT, asResource("category/primary"));
        public static final TagKey<Enchantment> SECONDARY_CATEGORY = TagKey.create(Registries.ENCHANTMENT, asResource("category/secondary"));
        public static final TagKey<Enchantment> TERTIARY_CATEGORY = TagKey.create(Registries.ENCHANTMENT, asResource("category/tertiary"));
        public static final TagKey<Enchantment> UNCATEGORISED_CATEGORY = TagKey.create(Registries.ENCHANTMENT, asResource("category/uncategorised"));

        public static final TagKey<Enchantment> ELEMENTAL_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, asResource("exclusive_set/elemental"));
    }

    public static class EntityTypeTags {
        public static final TagKey<EntityType<?>> IGNORES_BARDING = TagKey.create(Registries.ENTITY_TYPE, asResource("ignores_barding"));
        public static final TagKey<EntityType<?>> PREVENTS_JOUSTING = TagKey.create(Registries.ENTITY_TYPE, asResource("prevents_jousting"));
    }

    public static class ItemTags {
        public static final TagKey<Item> ASHES_ENCHANTABLE = TagKey.create(Registries.ITEM, asResource("enchantable/ashes"));
        public static final TagKey<Item> AXE_ENCHANTABLE = TagKey.create(Registries.ITEM, asResource("enchantable/axe"));
        public static final TagKey<Item> PICKAXE_ENCHANTABLE = TagKey.create(Registries.ITEM, asResource("enchantable/pickaxe"));
        public static final TagKey<Item> ICE_STRIKE_ENCHANTABLE = TagKey.create(Registries.ITEM, asResource("enchantable/ice_strike"));
        public static final TagKey<Item> ICE_STRIKE_PRIMARY_ENCHANTABLE = TagKey.create(Registries.ITEM, asResource("enchantable/ice_strike_primary"));
    }

    public static class FluidTags {
        public static final TagKey<Fluid> ACTIVATES_IMPALING = TagKey.create(Registries.FLUID, asResource("activates_impaling"));
    }
}