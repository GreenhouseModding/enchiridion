package dev.greenhouseteam.enchiridion.fabric.client.platform;

import dev.greenhouseteam.enchiridion.client.platform.EnchiridionClientPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public class EnchiridionClientPlatformHelperFabric implements EnchiridionClientPlatformHelper {
    @Override
    public BakedModel getModel(ResourceLocation id) {
        return Minecraft.getInstance().getModelManager().getModel(id);
    }
}
