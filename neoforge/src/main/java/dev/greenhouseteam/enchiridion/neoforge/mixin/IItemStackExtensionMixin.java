package dev.greenhouseteam.enchiridion.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.greenhouseteam.enchiridion.neoforge.util.AllowEnchantingUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IItemStackExtension.class)
public interface IItemStackExtensionMixin {
    @ModifyReturnValue(method = "supportsEnchantment", at = @At("RETURN"))
    private boolean enchiridion$preventCategoryIncompatibilities(boolean original, Holder<Enchantment> enchantment) {
        if (AllowEnchantingUtil.shouldCancelEnchanting(enchantment, (ItemStack)(Object)this))
            return false;
        return original;
    }
}
