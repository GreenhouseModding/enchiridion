package dev.greenhouseteam.enchiridion.neoforge.registry;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.neoforge.loot.NoneLootModifier;
import dev.greenhouseteam.enchiridion.registry.internal.RegistrationCallback;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EnchiridionLootModifiers {
    public static void registerAll(RegistrationCallback<MapCodec<? extends IGlobalLootModifier>> callback) {
        callback.register(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Enchiridion.asResource("none"), NoneLootModifier.CODEC);
    }
}
