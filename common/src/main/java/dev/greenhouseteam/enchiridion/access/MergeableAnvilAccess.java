package dev.greenhouseteam.enchiridion.access;

import net.minecraft.world.inventory.ResultContainer;

public interface MergeableAnvilAccess {
     boolean enchiridion$merged();

     boolean enchiridion$hasOneResult();
     void enchiridion$setHasOneResult(boolean value);

     ResultContainer enchiridion$getMergeContainer();
}
