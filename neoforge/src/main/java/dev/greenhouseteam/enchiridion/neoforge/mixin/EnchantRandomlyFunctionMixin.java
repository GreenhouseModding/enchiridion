package dev.greenhouseteam.enchiridion.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.greenhouseteam.enchiridion.neoforge.util.AllowEnchantingUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantRandomlyFunction.class)
public abstract class EnchantRandomlyFunctionMixin {
    @Inject(method = "run", at = @At("HEAD"))
    private void enchiridion$captureContext(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir, @Share("context") LocalRef<LootContext> lootContext) {
        lootContext.set(context);
    }

    @ModifyExpressionValue(method = "lambda$run$4", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean enchiridion$preventRandomEnchanting(boolean original, @Local(argsOnly = true) Holder<Enchantment> enchantment, @Local(argsOnly = true) ItemStack stack, @Share("context") LocalRef<LootContext> lootContext) {
        if (AllowEnchantingUtil.shouldCancelEnchanting(enchantment, stack))
            return false;
        return original;
    }
}
