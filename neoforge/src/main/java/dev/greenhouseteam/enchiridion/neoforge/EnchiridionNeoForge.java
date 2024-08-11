package dev.greenhouseteam.enchiridion.neoforge;

import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.enchantment.category.EnchantmentCategory;
import dev.greenhouseteam.enchiridion.neoforge.platform.EnchiridionPlatformHelperNeoForge;
import dev.greenhouseteam.enchiridion.network.clientbound.SyncEnchantScrollIndexClientboundPacket;
import dev.greenhouseteam.enchiridion.network.clientbound.SyncEnchantedFrozenStateClientboundPacket;
import dev.greenhouseteam.enchiridion.network.clientbound.SyncEnchantmentLevelUpSeedsClientboundPacket;
import dev.greenhouseteam.enchiridion.network.serverbound.SyncEnchantScrollIndexServerboundPacket;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import dev.greenhouseteam.enchiridion.registry.EnchiridionEnchantmentEffectComponents;
import dev.greenhouseteam.enchiridion.registry.EnchiridionEntityEnchantmentEffects;
import dev.greenhouseteam.enchiridion.registry.EnchiridionRegistries;
import dev.greenhouseteam.enchiridion.registry.internal.RegistrationCallback;
import dev.greenhouseteam.enchiridion.util.ClientRegistryAccessReference;
import dev.greenhouseteam.enchiridion.util.CreativeTabUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Set;
import java.util.function.Consumer;

@Mod(Enchiridion.MOD_ID)
public class EnchiridionNeoForge {

    public EnchiridionNeoForge(IEventBus eventBus) {
        Enchiridion.init(new EnchiridionPlatformHelperNeoForge());
    }

    @EventBusSubscriber(modid = Enchiridion.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void modifyCreativeTabs(BuildCreativeModeTabContentsEvent event) {
            CreativeTabUtil.sortEnchantmentsBasedOnCategory((Set<ItemStack>) event.getTab().getDisplayItems(), event.getParameters().holders());
            CreativeTabUtil.sortEnchantmentsBasedOnCategory((Set<ItemStack>) event.getTab().getSearchTabDisplayItems(), event.getParameters().holders());
        }

        @SubscribeEvent
        public static void registerPackets(RegisterPayloadHandlersEvent event) {
            event.registrar("1.0.0")
                    .playToClient(SyncEnchantedFrozenStateClientboundPacket.TYPE, SyncEnchantedFrozenStateClientboundPacket.STREAM_CODEC, (packet, context) -> packet.handle())
                    .playToClient(SyncEnchantScrollIndexClientboundPacket.TYPE, SyncEnchantScrollIndexClientboundPacket.STREAM_CODEC, (packet, context) -> packet.handle())
                    .playToClient(SyncEnchantmentLevelUpSeedsClientboundPacket.TYPE, SyncEnchantmentLevelUpSeedsClientboundPacket.STREAM_CODEC, (packet, context) -> packet.handle())
                    .playToServer(SyncEnchantScrollIndexServerboundPacket.TYPE, SyncEnchantScrollIndexServerboundPacket.STREAM_CODEC, (packet, context) -> packet.handle((ServerPlayer)context.player()));
        }

        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            register(event, EnchiridionDataComponents::registerAll);
            register(event, EnchiridionEnchantmentEffectComponents::registerAll);
            register(event, EnchiridionEntityEnchantmentEffects::registerAll);
        }

        private static <T> void register(RegisterEvent event, Consumer<RegistrationCallback<T>> consumer) {
            consumer.accept((registry, id, object) -> event.register(registry.key(), id, () -> object));
        }

        @SubscribeEvent
        public static void newDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(EnchiridionRegistries.ENCHANTMENT_CATEGORY, EnchantmentCategory.DIRECT_CODEC, EnchantmentCategory.DIRECT_CODEC);
        }
    }

    public static RegistryAccess getRegistryAccess() {
        if (ServerLifecycleHooks.getCurrentServer() == null || !ServerLifecycleHooks.getCurrentServer().isDedicatedServer())
            return ClientRegistryAccessReference.get(ServerLifecycleHooks.getCurrentServer());
        return ServerLifecycleHooks.getCurrentServer().registryAccess();
    }
}