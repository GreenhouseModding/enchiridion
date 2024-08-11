package dev.greenhouseteam.enchiridion.fabric.client;

import dev.greenhouseteam.enchiridion.client.EnchiridionClient;
import dev.greenhouseteam.enchiridion.client.util.EnchiridionModelUtil;
import dev.greenhouseteam.enchiridion.fabric.client.platform.EnchiridionClientPlatformHelperFabric;
import dev.greenhouseteam.enchiridion.network.clientbound.SyncEnchantScrollIndexClientboundPacket;
import dev.greenhouseteam.enchiridion.network.clientbound.SyncEnchantedFrozenStateClientboundPacket;
import dev.greenhouseteam.enchiridion.network.clientbound.SyncEnchantmentLevelUpSeedsClientboundPacket;
import dev.greenhouseteam.enchiridion.util.TooltipUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.concurrent.CompletableFuture;

public class EnchiridionFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EnchiridionClient.init(new EnchiridionClientPlatformHelperFabric());
        registerPackets();

        PreparableModelLoadingPlugin.register(EnchiridionModelUtil::getEnchiridionModels,
                (data, pluginContext) -> pluginContext.addModels(data));

        ItemTooltipCallback.EVENT.register(TooltipUtil::modifyEnchantmentTooltips);
    }

    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SyncEnchantScrollIndexClientboundPacket.TYPE, (payload, context) -> payload.handle());
        ClientPlayNetworking.registerGlobalReceiver(SyncEnchantedFrozenStateClientboundPacket.TYPE, (payload, context) -> payload.handle());
        ClientPlayNetworking.registerGlobalReceiver(SyncEnchantmentLevelUpSeedsClientboundPacket.TYPE, (payload, context) -> payload.handle());
    }
}
