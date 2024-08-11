package dev.greenhouseteam.enchiridion.util;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.IntStream;

public class CreativeTabUtil {
    public static void sortEnchantmentsBasedOnCategory(Set<ItemStack> original, HolderLookup.Provider provider) {
        if (original.stream().anyMatch(stack -> !EnchiridionUtil.getEnchantmentsOrStoredEnchantments(stack).isEmpty())) {
            ObjectLinkedOpenCustomHashSet<ItemStack> stacks = (ObjectLinkedOpenCustomHashSet<ItemStack>)original;
            List<ItemStack> returnStackList = new ArrayList<>();
            List<Pair<Integer, ItemStack>> indexList = new ArrayList<>(IntStream.range(0, stacks.size())
                    .filter(i -> !EnchiridionUtil.getEnchantmentsOrStoredEnchantments(stacks.get(i)).isEmpty())
                    .mapToObj(i -> Pair.of(i, stacks.get(i))).toList());

            for (Pair<Integer, ItemStack> stack : indexList)
                returnStackList.set(stack.getFirst(), ItemStack.EMPTY);

            List<Integer> intList = indexList.stream().map(Pair::getFirst).toList();
            List<Item> itemList = indexList.stream().map(pair -> pair.getSecond().getItem()).toList();
            List<ItemStack> stackList = new ArrayList<>(indexList.stream().map(Pair::getSecond).toList());

            stackList.sort((o1, o2) -> {
                if (o1.getItem() != o2.getItem())
                    return Integer.compare(itemList.indexOf(o1.getItem()), itemList.indexOf(o2.getItem()));

                ItemEnchantments enchantments = EnchiridionUtil.getEnchantmentsOrStoredEnchantments(o1);
                ItemEnchantments enchantments2 = EnchiridionUtil.getEnchantmentsOrStoredEnchantments(o2);

                Pair<Holder<Enchantment>, Integer> enchantment = EnchiridionUtil.getFirstEnchantmentAndLevel(provider, enchantments);
                Pair<Holder<Enchantment>, Integer> enchantment2 = EnchiridionUtil.getFirstEnchantmentAndLevel(provider, enchantments2);

                int o1CategoryPriority = Optional.ofNullable(EnchiridionUtil.getFirstEnchantmentCategory(provider, enchantments, o1.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY))).map(category -> {
                    if (!category.isBound())
                        return Integer.MIN_VALUE;
                    return category.value().priority();
                }).orElse(0);
                int o2CategoryPriority = Optional.ofNullable(EnchiridionUtil.getFirstEnchantmentCategory(provider, enchantments2, o2.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY))).map(category -> {
                    if (!category.isBound())
                        return Integer.MIN_VALUE;
                    return category.value().priority();
                }).orElse(0);

                if (enchantment != null && enchantment2 != null && enchantment.getFirst().equals(enchantment2.getFirst()))
                    return Integer.compare(enchantment.getSecond(), enchantment2.getSecond());

                if (o1CategoryPriority == o2CategoryPriority && enchantment != null && enchantment2 != null)
                    return EnchiridionUtil.compareEnchantmentNames(enchantment.getFirst(), enchantment2.getFirst());

                // We flip the typical comparison, so we can get higher priority to come first.
                return Integer.compare(o2CategoryPriority, o1CategoryPriority);
            });

            for (int i = 0; i < intList.size(); ++i)
                returnStackList.set(intList.get(i), stackList.get(i));

            original.clear();
            original.addAll(returnStackList);
        }
    }
}
