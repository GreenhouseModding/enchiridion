package dev.greenhouseteam.enchiridion.neoforge.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.greenhouseteam.enchiridion.mixin.LootParamsAccessor;
import dev.greenhouseteam.enchiridion.neoforge.mixin.LootContextAccessor;
import dev.greenhouseteam.enchiridion.registry.EnchiridionEnchantmentEffectComponents;
import dev.greenhouseteam.enchiridion.registry.EnchiridionLootContextParamSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.Map;
import java.util.Optional;

public class AdditionalEnchantedFishingLootModifier extends LootModifier {
    public static final MapCodec<AdditionalEnchantedFishingLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            IGlobalLootModifier.LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(glm -> glm.conditions)
    ).apply(inst, AdditionalEnchantedFishingLootModifier::new));

    private static ResourceKey<LootTable> previousTable;

    public AdditionalEnchantedFishingLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        LootParams.Builder paramBuilder = new LootParams.Builder(context.getLevel());
        for (Map.Entry<LootContextParam<?>, Object> param : ((LootParamsAccessor)((LootContextAccessor)context).enchiridion$getParams()).enchiridion$getParams().entrySet())
            paramBuilder.withParameter((LootContextParam<Object>) param.getKey(), param.getValue());
        if (context.hasParam(LootContextParams.TOOL)) {
            EquipmentSlot slot = null;
            LivingEntity living = null;
            if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof FishingHook hook)
                living = hook.getPlayerOwner();
            else if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof LivingEntity entity)
                living = entity;
            if (living != null) {
                for (EquipmentSlot slot1 : EquipmentSlot.values()) {
                    if (living.getItemBySlot(slot1).equals(context.getParam(LootContextParams.TOOL))) {
                        slot = slot1;
                        break;
                    }
                }
            }
            EquipmentSlot finalSlot = slot;
            for (Map.Entry<Holder<Enchantment>, Integer> entry : context.getParam(LootContextParams.TOOL).getEnchantments().entrySet().stream().filter(entry -> entry.getKey().isBound() && !entry.getKey().value().getEffects(EnchiridionEnchantmentEffectComponents.ADDITIONAL_FISHING_LOOT).isEmpty() && (finalSlot == null && !context.hasParam(LootContextParams.THIS_ENTITY) || entry.getKey().value().matchingSlot(finalSlot))).toList()) {
                paramBuilder.withOptionalParameter(LootContextParams.ENCHANTMENT_LEVEL, entry.getValue());
                LootParams params2 = paramBuilder.create(EnchiridionLootContextParamSets.ENCHANTED_FISHING);
                LootContext fishingContext = new LootContext.Builder(params2).create(Optional.empty());
                for (ConditionalEffect<ResourceKey<LootTable>> effect : entry.getKey().value().getEffects(EnchiridionEnchantmentEffectComponents.ADDITIONAL_FISHING_LOOT)) {
                    if (effect.effect() == previousTable)
                        continue;
                    if (!effect.matches(fishingContext))
                        continue;
                    generatedLoot.addAll(fishingContext.getLevel().getServer().reloadableRegistries().getLootTable(effect.effect()).getRandomItems(params2, params2.getLevel().getRandom()));
                    previousTable = effect.effect();
                }
            }
        }

        previousTable = null;

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}