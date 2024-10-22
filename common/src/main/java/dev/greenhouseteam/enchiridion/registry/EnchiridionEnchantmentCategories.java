package dev.greenhouseteam.enchiridion.registry;

import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.EnchiridionTags;
import dev.greenhouseteam.enchiridion.enchantment.category.EnchantmentCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public class EnchiridionEnchantmentCategories {
    public static final ResourceKey<EnchantmentCategory> PRIMARY = ResourceKey.create(EnchiridionRegistries.ENCHANTMENT_CATEGORY, Enchiridion.asResource("primary"));
    public static final ResourceKey<EnchantmentCategory> SECONDARY = ResourceKey.create(EnchiridionRegistries.ENCHANTMENT_CATEGORY, Enchiridion.asResource("secondary"));
    public static final ResourceKey<EnchantmentCategory> TERTIARY = ResourceKey.create(EnchiridionRegistries.ENCHANTMENT_CATEGORY, Enchiridion.asResource("tertiary"));
    public static final ResourceKey<EnchantmentCategory> CURSE = ResourceKey.create(EnchiridionRegistries.ENCHANTMENT_CATEGORY, Enchiridion.asResource("curse"));

    public static void bootstrap(BootstrapContext<EnchantmentCategory> context) {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        EnchantmentCategory primary = new EnchantmentCategory(
                enchantments.getOrThrow(EnchiridionTags.EnchantmentTags.PRIMARY_CATEGORY),
                Component.translatable("enchiridion.category.enchiridion.primary"),
                Enchiridion.asResource("enchiridion/category/primary"),
                Enchiridion.asResource("enchanted_book_primary"),
                TextColor.fromLegacyFormat(ChatFormatting.GOLD),
                false,
                Optional.of(1),
                30);
        context.register(PRIMARY, primary);
        EnchantmentCategory secondary = new EnchantmentCategory(
                enchantments.getOrThrow(EnchiridionTags.EnchantmentTags.SECONDARY_CATEGORY),
                Component.translatable("enchiridion.category.enchiridion.secondary"),
                Enchiridion.asResource("enchiridion/category/secondary"),
                Enchiridion.asResource("enchanted_book_secondary"),
                TextColor.fromLegacyFormat(ChatFormatting.BLUE),
                false,
                Optional.of(1),
                20);
        context.register(SECONDARY, secondary);
        EnchantmentCategory tertiary = new EnchantmentCategory(
                enchantments.getOrThrow(EnchiridionTags.EnchantmentTags.TERTIARY_CATEGORY),
                Component.translatable("enchiridion.category.enchiridion.tertiary"),
                Enchiridion.asResource("enchiridion/category/tertiary"),
                Enchiridion.asResource("enchanted_book_tertiary"),
                TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN),
                false,
                Optional.of(1),
                10);
        context.register(TERTIARY, tertiary);
        EnchantmentCategory curse = new EnchantmentCategory(
                enchantments.getOrThrow(EnchantmentTags.CURSE),
                Component.translatable("enchiridion.category.enchiridion.curse"),
                Optional.empty(),
                Enchiridion.asResource("enchanted_book_curse"),
                TextColor.fromLegacyFormat(ChatFormatting.RED),
                true,
                Optional.empty(),
                -10);
        context.register(CURSE, curse);
    }
}