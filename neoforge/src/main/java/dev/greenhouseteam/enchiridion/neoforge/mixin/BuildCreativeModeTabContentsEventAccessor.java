package dev.greenhouseteam.enchiridion.neoforge.mixin;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.InsertableLinkedOpenCustomHashSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuildCreativeModeTabContentsEvent.class)
public interface BuildCreativeModeTabContentsEventAccessor {
    @Accessor("parentEntries")
    InsertableLinkedOpenCustomHashSet<ItemStack> enchiridion$getParentEntries();

    @Accessor("searchEntries")
    InsertableLinkedOpenCustomHashSet<ItemStack> enchiridion$getSearchEntries();
}
