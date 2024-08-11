package dev.greenhouseteam.enchiridion.util;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.enchantment.category.EnchantmentCategory;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import dev.greenhouseteam.enchiridion.registry.EnchiridionRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EnchiridionUtil {

    public static void categoriseEnchantmentsOnItem(ItemEnchantments enchantments, ItemStack stack, DataComponentType<ItemEnchantments> componentType) {
        ItemEnchantmentCategories categories = stack.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, new ItemEnchantmentCategories());

        enchantments.keySet().forEach(enchantment -> {
            Holder<EnchantmentCategory> category = EnchiridionUtil.getFirstEnchantmentCategory(Enchiridion.getHelper().getReqistryAccess(), enchantment);
            if (category != null && category.isBound() && !EnchiridionUtil.isValidInCategory(category, categories.get(category), enchantment)) {
                categories.removeCategoryWithEnchantment(category, enchantment);
                return;
            }

            if (category == null || !category.isBound() || categories.getCategories().containsKey(enchantment) && categories.getCategories().get(category).contains(enchantment) || !EnchiridionUtil.categoryAcceptsNewEnchantments(category, categories))
                return;
            categories.addCategoryWithEnchantment(category, enchantment);
        });

        if (enchantments.keySet().stream().anyMatch(enchantment -> EnchiridionUtil.getFirstEnchantmentCategory(Enchiridion.getHelper().getReqistryAccess(), enchantment) != null && !categories.contains(enchantment))) {
            ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
            mutable.removeIf(enchantment -> EnchiridionUtil.getFirstEnchantmentCategory(Enchiridion.getHelper().getReqistryAccess(), enchantment) != null && !categories.contains(enchantment));
            stack.set(componentType, mutable.toImmutable());
        }
        if (!categories.equals(stack.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY))) {
            if (categories.isEmpty())
                stack.remove(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES);
            else {
                DataComponentPatch.Builder builder = DataComponentPatch.builder();
                builder.set(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, categories);
                stack.applyComponents(builder.build());
            }
        }
    }


    public static void categoriseStoredEnchantmentsOnItem(ItemEnchantments enchantments, ItemStack stack) {
        ItemEnchantmentCategories categories = stack.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, new ItemEnchantmentCategories());

        Map<Holder<EnchantmentCategory>, Set<HolderSet<Item>>> usedEnchantmentTags = new HashMap<>();

        enchantments.keySet().forEach(enchantment -> {
            List<Holder<EnchantmentCategory>> applicableCategories = EnchiridionUtil.getAllEnchantmentCategories(Enchiridion.getHelper().getReqistryAccess(), enchantment);
            for (Holder<EnchantmentCategory> category : applicableCategories) {
                HolderSet<Item> tag = enchantment.value().definition().primaryItems().orElse(enchantment.value().definition().supportedItems());

                if (category.isBound() && usedEnchantmentTags.containsKey(category) && usedEnchantmentTags.get(category).stream().anyMatch(holders -> holders.stream().anyMatch(tag::contains))) {
                    categories.removeCategoryWithEnchantment(category, enchantment);
                    return;
                }

                if (!category.isBound() || categories.getCategories().containsKey(enchantment) && categories.getCategories().get(category).contains(enchantment) || usedEnchantmentTags.containsKey(category) && usedEnchantmentTags.get(category).stream().anyMatch(holders -> holders.stream().anyMatch(tag::contains)))
                    return;
                categories.addCategoryWithEnchantment(category, enchantment);
                usedEnchantmentTags.computeIfAbsent(category, c -> new HashSet<>()).add(tag);
                break;
            }
        });

        if (enchantments.keySet().stream().anyMatch(enchantment -> EnchiridionUtil.getFirstEnchantmentCategory(Enchiridion.getHelper().getReqistryAccess(), enchantment) != null && !categories.contains(enchantment))) {
            ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
            mutable.removeIf(enchantment -> !EnchiridionUtil.getAllEnchantmentCategories(Enchiridion.getHelper().getReqistryAccess(), enchantment).isEmpty() && !categories.contains(enchantment));
            stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        }
        if (!categories.equals(stack.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY))) {
            if (categories.isEmpty())
                stack.remove(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES);
            else {
                DataComponentPatch.Builder builder = DataComponentPatch.builder();
                builder.set(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, categories);
                stack.applyComponents(builder.build());
            }
        }
    }

    @Nullable
    public static Pair<Holder<Enchantment>, Integer> getFirstEnchantmentAndLevel(HolderLookup.Provider registries, ItemEnchantments enchantments) {
        Optional<HolderSet.Named<Enchantment>> tooltipOrderTag = registries.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.TOOLTIP_ORDER);
        if (tooltipOrderTag.isEmpty())
            return null;

        Optional<Pair<Holder<Enchantment>, Integer>> enchantment = tooltipOrderTag.get().stream().filter(holder -> enchantments.keySet().contains(holder)).map(holder -> Pair.of(holder, enchantments.getLevel(holder))).findFirst();
        if (enchantment.isEmpty())
            enchantment = enchantments.keySet().stream().findFirst().map(holder -> Pair.of(holder, enchantments.getLevel(holder)));
        return enchantment.orElse(null);
    }

    @Nullable
    public static Holder<EnchantmentCategory> lookupFirstEnchantmentCategory(HolderLookup.Provider registries, Holder<Enchantment> enchantment, ItemEnchantmentCategories categories) {
        List<Holder.Reference<EnchantmentCategory>> applicable = registries.lookupOrThrow(EnchiridionRegistries.ENCHANTMENT_CATEGORY).listElements().filter(category -> categories.isValid(category, enchantment)).toList();
        return applicable.stream().max(Comparator.comparingInt(value -> value.value().priority())).orElse(null);
    }

    @Nullable
    public static Holder<EnchantmentCategory> getFirstEnchantmentCategory(HolderLookup.Provider registries, ItemEnchantments enchantments, ItemEnchantmentCategories categories) {
        Optional<HolderSet.Named<Enchantment>> tooltipOrderTag = registries.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.TOOLTIP_ORDER);
        if (tooltipOrderTag.isEmpty())
            return null;

        List<Holder<Enchantment>> enchantment = tooltipOrderTag.get().stream().filter(e -> enchantments.keySet().contains(e)).toList();

        if (enchantment.isEmpty())
            return enchantments.keySet().stream().map(categories::findFirstCategory).filter(holder -> holder != null && holder.isBound()).max(Comparator.comparingInt(value -> value.value().priority())).orElse(null);

        return enchantment.stream().map(categories::findFirstCategory).filter(holder -> holder != null && holder.isBound()).max(Comparator.comparingInt(value -> value.value().priority())).orElse(null);
    }

    @Nullable
    public static Holder<EnchantmentCategory> getFirstEnchantmentCategory(HolderLookup.Provider registries, Holder<Enchantment> enchantment) {
        return registries.lookupOrThrow(EnchiridionRegistries.ENCHANTMENT_CATEGORY).listElements().filter(category -> category.isBound() && category.value().acceptedEnchantments().contains(enchantment)).max(Comparator.comparingInt(value -> value.value().priority())).orElse(null);
    }

    public static List<Holder<EnchantmentCategory>> getAllEnchantmentCategories(HolderLookup.Provider registries, Holder<Enchantment> enchantment) {
        return registries.lookupOrThrow(EnchiridionRegistries.ENCHANTMENT_CATEGORY).listElements().filter(category -> category.isBound() && category.value().acceptedEnchantments().contains(enchantment)).collect(Collectors.toList());
    }

    public static boolean isValidInCategory(HolderLookup.Provider registries, ItemEnchantmentCategories categories, Holder<Enchantment> enchantment) {
        Optional<Holder.Reference<EnchantmentCategory>> category = registries.lookupOrThrow(EnchiridionRegistries.ENCHANTMENT_CATEGORY).listElements().filter(c -> c.isBound() && c.value().acceptedEnchantments().contains(enchantment)).max(Comparator.comparingInt(o -> o.value().priority()));
        return category.map(enchantmentCategory -> isValidInCategory(enchantmentCategory, categories.get(enchantmentCategory), enchantment)).orElse(true);
    }

    public static boolean isValidInCategory(Holder<EnchantmentCategory> category, List<Holder<Enchantment>> enchantments, Holder<Enchantment> enchantment) {
        enchantments = new ArrayList<>(enchantments);
        if (!enchantments.contains(enchantment))
            enchantments.add(enchantment);
        return (category.isBound() && category.value().limit().isEmpty()) || category.isBound() && enchantments.subList(0, enchantments.indexOf(enchantment)).size() < category.value().limit().get();
    }

    public static boolean categoryAcceptsNewEnchantments(Holder<EnchantmentCategory> category, ItemEnchantmentCategories categories) {
        if (category == null || !category.isBound() || category.value().limit().isEmpty() || !categories.getCategories().containsKey(category))
            return true;
        return categoryAcceptsNewEnchantmentsInternal(category, List.copyOf(categories.getCategories().get(category)));
    }

    public static boolean categoryAcceptsNewEnchantmentsWithValue(Holder<EnchantmentCategory> category, ItemEnchantmentCategories categories, Holder<Enchantment> enchantment) {
        if (category == null || !category.isBound() || category.value().limit().isEmpty() || !categories.getCategories().containsKey(category))
            return true;
        List<Holder<Enchantment>> enchantments = new ArrayList<>(categories.getCategories().get(category));
        if (!enchantments.contains(enchantment))
            enchantments.add(enchantment);
        return categoryAcceptsNewEnchantmentsInternal(category, enchantments);
    }

    private static boolean categoryAcceptsNewEnchantmentsInternal(Holder<EnchantmentCategory> category, List<Holder<Enchantment>> holders) {
        return (category.isBound() && category.value().limit().isEmpty()) || category.isBound() && holders.size() < category.value().limit().get();
    }

    @Nullable
    public static ItemEnchantments getEnchantmentsOrStoredEnchantments(ItemStack stack) {
        ItemEnchantments enchantments = stack.getEnchantments();
        if (enchantments.isEmpty())
            enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty())
            return null;
        return enchantments;
    }

    public static int compareEnchantments(Holder<Enchantment> enchantment, Holder<Enchantment> enchantment2, ItemEnchantmentCategories categories) {
        if (!enchantment.isBound() && !enchantment2.isBound())
            return 0;
        if (!enchantment.isBound() && enchantment2.isBound())
            return -1;
        if (enchantment.isBound() && !enchantment2.isBound())
            return 1;

        int o1CategoryPriority = Optional.ofNullable(categories.findFirstCategory(enchantment)).map(category -> {
            if (!category.isBound())
                return Integer.MIN_VALUE;
            return category.value().priority();
        }).orElse(0);
        int o2CategoryPriority = Optional.ofNullable(categories.findFirstCategory(enchantment2)).map(category -> {
            if (!category.isBound())
                return Integer.MIN_VALUE;
            return category.value().priority();
        }).orElse(0);

        if (o1CategoryPriority == o2CategoryPriority)
            return compareEnchantmentNames(enchantment, enchantment2);

        // We flip the typical comparison, so we can get higher priority to come first.
        return Integer.compare(o2CategoryPriority, o1CategoryPriority);
    }

    public static int compareEnchantmentNames(Holder<Enchantment> enchantment, Holder<Enchantment> enchantment2) {
        if (
            // Prioritise the Minecraft namespace.
                enchantment.unwrapKey().isPresent() && enchantment.unwrapKey().get().location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) &&
                        enchantment2.unwrapKey().isPresent() && !enchantment2.unwrapKey().get().location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)
        ) {
            return -1;
        } else if (
            // Prioritise the Minecraft namespace.
                enchantment.unwrapKey().isPresent() && !enchantment.unwrapKey().get().location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) &&
                        enchantment2.unwrapKey().isPresent() && enchantment2.unwrapKey().get().location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)
        ) {
            return 1;
        }
        if (enchantment.unwrapKey().isPresent() && enchantment2.unwrapKey().isPresent())
            return enchantment.unwrapKey().get().location().compareTo(enchantment2.unwrapKey().get().location());
        return Integer.compare(enchantment.hashCode(), enchantment2.hashCode());
    }
}
