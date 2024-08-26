package dev.greenhouseteam.enchiridion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.access.MergeableAnvilAccess;
import dev.greenhouseteam.enchiridion.enchantment.category.EnchantmentCategory;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import dev.greenhouseteam.enchiridion.registry.EnchiridionEnchantmentCategories;
import dev.greenhouseteam.enchiridion.util.AnvilUtil;
import dev.greenhouseteam.enchiridion.util.EnchiridionUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

// TODO: Allow cycling through same category enchantments from books.
// TODO: Figure out how to handle durability when swapping.
// TODO: Config to disable all this.
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu implements MergeableAnvilAccess {
    @Shadow private int repairItemCountCost;

    @Shadow @Final private DataSlot cost;

    @Shadow public abstract void createResult();

    @Shadow @Nullable private String itemName;

    @Unique
    private final ResultContainer enchiridion$mergeSlots = new ResultContainer();

    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int syncId, Inventory inventory, ContainerLevelAccess access) {
        super(menuType, syncId, inventory, access);
    }

    @ModifyArg(method = "createInputSlotDefinitions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;withResultSlot(III)Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;"), index = 1)
    private int enchiridion$modifyResultSlotX(int i) {
        return i - 4;
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void enchiridion$addExtraRepairSlot(int containerId, Inventory playerInventory, ContainerLevelAccess access, CallbackInfo ci) {
        AnvilUtil.setAnvilMergeSlotIndex(this.slots.size());
        this.addSlot(new Slot(this.enchiridion$mergeSlots, AnvilUtil.getAnvilMergeSlotIndex(), 151, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return AnvilMenuMixin.this.mayPickup(player, this.hasItem());
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                if (enchiridion$merged)
                    enchiridion$hasOneResult = !enchiridion$hasOneResult;
                AnvilMenuMixin.this.onTake(player, stack);
            }
        });
    }

    @Unique
    private boolean enchiridion$merged = false;
    @Unique
    private boolean enchiridion$hasOneResult = false;

    @ModifyReturnValue(method = "mayPickup", at = @At("RETURN"))
    private boolean enchiridion$allowPickingUpOtherItem(boolean original) {
        return original || enchiridion$hasOneResult;
    }

    @ModifyExpressionValue(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal =  2))
    private boolean enchiridion$cancelBonusCostFromItem(boolean original) {
        return true;
    }

    @ModifyExpressionValue(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;areCompatible(Lnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)Z"))
    private boolean enchiridion$allowMergingIncompatibleEnchantments(boolean original) {
        return true;
    }

    @Unique
    private final Set<Object2IntMap.Entry<Holder<Enchantment>>> enchiridion$otherEnchantments = new HashSet<>();

    @Inject(method = "createResult", at = @At("HEAD"))
    private void enchiridion$setAnvilContext(CallbackInfo ci) {
        AnvilUtil.setAnvilContext(true);
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    private void enchiridion$clearResultStack(CallbackInfo ci) {
        AnvilUtil.setAnvilContext(false);
    }

    @ModifyArg(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack enchiridion$removeMergeItem(ItemStack original) {
        if (original.isEmpty())
            enchiridion$mergeSlots.setItem(0, ItemStack.EMPTY);
        return original;
    }

    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void enchiridion$dontAttemptToDamageAnvil(Player player, ItemStack stack, CallbackInfo ci) {
        if (enchiridion$merged && !enchiridion$hasOneResult) {
            enchiridion$merged = false;
            ci.cancel();
        }
        else {
            itemName = null;
        }
    }

    @ModifyVariable(method = "createResult", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 1), ordinal = 0)
    private int enchiridion$setEnchantmentAnvilCost(int original,
                                                    @Local(ordinal = 1) ItemStack input, @Local(ordinal = 2) ItemStack otherInput,
                                                    @Local(ordinal = 2) int enchantmentLevel, @Local(ordinal = 3) int inputEnchantmentLevel,
                                                    @Local(ordinal = 4) int anvilCost,
                                                    @Local ItemEnchantments.Mutable mutable,
                                                    @Local Holder<Enchantment> enchantment) {
        ItemEnchantmentCategories inputCategories = input.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY);
        ItemEnchantmentCategories otherCategories = otherInput.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY);

        Holder<EnchantmentCategory> category = otherCategories.findFirstCategory(enchantment);
        if (category == null || !category.isBound())
            category = inputCategories.findFirstCategory(enchantment);

        if (!inputCategories.get(category).isEmpty() && !otherCategories.get(category).isEmpty() && category.isBound() &&
                (inputCategories.get(category).size() >= category.value().limit().orElse(Integer.MAX_VALUE) ||
                        inputCategories.get(category).size() >= category.value().limit().orElse(Integer.MAX_VALUE)) && enchantmentLevel < inputEnchantmentLevel) {
            original -= anvilCost * inputEnchantmentLevel;
            original += 1;
        } else if (enchantmentLevel == inputEnchantmentLevel)
            original -= anvilCost * inputEnchantmentLevel;

        return original;
    }

    @ModifyArg(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 4), index = 1)
    private ItemStack enchiridion$setAnvilItem(ItemStack original) {
        if (repairItemCountCost > 0 || original.isEmpty())
            return original;

        ItemStack input = this.inputSlots.getItem(0);
        ItemStack otherInput = this.inputSlots.getItem(1);

        if (EnchantmentHelper.getEnchantmentsForCrafting(otherInput).isEmpty())
            return original;

        ItemEnchantmentCategories inputCategories = input.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY);
        ItemEnchantmentCategories otherCategories = otherInput.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY);

        if (EnchantmentHelper.getEnchantmentsForCrafting(input).equals(EnchantmentHelper.getEnchantmentsForCrafting(otherInput)) && inputCategories.equals(otherCategories))
            return original;

        original.remove(EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original));
        original.remove(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES);

        ItemEnchantmentCategories newCategories = new ItemEnchantmentCategories();
        ItemEnchantments.Mutable itemEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        enchiridion$otherEnchantments.clear();
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(otherInput).entrySet()) {
            Holder<EnchantmentCategory> category = otherCategories.findFirstCategory(entry.getKey());
            if (category == null || !category.isBound())
                category = inputCategories.findFirstCategory(entry.getKey());
            if (category != null && category.is(EnchiridionEnchantmentCategories.CURSE)) {
                if (!itemEnchantments.keySet().contains(entry.getKey()) || entry.getIntValue() > itemEnchantments.getLevel(entry.getKey())) {
                    newCategories.addCategoryWithEnchantment(category, entry.getKey());
                    itemEnchantments.set(entry.getKey(), entry.getIntValue());
                    enchiridion$otherEnchantments.add(entry);
                }
            } else if (EnchiridionUtil.getAllEnchantmentCategories(player.level().registryAccess(), entry.getKey()).isEmpty() && (itemEnchantments.keySet().contains(entry.getKey()) && entry.getIntValue() > itemEnchantments.getLevel(entry.getKey()) || EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), entry.getKey()) && (player.getAbilities().instabuild || EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original) == DataComponents.STORED_ENCHANTMENTS || Enchiridion.getHelper().supportsEnchantment(original, entry.getKey()))))
                itemEnchantments.set(entry.getKey(), entry.getIntValue());
            else if (category != null && (itemEnchantments.keySet().contains(entry.getKey()) && entry.getIntValue() > itemEnchantments.getLevel(entry.getKey()) || newCategories.isValid(category, entry.getKey(), EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original)) && EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), entry.getKey()) && (player.getAbilities().instabuild || EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original) == DataComponents.STORED_ENCHANTMENTS || Enchiridion.getHelper().supportsEnchantment(original, entry.getKey())))) {
                newCategories.addCategoryWithEnchantment(category, entry.getKey());
                itemEnchantments.set(entry.getKey(), entry.getIntValue());
            } else if (EnchantmentHelper.getEnchantmentsForCrafting(otherInput).entrySet().stream().noneMatch(entry1 -> entry1.getKey() == entry.getKey() && entry1.getIntValue() < entry.getIntValue()) && (player.getAbilities().instabuild || EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original) == DataComponents.STORED_ENCHANTMENTS || Enchiridion.getHelper().supportsEnchantment(original, entry.getKey())))
                enchiridion$otherEnchantments.add(entry);
        }
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(input).entrySet()) {
            Holder<EnchantmentCategory> category = otherCategories.findFirstCategory(entry.getKey());
            if (category == null || !category.isBound())
                category = inputCategories.findFirstCategory(entry.getKey());
            if (category != null && category.is(EnchiridionEnchantmentCategories.CURSE)) {
                if (!itemEnchantments.keySet().contains(entry.getKey()) || entry.getIntValue() > itemEnchantments.getLevel(entry.getKey())) {
                    newCategories.addCategoryWithEnchantment(category, entry.getKey());
                    itemEnchantments.set(entry.getKey(), entry.getIntValue());
                    enchiridion$otherEnchantments.add(entry);
                }
            } else if (EnchiridionUtil.getAllEnchantmentCategories(player.level().registryAccess(), entry.getKey()).isEmpty() && (itemEnchantments.keySet().contains(entry.getKey()) && entry.getIntValue() > itemEnchantments.getLevel(entry.getKey()) || EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), entry.getKey()) && (player.getAbilities().instabuild || EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original) == DataComponents.STORED_ENCHANTMENTS || Enchiridion.getHelper().supportsEnchantment(original, entry.getKey()))))
                itemEnchantments.set(entry.getKey(), entry.getIntValue());
            else if (category != null && (itemEnchantments.keySet().contains(entry.getKey()) && entry.getIntValue() > itemEnchantments.getLevel(entry.getKey()) || newCategories.isValid(category, entry.getKey(), EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original)) && EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), entry.getKey()) && (player.getAbilities().instabuild || EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original) == DataComponents.STORED_ENCHANTMENTS || Enchiridion.getHelper().supportsEnchantment(original, entry.getKey())))) {
                newCategories.addCategoryWithEnchantment(category, entry.getKey());
                itemEnchantments.set(entry.getKey(), entry.getIntValue());
            } else if (EnchantmentHelper.getEnchantmentsForCrafting(input).entrySet().stream().noneMatch(entry1 -> entry1.getKey() == entry.getKey() && entry1.getIntValue() > entry.getIntValue()) && (player.getAbilities().instabuild || EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original) == DataComponents.STORED_ENCHANTMENTS || Enchiridion.getHelper().supportsEnchantment(original, entry.getKey())))
                enchiridion$otherEnchantments.add(entry);
        }

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(original).entrySet().stream().filter(entry -> !EnchantmentHelper.getEnchantmentsForCrafting(input).keySet().contains(entry.getKey()) && !EnchantmentHelper.getEnchantmentsForCrafting(otherInput).keySet().contains(entry.getKey())).toList()) {
            Holder<EnchantmentCategory> category = otherCategories.findFirstCategory(entry.getKey());
            if (category == null || !category.isBound())
                category = inputCategories.findFirstCategory(entry.getKey());
            if (EnchiridionUtil.getAllEnchantmentCategories(player.level().registryAccess(), entry.getKey()).isEmpty() && EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), entry.getKey()) && (player.getAbilities().instabuild || EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original) == DataComponents.STORED_ENCHANTMENTS || Enchiridion.getHelper().supportsEnchantment(original, entry.getKey())))
                itemEnchantments.set(entry.getKey(), entry.getIntValue());
            else if (category != null && newCategories.isValid(category, entry.getKey(), EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original)) && EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), entry.getKey()) && (player.getAbilities().instabuild || EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original) == DataComponents.STORED_ENCHANTMENTS || Enchiridion.getHelper().supportsEnchantment(original, entry.getKey()))) {
                newCategories.addCategoryWithEnchantment(category, entry.getKey());
                itemEnchantments.set(entry.getKey(), entry.getIntValue());
            }
        }

        if (itemEnchantments.toImmutable().equals(EnchantmentHelper.getEnchantmentsForCrafting(input))) {
            this.cost.set(0);
            return ItemStack.EMPTY;
        }

        if (!itemEnchantments.keySet().isEmpty()) {
            if (!inputCategories.isEmpty() && inputCategories.getCategories().keySet().equals(otherCategories.getCategories().keySet()) && newCategories.getCategories().entrySet().stream().allMatch(entry -> inputCategories.getCategories().containsKey(entry.getKey()) && entry.getValue().size() == inputCategories.get(entry.getKey()).size() || !otherCategories.isEmpty() && otherCategories.getCategories().containsKey(entry.getKey()) && entry.getValue().size() == otherCategories.get(entry.getKey()).size()))
                this.cost.set(1);
            original.set(EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(original), itemEnchantments.toImmutable());
            original.set(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, newCategories);
            EnchantmentHelper.setEnchantments(original, itemEnchantments.toImmutable());

            if (original.isDamageableItem() && !input.getEnchantments().isEmpty() && !otherInput.getEnchantments().isEmpty()) {
                original.setDamageValue(input.getDamageValue());
                if (!otherInput.has(DataComponents.STORED_ENCHANTMENTS)) {
                    int l = input.getMaxDamage() - input.getDamageValue();
                    int i1 = otherInput.getMaxDamage() - otherInput.getDamageValue();
                    int j1 = i1 + input.getMaxDamage() * 12 / 100;
                    int k1 = l + j1;
                    int l1 = input.getMaxDamage() - k1;
                    if (l1 < 0) {
                        l1 = 0;
                    }

                    if (l1 < input.getDamageValue()) {
                        cost.set(cost.get() - 2);
                    }
                }
            }
        }

        if (!enchiridion$otherEnchantments.isEmpty()) {
            ItemEnchantmentCategories mergeCategories = new ItemEnchantmentCategories();
            ItemEnchantments.Mutable mergeEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchiridion$otherEnchantments) {
                Holder<EnchantmentCategory> category = inputCategories.findFirstCategory(entry.getKey());
                if (category == null || !category.isBound())
                    category = otherCategories.findFirstCategory(entry.getKey());
                mergeEnchantments.set(entry.getKey(), entry.getIntValue());
                if (category != null)
                    mergeCategories.addCategoryWithEnchantment(category, entry.getKey());
            }

            enchiridion$otherEnchantments.clear();

            if (!itemEnchantments.keySet().isEmpty() || !newCategories.isEmpty()) {
                ItemStack otherCopy = otherInput.copy();
                otherCopy.set(EnchantmentHelperAccessor.enchiridion$invokeGetComponentType(otherCopy), mergeEnchantments.toImmutable());
                otherCopy.set(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, mergeCategories);
                enchiridion$mergeSlots.setItem(0, otherCopy);
                enchiridion$merged = true;
            }
        }

        return original;
    }

    @ModifyExpressionValue(method = "setItemName", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    private boolean enchiridion$preventNameChangingWithOtherResult(boolean original) {
        return original || enchiridion$hasOneResult;
    }

    @Override
    public boolean enchiridion$merged() {
        return enchiridion$merged;
    }

    @Override
    public boolean enchiridion$hasOneResult() {
        return enchiridion$hasOneResult;
    }

    @Override
    public void enchiridion$setHasOneResult(boolean value) {
        enchiridion$hasOneResult = value;
    }

    @Override
    public ResultContainer enchiridion$getMergeContainer() {
        return enchiridion$mergeSlots;
    }
}
