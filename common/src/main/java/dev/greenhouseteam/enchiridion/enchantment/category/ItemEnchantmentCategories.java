package dev.greenhouseteam.enchiridion.enchantment.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import dev.greenhouseteam.enchiridion.registry.EnchiridionRegistries;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ItemEnchantmentCategories {
    public static final ItemEnchantmentCategories EMPTY = new ItemEnchantmentCategories(new Object2ObjectOpenHashMap<>());
    public static final Codec<ItemEnchantmentCategories> CODEC = new UnboundedMapCodec<>(EnchantmentCategory.CODEC, Enchantment.CODEC.listOf().xmap(LinkedHashSet::new, ObjectArrayList::new)).xmap(Object2ObjectOpenHashMap::new, Function.identity()).xmap(ItemEnchantmentCategories::new, categories -> categories.enchantmentCategories);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEnchantmentCategories> STREAM_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.holderRegistry(EnchiridionRegistries.ENCHANTMENT_CATEGORY), ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT).apply(ByteBufCodecs.list()).map(LinkedHashSet::new, ObjectArrayList::new)).map(ItemEnchantmentCategories::new, categories -> new Object2ObjectOpenHashMap<>(categories.enchantmentCategories));

    private final Object2ObjectOpenHashMap<Holder<EnchantmentCategory>, LinkedHashSet<Holder<Enchantment>>> enchantmentCategories;

    public ItemEnchantmentCategories() {
        this.enchantmentCategories = new Object2ObjectOpenHashMap<>();
    }

    public ItemEnchantmentCategories(Object2ObjectOpenHashMap<Holder<EnchantmentCategory>, LinkedHashSet<Holder<Enchantment>>> enchantmentCategories) {
        this.enchantmentCategories = enchantmentCategories;
    }

    public Map<Holder<EnchantmentCategory>, LinkedHashSet<Holder<Enchantment>>> getCategories() {
        return Map.copyOf(enchantmentCategories);
    }

    @Nullable
    public Holder<EnchantmentCategory> findFirstCategory(Holder<Enchantment> enchantment) {
        return enchantmentCategories.entrySet().stream().filter(entry -> entry.getValue().contains(enchantment)).max(Comparator.comparingInt(value -> !value.getKey().isBound() ? -1 : value.getKey().value().priority())).map(Map.Entry::getKey).orElse(null);
    }

    public void addCategoryWithEnchantment(Holder<EnchantmentCategory> category, Holder<Enchantment> enchantment) {
        enchantmentCategories.computeIfAbsent(category, category1 -> new LinkedHashSet<>()).add(enchantment);
    }

    public void removeCategoryWithEnchantment(Holder<EnchantmentCategory> category, Holder<Enchantment> enchantment) {
        if (!enchantmentCategories.containsKey(category))
            return;

        enchantmentCategories.computeIfAbsent(category, category1 -> new LinkedHashSet<>()).remove(enchantment);

        if (enchantmentCategories.get(category).isEmpty())
            enchantmentCategories.remove(category);
    }

    public List<Holder<Enchantment>> get(Holder<EnchantmentCategory> category) {
        if (!enchantmentCategories.containsKey(category))
            return List.of();
        return List.copyOf(enchantmentCategories.get(category));
    }

    public boolean contains(Holder<Enchantment> enchantment) {
        return enchantmentCategories.values().stream().anyMatch(holders -> holders.contains(enchantment));
    }

    public boolean isEmpty() {
        return enchantmentCategories.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ItemEnchantmentCategories categories))
            return false;
        return categories.enchantmentCategories.equals(enchantmentCategories);
    }

    @Override
    public int hashCode() {
        return enchantmentCategories.hashCode();
    }

    @Override
    public String toString() {
        return "ItemEnchantmentCategories{" + this.enchantmentCategories + "}";
    }
}
