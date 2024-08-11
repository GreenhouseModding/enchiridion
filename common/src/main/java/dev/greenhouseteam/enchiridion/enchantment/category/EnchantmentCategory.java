package dev.greenhouseteam.enchiridion.enchantment.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchiridion.registry.EnchiridionRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public record EnchantmentCategory(HolderSet<Enchantment> acceptedEnchantments, Component name,
                                  Optional<ResourceLocation> iconLocation, Optional<ResourceLocation> fullIconLocation,
                                  Optional<ResourceLocation> enchantedBookModelLocation, Optional<ResourceLocation> fullEnchantedBookModelLocation,
                                  TextColor color, Optional<Integer> limit, int priority) {
    public static final Codec<Holder<EnchantmentCategory>> CODEC = RegistryFixedCodec.create(EnchiridionRegistries.ENCHANTMENT_CATEGORY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<EnchantmentCategory>> STREAM_CODEC = ByteBufCodecs.holderRegistry(EnchiridionRegistries.ENCHANTMENT_CATEGORY);
    public static final Codec<EnchantmentCategory> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC).fieldOf("accepted_enchantments").forGetter(EnchantmentCategory::acceptedEnchantments),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(EnchantmentCategory::name),
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(EnchantmentCategory::iconLocation),
            ResourceLocation.CODEC.optionalFieldOf("enchanted_book_model").forGetter(EnchantmentCategory::enchantedBookModelLocation),
            TextColor.CODEC.fieldOf("color").forGetter(EnchantmentCategory::color),
            Codec.INT.optionalFieldOf("limit").forGetter(EnchantmentCategory::limit),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(EnchantmentCategory::priority)
    ).apply(inst, EnchantmentCategory::new));

    // TODO: Builder class or methods for this?
    public EnchantmentCategory(HolderSet<Enchantment> acceptedEnchantments, Component name,
                               ResourceLocation iconLocation, ResourceLocation enchantedBookModelLocation,
                               TextColor color, Optional<Integer> limit, int priority) {
        this(acceptedEnchantments, name, Optional.of(iconLocation), Optional.of(iconLocation).map(id -> id.withPath(path -> "textures/" + path + ".png")), Optional.of(enchantedBookModelLocation), Optional.of(enchantedBookModelLocation).map(resourceLocation -> resourceLocation.withPath(string -> "item/enchiridion/" + string)), color, limit, priority);
    }

    public EnchantmentCategory(HolderSet<Enchantment> acceptedEnchantments, Component name,
                               ResourceLocation iconLocation, Optional<ResourceLocation> enchantedBookModelLocation,
                               TextColor color, Optional<Integer> limit, int priority) {
        this(acceptedEnchantments, name, Optional.of(iconLocation), Optional.of(iconLocation).map(id -> id.withPath(path -> "textures/" + path + ".png")), enchantedBookModelLocation, enchantedBookModelLocation.map(resourceLocation -> resourceLocation.withPath(string -> "item/enchiridion/" + string)), color, limit, priority);
    }

    public EnchantmentCategory(HolderSet<Enchantment> acceptedEnchantments, Component name,
                               Optional<ResourceLocation> iconLocation, ResourceLocation enchantedBookModelLocation,
                               TextColor color, Optional<Integer> limit, int priority) {
        this(acceptedEnchantments, name, iconLocation, iconLocation.map(id -> id.withPath(path -> "textures/" + path + ".png")), Optional.of(enchantedBookModelLocation), Optional.of(enchantedBookModelLocation).map(resourceLocation -> resourceLocation.withPath(string -> "item/enchiridion/" + string)), color, limit, priority);
    }

    public EnchantmentCategory(HolderSet<Enchantment> acceptedEnchantments, Component name,
                               Optional<ResourceLocation> iconLocation, Optional<ResourceLocation> enchantedBookModelLocation,
                               TextColor color, Optional<Integer> limit, int priority) {
        this(acceptedEnchantments, name, iconLocation, iconLocation.map(id -> id.withPath(path -> "textures/" + path + ".png")), enchantedBookModelLocation,  enchantedBookModelLocation.map(resourceLocation -> resourceLocation.withPath(string -> "item/enchiridion/" + string)), color, limit, priority);
    }
}
