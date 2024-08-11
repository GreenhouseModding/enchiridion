package dev.greenhouseteam.enchiridion.util;

import com.mojang.datafixers.util.Pair;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class CreativeTabUtil {
    public static void sortEnchantmentsBasedOnCategory(List<ItemStack> stacks, Consumer<List<ItemStack>> addConsumer, HolderLookup.Provider provider) {
        if (stacks.stream().noneMatch(stack -> EnchiridionUtil.getEnchantmentsOrStoredEnchantments(stack) != null))
            return;
        List<Pair<Integer, ItemStack>> indexList = new ArrayList<>(IntStream.range(0, stacks.size())
                .mapToObj(i -> Pair.of(i, stacks.get(i))).toList());

        List<Item> itemList = indexList.stream().map(pair -> pair.getSecond().getItem()).toList();
        List<ItemStack> stackList = new ArrayList<>(indexList.stream().map(Pair::getSecond).toList());

        stackList.sort((o1, o2) -> {
            ItemEnchantments enchantments = EnchiridionUtil.getEnchantmentsOrStoredEnchantments(o1);
            ItemEnchantments enchantments2 = EnchiridionUtil.getEnchantmentsOrStoredEnchantments(o2);

            if (o1.getItem() != o2.getItem() || enchantments == null || enchantments2 == null)
                return Integer.compare(itemList.indexOf(o1.getItem()), itemList.indexOf(o2.getItem()));

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

        addConsumer.accept(stackList);
    }
}
