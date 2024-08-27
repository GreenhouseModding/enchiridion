package dev.greenhouseteam.enchiridion.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.greenhouseteam.enchiridion.Enchiridion;
import dev.greenhouseteam.enchiridion.access.LevelUpEnchantmentMenuAccess;
import dev.greenhouseteam.enchiridion.enchantment.category.ItemEnchantmentCategories;
import dev.greenhouseteam.enchiridion.network.clientbound.SyncEnchantScrollIndexClientboundPacket;
import dev.greenhouseteam.enchiridion.registry.EnchiridionDataComponents;
import dev.greenhouseteam.enchiridion.util.EnchantingTableUtil;
import dev.greenhouseteam.enchiridion.util.EnchiridionUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.EnchantingTableBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin  extends AbstractContainerMenu implements LevelUpEnchantmentMenuAccess {
    @Shadow @Final private ContainerLevelAccess access;

    @Shadow @Final public int[] costs;

    @Shadow @Final public int[] enchantClue;

    @Shadow @Final public int[] levelClue;

    @Shadow @Final private RandomSource random;

    @Shadow @Final private DataSlot enchantmentSeed;

    @Shadow @Final private Container enchantSlots;

    @Unique
    private final DataSlot enchiridion$levelableEnchantmentSize = DataSlot.standalone();

    @Unique
    private final DataSlot enchiridion$bookshelfCount = DataSlot.standalone();

    @Unique
    private final int[] enchiridion$requiredBookshelves = new int[]{-1, -1, -1};

    @Unique
    private List<Integer> enchiridion$allLevelUpCosts = new ArrayList<>();

    @Unique
    private List<Integer> enchiridion$allEnchantClues = new ArrayList<>();

    @Unique
    private List<Integer> enchiridion$allLevelClues = new ArrayList<>();

    @Unique
    private List<Integer> enchiridion$allRequiredBookshelves = new ArrayList<>();

    @Unique
    private double enchiridion$scrollOff = 0;

    @Unique
    private Player enchiridion$player;

    protected EnchantmentMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void enchiridion$addDataSlotsToEnchantingTableMenu(int containerId, Inventory inventory, ContainerLevelAccess level, CallbackInfo ci) {
        addDataSlot(enchiridion$levelableEnchantmentSize).set(-1);
        addDataSlot(enchiridion$bookshelfCount).set(-1);
        addDataSlot(DataSlot.shared(enchiridion$requiredBookshelves, 0));
        addDataSlot(DataSlot.shared(enchiridion$requiredBookshelves, 1));
        addDataSlot(DataSlot.shared(enchiridion$requiredBookshelves, 2));

        enchiridion$player = inventory.player;
    }

    @Inject(method = "slotsChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void enchiridion$allowEnchantmentLevelUps(Container container, CallbackInfo ci, ItemStack stack) {
        enchiridion$allLevelUpCosts.clear();
        enchiridion$allEnchantClues.clear();
        enchiridion$allLevelClues.clear();
        enchiridion$allRequiredBookshelves.clear();
        if (!stack.isEmpty() && EnchantmentHelper.hasAnyEnchantments(stack)) {
            access.execute((level, blockPos) -> {
                List<Object2IntMap.Entry<Holder<Enchantment>>> enchantmentSet = new ArrayList<>(stack.getEnchantments().entrySet().stream().toList());
                if (enchantmentSet.isEmpty())
                    enchantmentSet = new ArrayList<>(stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet().stream().toList());
                enchantmentSet = new ArrayList<>(enchantmentSet.stream().filter(entry -> entry.getIntValue() < entry.getKey().value().getMaxLevel()).toList());
                enchantmentSet.sort((o1, o2) -> EnchiridionUtil.compareEnchantments(o1.getKey(), o2.getKey(), stack.getOrDefault(EnchiridionDataComponents.ENCHANTMENT_CATEGORIES, ItemEnchantmentCategories.EMPTY)));

                int i = 0;

                for (BlockPos blockPos2 : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                    if (EnchantingTableBlock.isValidBookShelf(level, blockPos, blockPos2)) {
                        ++i;
                    }
                }

                int scrollOffset = Mth.floor(enchiridion$scrollOff);
                int size = enchantmentSet.size();
                if (scrollOffset > 0 && scrollOffset > size - 3) {
                    enchiridion$scrollOff = size - 3;
                    if (enchiridion$player instanceof ServerPlayer serverPlayer)
                        Enchiridion.getHelper().sendClientbound(serverPlayer, new SyncEnchantScrollIndexClientboundPacket(Mth.floor(enchiridion$scrollOff)));
                }

                random.setSeed(enchantmentSeed.get());
                for (int j = 1; j < size; ++j) {
                    if (!Enchiridion.getHelper().containsEnchantmentSeed(enchiridion$player, j)) {
                        int newSeed = level.random.nextInt();
                        Enchiridion.getHelper().addEnchantmentSeed(enchiridion$player, j - 1, newSeed);
                    }
                }

                int k;
                for (k = 0; k < 3; ++k) {
                    this.costs[k] = 0;
                    this.enchantClue[k] = -1;
                    this.levelClue[k] = -1;
                    enchiridion$requiredBookshelves[k] = -1;
                }

                k = 0;
                for (int l = 0; l < enchantmentSet.size(); ++l) {
                    if (l > 0)
                        random.setSeed(Enchiridion.getHelper().getEnchantmentSeed(enchiridion$player, l - 1));
                    Object2IntMap.Entry<Holder<Enchantment>> enchantment = enchantmentSet.get(l);
                    int maxLevel = enchantment.getKey().value().getMaxLevel();
                    int cost = EnchantingTableUtil.getLevelUpCost(random, enchantment.getIntValue() + 1, maxLevel);
                    int enchantClue = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getId(enchantment.getKey().value());
                    int levelClue = enchantment.getIntValue() + 1;
                    int bookshelfCount = EnchantingTableUtil.getMinimumBookshelfAmountForLevelling(enchantment.getIntValue() + 1, maxLevel);
                    enchiridion$allLevelUpCosts.add(cost);
                    enchiridion$allEnchantClues.add(enchantClue);
                    enchiridion$allLevelClues.add(levelClue);
                    enchiridion$allRequiredBookshelves.add(levelClue);
                    if (k < 3 && l > Mth.floor(enchiridion$scrollOff) - 1) {
                        this.costs[k] = cost;
                        this.enchantClue[k] = enchantClue;
                        this.levelClue[k] = levelClue;
                        enchiridion$requiredBookshelves[k] = bookshelfCount;
                        ++k;
                    }
                }
                enchiridion$bookshelfCount.set(i);
                enchiridion$levelableEnchantmentSize.set(size);
            });
            this.broadcastChanges();
            ci.cancel();
        } else {
            enchiridion$levelableEnchantmentSize.set(-1);
            enchiridion$bookshelfCount.set(-1);
            enchiridion$scrollOff = 0;
            if (enchiridion$player instanceof ServerPlayer serverPlayer)
                Enchiridion.getHelper().sendClientbound(serverPlayer, new SyncEnchantScrollIndexClientboundPacket(0));
            for(int j = 0; j < 3; ++j) {
                enchiridion$requiredBookshelves[j] = -1;
            }
        }
    }

    @ModifyVariable(method = "getEnchantmentList", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private int enchiridion$setEnchantmentLevelToFull(int original) {
        return Math.max(1, Math.round((float) original / 0.67F));
    }

    @ModifyVariable(method = "clickMenuButton", at = @At(value = "LOAD"), ordinal = 1)
    private int enchiridion$modifyLapisCountForLevelUps(int original, Player player, int index, @Local(ordinal = 0) ItemStack stack) {
        if (!stack.isEmpty() && EnchantmentHelper.hasAnyEnchantments(stack)) {
            Holder<Enchantment> enchantment = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap().byId(enchantClue[index]);
            if (enchantment != null && enchantment.isBound())
                return EnchantingTableUtil.getLapisCountForLevelling(levelClue[index], enchantment.value().getMaxLevel());
        }
        return original;
    }

    @Inject(method = "clickMenuButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"), cancellable = true)
    private void enchiridion$performEnchantmentLevelUps(Player player, int index, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) ItemStack stack, @Local(ordinal = 1) ItemStack lapis, @Local(ordinal = 1) int lapisCount) {
        if (!stack.isEmpty() && EnchantmentHelper.hasAnyEnchantments(stack)) {
            access.execute((level, blockPos) -> {
                Holder<Enchantment> enchantment = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap().byId(enchantClue[index]);
                if (enchantment != null && enchantment.isBound()) {
                    if (index + enchiridion$scrollOff > 0)
                        random.setSeed(Enchiridion.getHelper().getEnchantmentSeed(player, index + Mth.floor(enchiridion$scrollOff) - 1));
                    else
                        random.setSeed(enchantmentSeed.get());

                    stack.enchant(enchantment, EnchantingTableUtil.potentiallyAddExtraLevel(random, levelClue[index], enchantment.value().getMaxLevel(), stack.getItem().getEnchantmentValue()));
                    player.onEnchantmentPerformed(stack, EnchantingTableUtil.getLapisCountForLevelling(levelClue[index], enchantment.value().getMaxLevel()));

                    lapis.consume(lapisCount, player);
                    if (lapis.isEmpty())
                        this.enchantSlots.setItem(1, ItemStack.EMPTY);

                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer serverPlayer)
                        CriteriaTriggers.ENCHANTED_ITEM.trigger(serverPlayer, stack, lapisCount);

                    enchantSlots.setChanged();
                    enchantmentSeed.set(player.getEnchantmentSeed());
                    this.slotsChanged(this.enchantSlots);

                    level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
                    this.broadcastChanges();

                    cir.setReturnValue(true);
                } else
                    cir.setReturnValue(false);
            });
        }
    }

    @Override
    public int enchiridion$getEnchantmentSize() {
        return enchiridion$levelableEnchantmentSize.get();
    }

    @Override
    public int enchiridion$getBookshelfCount() {
        return enchiridion$bookshelfCount.get();
    }

    @Override
    public int enchiridion$getRequiredBookshelves(int index) {
        return enchiridion$requiredBookshelves[index];
    }

    @Override
    public double enchiridion$getScrollOffset() {
        return enchiridion$scrollOff;
    }

    public void enchiridion$setScrollOffset(double value) {
        enchiridion$scrollOff = value;
    }

    @Override
    public void enchiridion$refreshEnchantmentIndexes() {
        int scrollOff = Mth.floor(enchiridion$scrollOff);
        for (int i = scrollOff; i < 3 + scrollOff; ++i) {
            this.costs[i - scrollOff] = enchiridion$allLevelUpCosts.get(i);
            this.enchantClue[i - scrollOff] = enchiridion$allEnchantClues.get(i);
            this.levelClue[i - scrollOff] = enchiridion$allLevelClues.get(i);
            enchiridion$requiredBookshelves[i - scrollOff] = enchiridion$allRequiredBookshelves.get(i);
        }
    }
}
