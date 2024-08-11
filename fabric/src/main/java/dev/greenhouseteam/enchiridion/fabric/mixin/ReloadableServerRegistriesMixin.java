package dev.greenhouseteam.enchiridion.fabric.mixin;

import dev.greenhouseteam.enchiridion.fabric.EnchiridionFabric;
import dev.greenhouseteam.enchiridion.fabric.util.TagUtil;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {
    @Inject(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootDataType;values()Ljava/util/stream/Stream;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void enchiridion$setLootTableAccess(LayeredRegistryAccess<RegistryLayer> layeredRegistryAccess, ResourceManager resourceManager, Executor executor, CallbackInfoReturnable<CompletableFuture<LayeredRegistryAccess<RegistryLayer>>> cir, RegistryAccess.Frozen frozen) {
        EnchiridionFabric.setLootTableResourceManager(resourceManager);
    }

    @Inject(method = "method_58288", at = @At("RETURN"))
    private static void enchiridion$clearLootTableAccess(LayeredRegistryAccess layeredRegistryAccess, List list, CallbackInfoReturnable<LayeredRegistryAccess> cir) {
        EnchiridionFabric.setLootTableResourceManager(null);
        TagUtil.resetEarlyBaseStoneTag();
    }
}
