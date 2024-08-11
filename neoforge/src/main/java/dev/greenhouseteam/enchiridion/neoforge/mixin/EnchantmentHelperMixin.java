package dev.greenhouseteam.enchiridion.neoforge.mixin;

import dev.greenhouseteam.enchiridion.neoforge.util.AllowEnchantingUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @ModifyVariable(method = "getAvailableEnchantmentResults", at = @At("HEAD"), argsOnly = true)
    private static Stream<Holder<Enchantment>> enchiridion$preventCommandEnchanting(Stream<Holder<Enchantment>> possibleEnchantments, int level, ItemStack stack) {
        return possibleEnchantments.filter(enchantment -> !AllowEnchantingUtil.shouldCancelEnchanting(enchantment, stack));
    }
}
