package dev.greenhouseteam.enchiridion;

import dev.greenhouseteam.enchiridion.platform.EnchiridionPlatformHelper;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Enchiridion {
    public static final String MOD_ID = "enchiridion";
    public static final String MOD_NAME = "Enchiridion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static EnchiridionPlatformHelper helper;

    public static void init(EnchiridionPlatformHelper helper) {
        Enchiridion.helper = helper;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static EnchiridionPlatformHelper getHelper() {
        return helper;
    }
}