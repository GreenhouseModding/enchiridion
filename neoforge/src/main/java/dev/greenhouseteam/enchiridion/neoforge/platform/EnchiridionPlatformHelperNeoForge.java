package dev.greenhouseteam.enchiridion.neoforge.platform;

import dev.greenhouseteam.enchiridion.neoforge.EnchiridionNeoForge;
import dev.greenhouseteam.enchiridion.neoforge.mixin.ChunkMapAccessor;
import dev.greenhouseteam.enchiridion.neoforge.mixin.EntityTrackerAccessor;
import dev.greenhouseteam.enchiridion.neoforge.registry.EnchiridionAttachments;
import dev.greenhouseteam.enchiridion.network.clientbound.SyncEnchantmentLevelUpSeedsClientboundPacket;
import dev.greenhouseteam.enchiridion.platform.EnchiridionPlatformHelper;
import dev.greenhouseteam.enchiridion.platform.Platform;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkSource;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EnchiridionPlatformHelperNeoForge implements EnchiridionPlatformHelper {

    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public RegistryAccess getReqistryAccess() {
        return EnchiridionNeoForge.getRegistryAccess();
    }

    @Override
    public Collection<ServerPlayer> getTracking(Entity entity) {
        ChunkSource source = entity.level().getChunkSource();

        if (source instanceof ServerChunkCache) {
            ChunkMap chunkLoadingManager = ((ServerChunkCache) source).chunkMap;
            EntityTrackerAccessor tracker = ((EntityTrackerAccessor)((ChunkMapAccessor) chunkLoadingManager).getEntityMap().get(entity.getId()));

            if (tracker != null)
                return tracker.getSeenBy()
                        .stream().map(ServerPlayerConnection::getPlayer).collect(Collectors.toUnmodifiableSet());
        }
        return Collections.emptySet();
    }

    @Override
    public void sendClientbound(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendServerbound(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    @Override
    public void setFrozenByEnchantment(Entity entity, boolean value) {
        if (value)
            entity.setData(EnchiridionAttachments.FROZEN_BY_ENCHANTMENT, true);
        entity.removeData(EnchiridionAttachments.FROZEN_BY_ENCHANTMENT);
    }

    @Override
    public boolean containsEnchantmentSeed(Player player, int index) {
        return player.getExistingData(EnchiridionAttachments.ENCHANTMENT_LEVEL_UP_SEEDS).orElse(List.of()).size() < index;
    }

    @Override
    public int getEnchantmentSeed(Player player, int index) {
        return player.getExistingData(EnchiridionAttachments.ENCHANTMENT_LEVEL_UP_SEEDS).map(integers -> integers.get(index)).orElse(-1);
    }

    @Override
    public void addEnchantmentSeed(Player player, int index, int seed) {
        player.getData(EnchiridionAttachments.ENCHANTMENT_LEVEL_UP_SEEDS).add(index, seed);

        if (!player.level().isClientSide())
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, SyncEnchantmentLevelUpSeedsClientboundPacket.add(player.getId(), index, seed));
    }

    @Override
    public void clearEnchantmentSeeds(Player player) {
        player.removeData(EnchiridionAttachments.ENCHANTMENT_LEVEL_UP_SEEDS);
    }

    @Override
    public boolean isFrozenByEnchantment(Entity entity) {
        return entity.getExistingData(EnchiridionAttachments.FROZEN_BY_ENCHANTMENT).orElse(false);
    }

    @Override
    public boolean isClientThread() {
        return EffectiveSide.get().isClient();
    }
}