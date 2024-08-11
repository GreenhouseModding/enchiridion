package dev.greenhouseteam.enchiridion.neoforge.registry;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.ArrayList;
import java.util.List;

public class EnchiridionAttachments {
    public static final AttachmentType<Boolean> FROZEN_BY_ENCHANTMENT = AttachmentType.builder(() -> false)
            .serialize(Codec.BOOL)
            .build();
    public static final AttachmentType<List<Integer>> ENCHANTMENT_LEVEL_UP_SEEDS = AttachmentType.<List<Integer>>builder(() -> new ArrayList<>())
            .serialize(Codec.INT.listOf())
            .build();
}
