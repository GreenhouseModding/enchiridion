package dev.greenhouseteam.enchiridion.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.platform.Platform;
import dev.greenhouseteam.enchiridion.registry.EnchiridionEnchantmentEffectComponents;
import dev.greenhouseteam.enchiridion.registry.EnchiridionLootContextParamSets;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Projectile {

    public FishingHookMixin(EntityType<? extends Projectile> projectile, Level level) {
        super(projectile, level);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int enchiridion$modifyFishingExperience(int i, @Local(argsOnly = true) ItemStack fishingRod, @Local Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Map.Entry<Holder<Enchantment>, Integer> entry : fishingRod.getEnchantments().entrySet().stream().filter(entry -> entry.getKey().isBound() && !entry.getKey().value().getEffects(EnchiridionEnchantmentEffectComponents.FISHING_EXPERIENCE_BONUS).isEmpty()).toList()) {
                EquipmentSlot slot = null;
                for (EquipmentSlot slot1 : EquipmentSlot.values()) {
                    if (player.getItemBySlot(slot1).equals(fishingRod)) {
                        slot = slot1;
                        break;
                    }
                }
                if (slot == null || !entry.getKey().value().matchingSlot(slot))
                    continue;

                LootParams.Builder paramBuilder = new LootParams.Builder(serverLevel);
                paramBuilder.withParameter(LootContextParams.TOOL, fishingRod);
                paramBuilder.withParameter(LootContextParams.ORIGIN, position());
                paramBuilder.withParameter(LootContextParams.ENCHANTMENT_LEVEL, entry.getValue());
                paramBuilder.withParameter(LootContextParams.THIS_ENTITY, player);

                LootContext context = new LootContext.Builder(paramBuilder.create(EnchiridionLootContextParamSets.ENCHANTED_FISHING)).create(Optional.empty());

                for (ConditionalEffect<EnchantmentValueEffect> effect : entry.getKey().value().getEffects(EnchiridionEnchantmentEffectComponents.FISHING_EXPERIENCE_BONUS)) {
                    if (!effect.matches(context))
                        continue;

                    i = Mth.floor(effect.effect().process(entry.getValue(), player.getRandom(), (float)i));
                }
            }
        }
        return i;
    }
}
