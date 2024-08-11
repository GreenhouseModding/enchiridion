package dev.greenhouseteam.enchiridion.neoforge.client.platform;

import dev.greenhouseteam.enchiridion.client.platform.EnchiridionClientPlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class EnchiridionClientPlatformHelperNeoForge implements EnchiridionClientPlatformHelper {
    @Override
    public BakedModel getModel(ResourceLocation id) {
        return Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(id));
    }
}
