package dev.greenhouseteam.enchiridion.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sun.jna.platform.win32.OaIdl;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record RunFunctionOnLootEffect(List<LootItemFunction> functions, Optional<ItemPredicate> predicate, Optional<ResourceKey<LootTable>> tableId) {
    public static final Codec<RunFunctionOnLootEffect> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            LootItemFunctions.ROOT_CODEC.listOf().fieldOf("functions").forGetter(RunFunctionOnLootEffect::functions),
            ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(RunFunctionOnLootEffect::predicate),
            ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("table").forGetter(RunFunctionOnLootEffect::tableId)
    ).apply(inst, RunFunctionOnLootEffect::new));

    public RunFunctionOnLootEffect(List<LootItemFunction> functions) {
        this(functions, Optional.empty(), Optional.empty());
    }

    public RunFunctionOnLootEffect(List<LootItemFunction> functions, ResourceKey<LootTable> tableId) {
        this(functions, Optional.empty(), Optional.of(tableId));
    }

    public RunFunctionOnLootEffect(List<LootItemFunction> functions, ItemPredicate predicate) {
        this(functions, Optional.of(predicate), Optional.empty());
    }

    public RunFunctionOnLootEffect(List<LootItemFunction> functions, ItemPredicate predicate, ResourceKey<LootTable> tableId) {
        this(functions, Optional.of(predicate), Optional.of(tableId));
    }

    public static List<ItemStack> modifyLoot(List<RunFunctionOnLootEffect> effects, List<ItemStack> stacks, LootContext context, ResourceKey<LootTable> lootTableKey) {
        List<ItemStack> newList = new ArrayList<>();
        for (ItemStack stack : stacks) {
            ItemStack newStack = stack.copy();
            for (RunFunctionOnLootEffect effect : effects) {
                if (effect.predicate.isPresent() && !effect.predicate.get().test(stack))
                    continue;
                if (effect.tableId.isPresent() && !effect.tableId.get().equals(lootTableKey))
                    continue;
                for (LootItemFunction function : effect.functions)
                    newStack = function.apply(stack, context);
            }
            newList.add(newStack);
        }
        return newList;
    }
}
