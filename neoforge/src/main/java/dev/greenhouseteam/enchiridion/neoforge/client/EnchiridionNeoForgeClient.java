package dev.greenhouseteam.enchiridion.neoforge.client;

import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.client.EnchiridionClient;
import dev.greenhouseteam.enchiridion.client.util.EnchiridionModelUtil;
import dev.greenhouseteam.enchiridion.neoforge.client.platform.EnchiridionClientPlatformHelperNeoForge;
import dev.greenhouseteam.enchiridion.util.TooltipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@Mod(value = Enchiridion.MOD_ID, dist = Dist.CLIENT)
public class EnchiridionNeoForgeClient {

    public EnchiridionNeoForgeClient(IEventBus bus) {
        EnchiridionClient.init(new EnchiridionClientPlatformHelperNeoForge());
    }

    @EventBusSubscriber(modid = Enchiridion.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {

        @SubscribeEvent
        public static void bakeModels(ModelEvent.ModifyBakingResult event) {
            List<ResourceLocation> models = EnchiridionModelUtil.getEnchiridionModels(Minecraft.getInstance().getResourceManager(), Runnable::run).join();

            for (ResourceLocation entry : models) {
                UnbakedModel unbaked = event.getModelBakery().getModel(entry);
                unbaked.resolveParents(location -> event.getModelBakery().getModel(location));
                ModelResourceLocation modelResource = ModelResourceLocation.standalone(entry);
                BakedModel model = unbaked.bake(event.getModelBakery().new ModelBakerImpl((rl, material) -> material.sprite(), modelResource), event.getTextureGetter(), BlockModelRotation.X0_Y0);
                event.getModels().put(modelResource, model);
            }
        }

    }

    @EventBusSubscriber(modid = Enchiridion.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameEvents {
        @SubscribeEvent
        public static void modifyTooltip(ItemTooltipEvent event) {
            TooltipUtil.modifyEnchantmentTooltips(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip());
        }
    }

}
