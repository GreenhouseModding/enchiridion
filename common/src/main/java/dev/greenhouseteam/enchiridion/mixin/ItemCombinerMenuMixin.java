package dev.greenhouseteam.enchiridion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.access.MergeableAnvilAccess;
import dev.greenhouseteam.enchiridion.util.AnvilUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMenuMixin extends AbstractContainerMenu {
    @Shadow @Final protected ContainerLevelAccess access;

    @Shadow @Final protected ResultContainer resultSlots;

    protected ItemCombinerMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @WrapOperation(method = "slotsChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ItemCombinerMenu;createResult()V"))
    private void enchiridion$cancelResultCreationWhenItemIsPresent(ItemCombinerMenu instance, Operation<Void> original) {
        if ((ItemCombinerMenu)(Object)this instanceof MergeableAnvilAccess access) {
            if (!access.enchiridion$hasOneResult())
                original.call(instance);
        }
    }

    @Inject(method = "removed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"))
    private void enchiridion$clearContainerOfRemainingResults(Player player, CallbackInfo ci) {
        if ((ItemCombinerMenu)(Object)this instanceof MergeableAnvilAccess hasItemsAccess && hasItemsAccess.enchiridion$hasOneResult()) {
            access.execute((level, pos) -> clearContainer(player, this.resultSlots));
            access.execute((level, pos) -> clearContainer(player, hasItemsAccess.enchiridion$getMergeContainer()));
        }
    }

    @ModifyExpressionValue(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ItemCombinerMenu;getResultSlot()I"))
    private int enchiridion$allowQuickMovingOfMergeStack(int original, Player player, int index) {
        if (this instanceof MergeableAnvilAccess && index == AnvilUtil.getAnvilMergeSlotIndex())
            return index;
        return original;
    }
}
