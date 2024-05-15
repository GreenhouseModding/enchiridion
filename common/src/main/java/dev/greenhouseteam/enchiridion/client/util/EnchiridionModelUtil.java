package dev.greenhouseteam.enchiridion.client.util;

import dev.greenhouseteam.enchiridion.Enchiridion;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EnchiridionModelUtil {
    public static final ResourceLocation ENCHANTED_BOOK_COLORED = Enchiridion.asResource("enchiridion/enchanted_book_colored");
    public static final ResourceLocation ENCHANTED_BOOK_MISC = Enchiridion.asResource("enchiridion/enchanted_book_misc");
    public static final ResourceLocation ENCHANTED_BOOK_RED = Enchiridion.asResource("enchiridion/enchanted_book_curse");

    public static List<ResourceLocation> getEnchiridionModels(ResourceManager manager) {
        List<ResourceLocation> modelList = new ArrayList<>();
        Map<ResourceLocation, Resource> models = manager.listResources("models/item/enchiridion", fileName -> fileName.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : models.entrySet()) {
            ResourceLocation location = entry.getKey().withPath(string -> {
                String splitString = string.split("/", 3)[2];
                return splitString.substring(0, splitString.length() - 5);
            });
            modelList.add(location);
        }
        return modelList;
    }

    public static UnbakedModel getVariantModel(ResourceLocation id, Function<ResourceLocation, UnbakedModel> consumer) {
        if (!id.getPath().startsWith("enchiridion/"))
            return null;

        return consumer.apply(new ModelResourceLocation(id.getNamespace(), id.getPath(), "inventory"));
    }

}
