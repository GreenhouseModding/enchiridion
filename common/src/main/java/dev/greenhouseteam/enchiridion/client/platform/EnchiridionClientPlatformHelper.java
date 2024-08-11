package dev.greenhouseteam.enchiridion.client.platform;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public interface EnchiridionClientPlatformHelper {

    BakedModel getModel(ResourceLocation id);
}