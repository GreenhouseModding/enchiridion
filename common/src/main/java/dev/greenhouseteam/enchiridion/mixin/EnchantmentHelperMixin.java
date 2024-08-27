package dev.greenhouseteam.enchiridion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.greenhouseteam.enchiridion.registry.EnchiridionEnchantmentEffectComponents;
import dev.greenhouseteam.enchiridion.registry.EnchiridionLootContextParamSets;
import dev.greenhouseteam.enchiridion.registry.EnchiridionLootContextParams;
import dev.greenhouseteam.enchiridion.util.EnchiridionUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Shadow
    private static void runIterationOnEquipment(LivingEntity $$0, EnchantmentHelper.EnchantmentInSlotVisitor $$1) {
    }

    @ModifyReturnValue(method = "getEnchantmentCost", at = @At("RETURN"))
    private static int enchiridion$modifyEnchantmentCost(int original) {
        return Math.max(1, Math.round((float) original * 0.67F));
    }

    @ModifyReturnValue(method = "updateEnchantments", at = @At("RETURN"))
    private static ItemEnchantments enchiridion$updateAndValidateCategories(ItemEnchantments enchantments, ItemStack stack, Consumer<ItemEnchantments.Mutable> consumer, @Local DataComponentType<ItemEnchantments> componentType) {
        if (componentType == DataComponents.STORED_ENCHANTMENTS)
            EnchiridionUtil.categoriseStoredEnchantmentsOnItem(enchantments, stack);
        else
            EnchiridionUtil.categoriseEnchantmentsOnItem(enchantments, stack, componentType);

        return enchantments;
    }

    @ModifyVariable(method = "getDamageProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;runIterationOnEquipment(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V", shift = At.Shift.AFTER))
    private static MutableFloat enchiridion$addVehicleProtection(MutableFloat original, ServerLevel level, LivingEntity entity, DamageSource source) {
        if (!(entity.getFirstPassenger() instanceof LivingEntity passenger))
            return original;
        LootParams.Builder params = new LootParams.Builder(level);
        params.withParameter(LootContextParams.THIS_ENTITY, passenger);
        params.withParameter(EnchiridionLootContextParams.VEHICLE, entity);
        params.withParameter(LootContextParams.ORIGIN, entity.position());
        params.withParameter(LootContextParams.DAMAGE_SOURCE, source);
        params.withOptionalParameter(EnchiridionLootContextParams.FIRST_PASSENGER, passenger.getFirstPassenger());
        params.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity());
        params.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity());
        runIterationOnEquipment(passenger, (enchantment, l, enchantedItem) -> {
            params.withParameter(LootContextParams.ENCHANTMENT_LEVEL, l);

            LootContext lootContext = new LootContext.Builder(params.create(EnchiridionLootContextParamSets.VEHICLE_ENCHANTED_DAMAGE)).create(Optional.empty());

            for (ConditionalEffect<EnchantmentValueEffect> conditionalEffect : enchantment.value().getEffects(EnchiridionEnchantmentEffectComponents.VEHICLE_DAMAGE_PROTECTION)) {
                if (conditionalEffect.matches(lootContext)) {
                    original.setValue(conditionalEffect.effect().process(l, entity.getRandom(), original.floatValue()));
                }
            }
        });
        return original;
    }
}
