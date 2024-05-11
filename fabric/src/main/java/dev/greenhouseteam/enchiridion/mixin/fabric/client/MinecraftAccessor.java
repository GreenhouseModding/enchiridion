package dev.greenhouseteam.enchiridion.mixin.fabric.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("gameThread")
    Thread enchiridion$getGameThread();
}
