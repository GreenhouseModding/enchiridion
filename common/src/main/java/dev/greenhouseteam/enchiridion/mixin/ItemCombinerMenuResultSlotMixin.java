package dev.greenhouseteam.enchiridion.mixin;

import dev.greenhouseteam.enchiridion.access.MergeableAnvilAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/inventory/ItemCombinerMenu$2")
public class ItemCombinerMenuResultSlotMixin {
    @Shadow @Final
    ItemCombinerMenu field_22483;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void enchiridion$setHasItems(Player player, ItemStack itemStack, CallbackInfo ci) {
        if (field_22483 instanceof MergeableAnvilAccess access && access.enchiridion$merged())
            access.enchiridion$setHasOneResult(!access.enchiridion$hasOneResult());
    }
}
