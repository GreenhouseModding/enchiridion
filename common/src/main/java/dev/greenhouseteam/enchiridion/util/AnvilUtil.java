package dev.greenhouseteam.enchiridion.util;

import dev.greenhouseteam.enchiridion.Enchiridion;
import net.minecraft.resources.ResourceLocation;

public class AnvilUtil {
    public static final ResourceLocation ORIGINAL_GUI_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/anvil.png");
    public static final ResourceLocation MERGABLE_GUI_TEXTURE = Enchiridion.asResource("textures/gui/container/mergeable_anvil.png");
    private static int anvilMergeSlotIndex = 0;

    public static int getAnvilMergeSlotIndex() {
        return anvilMergeSlotIndex;
    }

    public static void setAnvilMergeSlotIndex(int index) {
        if (anvilMergeSlotIndex == 0)
            anvilMergeSlotIndex = index;
    }

    private static boolean anvilContext = false;

    public static boolean getAnvilContext() {
        return anvilContext;
    }

    public static void setAnvilContext(boolean value) {
        anvilContext = value;
    }
}
