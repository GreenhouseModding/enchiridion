package dev.greenhouseteam.enchiridion.fabric.mixin;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Predicate;

@Mixin(LootPool.class)
public interface LootPoolAccessor {
    @Accessor("conditions") @Mutable
    void enchiridion$setConditions(List<LootItemCondition> value);

    @Accessor("compositeCondition") @Mutable
    void enchiridion$setCompositeCondition(Predicate<LootContext> value);
}
