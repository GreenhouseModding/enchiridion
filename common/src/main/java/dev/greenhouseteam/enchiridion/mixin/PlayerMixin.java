package dev.greenhouseteam.enchiridion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.access.PlayerTargetAccess;
import dev.greenhouseteam.enchiridion.enchantment.effects.PreventHungerConsumptionEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerTargetAccess {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"))
    private boolean enchiridion$wrapExhaustionFromJumping(boolean original) {
        return original && !PreventHungerConsumptionEffect.shouldPreventSprintingConsumption((Player)(Object)this);
    }

    @WrapWithCondition(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private boolean enchiridion$wrapExhaustionFromJumping(Player instance, float value) {
        return !PreventHungerConsumptionEffect.shouldPreventJumpingConsumption(instance);
    }

    @Unique
    private final Map<Holder<Enchantment>, Set<EnchantmentLocationBasedEffect>> enchiridion$activeBlockTargetEnchantmentEffects = new HashMap<>();

    @Override
    public Map<Holder<Enchantment>, Set<EnchantmentLocationBasedEffect>> enchiridion$activeBlockTargetEnchantmentEffects() {
        return enchiridion$activeBlockTargetEnchantmentEffects;
    }

    @Override
    public void enchiridion$addActiveBlockTargetEnchantmentEffect(Holder<Enchantment> enchantment, EnchantmentLocationBasedEffect effect) {
        enchiridion$activeBlockTargetEnchantmentEffects.computeIfAbsent(enchantment, e -> new HashSet<>()).add(effect);
    }

    @Override
    public void enchiridion$removeActiveBlockTargetEnchantmentEffect(Holder<Enchantment> enchantment, EnchantmentLocationBasedEffect effect) {
        if (!enchiridion$activeBlockTargetEnchantmentEffects.containsKey(enchantment))
            return;

        enchiridion$activeBlockTargetEnchantmentEffects.get(enchantment).remove(effect);

        if (enchiridion$activeBlockTargetEnchantmentEffects.get(enchantment).isEmpty())
            enchiridion$activeBlockTargetEnchantmentEffects.remove(enchantment);
    }

    @Inject(method = "onEnchantmentPerformed", at = @At("TAIL"))
    private void enchiridion$clearEnchantments(ItemStack stack, int cost, CallbackInfo ci) {
        Enchiridion.getHelper().clearEnchantmentSeeds((Player)(Object)this);
    }
}
