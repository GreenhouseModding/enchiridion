package dev.greenhouseteam.enchiridion.fabric.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public class TagLoaderMixin {
    @Shadow @Final private String directory;

    @ModifyVariable(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("HEAD"), argsOnly = true)
    private Map<ResourceLocation, List<TagLoader.EntryWithSource>> enchiridion$buildEnchantmentTags(Map<ResourceLocation, List<TagLoader.EntryWithSource>> original) {
        if (this.directory.equals("tags/enchantment")) {
            original.get(EnchantmentTags.BOW_EXCLUSIVE.location()).removeIf(source -> source.source().equals("vanilla") && ((TagEntryAccessor)source.entry()).enchiridion$getId().equals(Enchantments.MENDING.location()));
        }
        return original;
    }
}
