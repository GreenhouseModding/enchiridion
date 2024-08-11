package dev.greenhouseteam.enchiridion.util;

import dev.greenhouseteam.enchiridion.enchantment.category.EnchantmentCategory;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TooltipUtil {

    public static void modifyEnchantmentTooltips(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag flag, List<Component> components) {
        if (tooltipContext.registries() == null)
            return;

        ItemEnchantmentCategories categories = stack.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY);

        List<Component> enchantmentComponents = new ArrayList<>();
        ItemEnchantments enchantments = EnchiridionUtil.getEnchantmentsOrStoredEnchantments(stack);
        if (enchantments == null)
            return;

        enchantments.addToTooltip(tooltipContext, enchantmentComponents::add, flag);

        components.sort((o1, o2) -> {
            if (!enchantmentComponents.contains(o1) || !enchantmentComponents.contains(o2))
                return Integer.compare(components.indexOf(o1), components.indexOf(o2));

            Optional<Holder.Reference<Enchantment>> o1Enchantment = tooltipContext.registries().lookupOrThrow(Registries.ENCHANTMENT).filterElements(e -> o1.contains(e.description())).listElements().findFirst();
            Optional<Holder.Reference<Enchantment>> o2Enchantment = tooltipContext.registries().lookupOrThrow(Registries.ENCHANTMENT).filterElements(e -> o2.contains(e.description())).listElements().findFirst();

            if (o1Enchantment.isEmpty() || o2Enchantment.isEmpty())
                return Integer.compare(components.indexOf(o1), components.indexOf(o2));

            Holder<EnchantmentCategory> category = categories.findFirstCategory(o1Enchantment.get());
            Holder<EnchantmentCategory> category2 = categories.findFirstCategory(o2Enchantment.get());

            int categoryPriority = 0;
            int category2Priority = 0;

            if (category != null && category.isBound())
                categoryPriority = category.value().priority();

            if (category2 != null && category2.isBound())
                category2Priority = category2.value().priority();

            return Integer.compare(category2Priority, categoryPriority);
        });

        for (int i = 0; i < components.size(); ++i) {
            Component component = components.get(i);
            if (enchantmentComponents.contains(component)) {
                Optional<Holder.Reference<Enchantment>> enchantment = tooltipContext.registries().lookupOrThrow(Registries.ENCHANTMENT).filterElements(e -> component.contains(e.description())).listElements().findFirst();
                if (enchantment.isEmpty())
                    return;
                Holder<EnchantmentCategory> category = categories.findFirstCategory(enchantment.get());
                if (category == null || !category.isBound())
                    return;
                components.set(i, components.get(i).copy().withColor(category.value().color().getValue()));
            }
        }
    }
}
