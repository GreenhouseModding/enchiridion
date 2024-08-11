package dev.greenhouseteam.enchiridion.fabric.util;

import dev.greenhouseteam.enchiridion.EnchiridionTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.Map;

public class TagUtil {
    // Vanilla loads tags after Loot Tables are loaded, so we need to do something about that.
    private static Collection<Holder<Block>> EARLY_BASE_STONE_TAG;

    public static boolean isInBaseStoneTag(Holder<Block> block, ResourceManager manager, HolderLookup<Block> lookup) {
        if (EARLY_BASE_STONE_TAG == null) {
            TagLoader<Holder<Block>> loader = new TagLoader<>(rl -> lookup.get(ResourceKey.create(Registries.BLOCK, rl)), "tags/block");
            Map<ResourceLocation, Collection<Holder<Block>>> loaded = loader.loadAndBuild(manager);
            EARLY_BASE_STONE_TAG = loaded.get(EnchiridionTags.BlockTags.BASE_STONE.location());
        }

        if (EARLY_BASE_STONE_TAG == null)
            return false;
        return EARLY_BASE_STONE_TAG.contains(block);
    }

    public static void resetEarlyBaseStoneTag() {
        EARLY_BASE_STONE_TAG = null;
    }
}
