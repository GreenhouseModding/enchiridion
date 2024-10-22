package dev.greenhouseteam.enchiridion.mixin.integration.enchdesc;

import net.darkhax.enchdesc.common.impl.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// I hate to do this.
// FIXME: This is hopefully temporary.
@Mixin(targets = "net.darkhax.enchdesc.common.impl.EnchdescMod", remap = false)
public interface EnchdescModAccessor {
    @Accessor(value = "config", remap = false)
    Config enchiridion$getEnchdescConfig();
}
