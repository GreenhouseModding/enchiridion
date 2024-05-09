package dev.greenhouseteam.enchiridion.mixin;

import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEnchantments.class)
public interface ItemEnchantmentsAccessor {
    @Accessor("showInTooltip")
    boolean enchiridion$shouldShowInTooltip();
}
