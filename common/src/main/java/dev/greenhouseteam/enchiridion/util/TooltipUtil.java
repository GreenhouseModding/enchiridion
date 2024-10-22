package dev.greenhouseteam.enchiridion.util;

import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.enchantment.category.EnchantmentCategory;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.mixin.integration.enchdesc.EnchdescModAccessor;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import net.darkhax.enchdesc.common.impl.Config;
import net.darkhax.enchdesc.common.impl.EnchdescMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
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

        enchantmentComponents.sort((o1, o2) -> {
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

        List<Component> originalComponents = new ArrayList<>(components);
        int processed = -1;

        root: for (int i = 0; i < enchantmentComponents.size(); ++i) {
            Component component = enchantmentComponents.get(i);

            Optional<Holder.Reference<Enchantment>> enchantment = tooltipContext.registries().lookupOrThrow(Registries.ENCHANTMENT).filterElements(e -> component.contains(e.description())).listElements().findFirst();
            if (enchantment.isEmpty())
                continue;

            for (int j = 0; j < originalComponents.size(); ++j) {
                Component toReplace = originalComponents.get(j);
                if (enchantmentComponents.stream().noneMatch(comp -> comp.equals(toReplace)) || processed >= j)
                    continue;

                processed = j;

                Holder<EnchantmentCategory> category = categories.findFirstCategory(enchantment.get());

                Component newComp = component.copy();
                if (category != null && category.isBound())
                    newComp = newComp.copy().withColor(category.value().color().getValue());

                if (Enchiridion.ENCHANTMENT_DESCRIPTION_MODS.stream().anyMatch(Enchiridion.getHelper()::isModLoaded) && areTooltipsActive(stack)) {
                    int index = -1;
                    for (int k = 0; k < originalComponents.size(); ++k)
                        if (originalComponents.get(k).equals(component)) {
                            index = k;
                            break;
                        }
                    if (index != -1)
                        components.set(j + 1, originalComponents.get(index + 1));
                }

                components.set(j, newComp);
                continue root;
            }
        }
    }

    private static boolean areTooltipsActive(ItemStack stack) {
        if (Enchiridion.getHelper().isModLoaded("enchdesc"))
            return isEnchDescActive(stack);
        return false;
    }

    private static boolean isEnchDescActive(ItemStack stack) {
        if (!EnchdescMod.hasInstance())
            return false;

        Config config = ((EnchdescModAccessor)EnchdescMod.getInstance()).enchiridion$getEnchdescConfig();

        if (config.only_on_books && !(stack.getItem() instanceof EnchantedBookItem))
            return false;

        if (config.only_in_enchanting_table && !(Minecraft.getInstance().screen instanceof EnchantmentScreen))
            return false;

        return !config.require_keybind || Screen.hasShiftDown();
    }
}
