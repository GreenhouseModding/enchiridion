package dev.greenhouseteam.enchiridion.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.client.EnchiridionClient;
import dev.greenhouseteam.enchiridion.client.util.EnchiridionModelUtil;
import dev.greenhouseteam.enchiridion.enchantment.category.EnchantmentCategory;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.mixin.ItemEnchantmentsAccessor;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import dev.greenhouseteam.enchiridion.registry.EnchiridionEnchantmentCategories;
import dev.greenhouseteam.enchiridion.registry.EnchiridionRegistries;
import dev.greenhouseteam.enchiridion.util.EnchiridionUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow @Final private ItemModelShaper itemModelShaper;

    @ModifyReturnValue(method = "getModel", at = @At("RETURN"))
    private BakedModel enchiridion$renderSpecialBook(BakedModel model, ItemStack stack, Level level, @Nullable LivingEntity entity, int light, @Share("textColor") LocalRef<TextColor> textColor) {
        if (!stack.is(Items.ENCHANTED_BOOK))
            return model;

        ItemEnchantments storedEnchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (!((ItemEnchantmentsAccessor)storedEnchantments).enchiridion$shouldShowInTooltip() || storedEnchantments.isEmpty() || level.registryAccess().registryOrThrow(EnchiridionRegistries.ENCHANTMENT_CATEGORY).holders().filter(category -> category.isBound() && !category.is(EnchiridionEnchantmentCategories.CURSE)).noneMatch(category -> category.value().acceptedEnchantments().stream().findAny().isPresent()))
            return EnchiridionClient.getHelper().getModel(EnchiridionModelUtil.ENCHANTED_BOOK_RED);

        Holder<EnchantmentCategory> first = EnchiridionUtil.getFirstEnchantmentCategory(level.registryAccess(), stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY), stack.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY));

        if (first == null || !first.isBound())
            return EnchiridionClient.getHelper().getModel(EnchiridionModelUtil.ENCHANTED_BOOK_MISC);

        if (first.value().fullEnchantedBookModelLocation().isEmpty()) {
            textColor.set(first.value().color());
            return EnchiridionClient.getHelper().getModel(EnchiridionModelUtil.ENCHANTED_BOOK_COLORED);
        }
        ClientLevel clientLevel = level instanceof ClientLevel ? (ClientLevel)level : null;
        BakedModel newModel = EnchiridionClient.getHelper().getModel(first.value().fullEnchantedBookModelLocation().orElse(EnchiridionModelUtil.ENCHANTED_BOOK_COLORED));

        if (newModel == null) {
            Enchiridion.LOGGER.warn("Could not find item model \"{}\".", first.value().fullEnchantedBookModelLocation().get());
        } else {
            newModel = newModel.getOverrides().resolve(newModel, stack, clientLevel, entity, light);
        }

        return newModel == null ? EnchiridionClient.getHelper().getModel(EnchiridionModelUtil.ENCHANTED_BOOK_MISC) : newModel;
    }

    @ModifyVariable(method = "renderQuadList", at = @At(value = "STORE", ordinal = 0), index = 11)
    private int enchiridion$modifyEnchantmentColor(int value, @Share("textColor") LocalRef<TextColor> textColor) {
        if (textColor.get() != null)
            return textColor.get().getValue();
        return value;
    }
}
