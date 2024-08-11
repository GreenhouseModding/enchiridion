package dev.greenhouseteam.enchiridion.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.greenhouseteam.enchiridion.neoforge.util.AllowEnchantingUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantCommand.class)
public abstract class EnchantCommandMixin {
    @ModifyExpressionValue(method = "enchant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean enchiridion$preventCommandEnchanting(boolean original, @Local(argsOnly = true) CommandSourceStack source, @Local(argsOnly = true) Holder<Enchantment> enchantment, @Local ItemStack stack) {
        if (AllowEnchantingUtil.shouldCancelEnchanting(enchantment, stack))
            return false;
        return original;
    }
}
