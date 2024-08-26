package dev.greenhouseteam.enchiridion.mixin.client;

import dev.greenhouseteam.enchiridion.util.AnvilUtil;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemCombinerScreen.class)
public class ItemCombinerScreenMixin {
    @ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    private ResourceLocation enchiridion$renderCustomAnvilBg(ResourceLocation original) {
        if (original.equals(AnvilUtil.ORIGINAL_GUI_TEXTURE))
            return AnvilUtil.MERGABLE_GUI_TEXTURE;
        return original;
    }
}
