package dev.greenhouseteam.enchiridion.fabric.registry;

import com.mojang.serialization.Codec;
import dev.greenhouseteam.enchiridion.Enchiridion;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import java.util.List;

public class EnchiridionAttachments {
    public static final AttachmentType<List<Integer>> ENCHANTMENT_LEVEL_UP_SEEDS = AttachmentRegistry
            .createPersistent(Enchiridion.asResource("enchantment_level_up_seeds"), Codec.INT.listOf());
    public static final AttachmentType<Boolean> FROZEN_BY_ENCHANTMENT = AttachmentRegistry
            .createPersistent(Enchiridion.asResource("frozen_by_enchantment"), Codec.BOOL);
}
