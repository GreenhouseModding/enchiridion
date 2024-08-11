package dev.greenhouseteam.enchiridion.neoforge.loot.condition;

import com.mojang.serialization.MapCodec;
import dev.greenhouseteam.enchiridion.EnchiridionTags;
import dev.greenhouseteam.enchiridion.registry.EnchiridionEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Optional;

public class BaseStoneTableCondition implements LootItemCondition {
    public static final MapCodec<BaseStoneTableCondition> CODEC = MapCodec.unit(BaseStoneTableCondition::new);
    public static final LootItemConditionType TYPE = new LootItemConditionType(CODEC);

    @Override
    public boolean test(LootContext context) {
        if (context.getQueriedLootTableId().getPath().startsWith("blocks/")) {
            Optional<Holder.Reference<Block>> blockHolder = context.getLevel().registryAccess().lookupOrThrow(Registries.BLOCK).get(ResourceKey.create(Registries.BLOCK, context.getQueriedLootTableId().withPath(string -> string.split("/", 2)[1])));
            if (blockHolder.isPresent())
                return context.getLevel().registryAccess().lookupOrThrow(Registries.BLOCK).get(EnchiridionTags.BlockTags.BASE_STONE).map(holders -> holders.contains(blockHolder.get())).orElse(false);
        }
        return false;
    }

    @Override
    public LootItemConditionType getType() {
        return TYPE;
    }
}
