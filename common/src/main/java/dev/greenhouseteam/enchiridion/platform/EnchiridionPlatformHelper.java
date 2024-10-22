package dev.greenhouseteam.enchiridion.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Collection;

public interface EnchiridionPlatformHelper {

    Platform getPlatform();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    RegistryAccess getReqistryAccess();

    Collection<ServerPlayer> getTracking(Entity entity);

    void sendClientbound(ServerPlayer player, CustomPacketPayload payload);

    void sendServerbound(CustomPacketPayload payload);

    void setFrozenByEnchantment(Entity entity, boolean value);

    boolean containsEnchantmentSeed(Player player, int index);

    int getEnchantmentSeed(Player player, int index);

    void addEnchantmentSeed(Player player, int index, int seed);

    void clearEnchantmentSeeds(Player player);

    boolean isFrozenByEnchantment(Entity entity);

    boolean isClientThread();

    boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment);
}