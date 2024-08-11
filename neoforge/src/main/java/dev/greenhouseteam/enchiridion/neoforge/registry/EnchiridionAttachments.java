package dev.greenhouseteam.enchiridion.neoforge.registry;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.registry.internal.RegistrationCallback;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class EnchiridionAttachments {
    public static final AttachmentType<List<Integer>> ENCHANTMENT_LEVEL_UP_SEEDS = AttachmentType.<List<Integer>>builder(() -> new ArrayList<>())
            .serialize(Codec.INT.listOf())
            .build();
    public static final AttachmentType<Boolean> FROZEN_BY_ENCHANTMENT = AttachmentType.builder(() -> false)
            .serialize(Codec.BOOL)
            .build();

    public static void registerAll(RegistrationCallback<AttachmentType<?>> callback) {
        callback.register(NeoForgeRegistries.ATTACHMENT_TYPES, Enchiridion.asResource("enchantment_level_up_seeds"), ENCHANTMENT_LEVEL_UP_SEEDS);
        callback.register(NeoForgeRegistries.ATTACHMENT_TYPES, Enchiridion.asResource("frozen_by_enchantment"), FROZEN_BY_ENCHANTMENT);
    }
}
