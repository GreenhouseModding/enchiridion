package dev.greenhouseteam.enchiridion.client.util;

import dev.greenhouseteam.enchiridion.Enchiridion;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class EnchiridionModelUtil {
    public static final ResourceLocation ENCHANTED_BOOK_COLORED = Enchiridion.asResource("item/enchiridion/enchanted_book_colored");
    public static final ResourceLocation ENCHANTED_BOOK_MISC = Enchiridion.asResource("item/enchiridion/enchanted_book_misc");
    public static final ResourceLocation ENCHANTED_BOOK_RED = Enchiridion.asResource("item/enchiridion/enchanted_book_curse");

    public static CompletableFuture<List<ResourceLocation>> getEnchiridionModels(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> manager.listResources("models/item/enchiridion", fileName -> fileName.getPath().endsWith(".json")), executor).thenCompose(entries -> {
            List<CompletableFuture<ResourceLocation>> models = new ArrayList<>();
            for (Map.Entry<ResourceLocation, Resource> entry : entries.entrySet()) {
                models.add(CompletableFuture.supplyAsync(() -> entry.getKey().withPath(string -> {
                    String splitString = string.split("/", 2)[1];
                    return splitString.substring(0, splitString.length() - 5);
                }), executor));
            }
            return Util.sequenceFailFast(models).thenApply(rls -> rls.stream().filter(Objects::nonNull).toList());
        });
    }

}
