package dev.greenhouseteam.enchiridion.neoforge.util;

import dev.greenhouseteam.enchiridion.enchantment.category.EnchantmentCategory;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.neoforge.EnchiridionNeoForge;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import dev.greenhouseteam.enchiridion.util.EnchiridionUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class AllowEnchantingUtil {
    public static boolean shouldCancelEnchanting(Holder<Enchantment> enchantment, ItemStack target) {
        ItemEnchantmentCategories categories = target.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY);
        Holder<EnchantmentCategory> category = EnchiridionUtil.getFirstEnchantmentCategory(EnchiridionNeoForge.getRegistryAccess(), enchantment);

        return category != null && !EnchiridionUtil.categoryAcceptsNewEnchantmentsWithValue(category, categories, enchantment);
    }
}
