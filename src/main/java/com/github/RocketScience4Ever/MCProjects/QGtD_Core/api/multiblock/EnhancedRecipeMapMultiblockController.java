package com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.multiblock;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;

import gregtech.core.sound.GTSoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.NotNull;

public abstract class EnhancedRecipeMapMultiblockController extends RecipeMapMultiblockController {
    public EnhancedRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        super(metaTileEntityId, recipeMap);
    }

    protected IBlockState getCasingState() {
        return null;
    }

    protected IBlockState getGlassCasingState() {
        return null;
    }

    protected IBlockState getPipeCasingState() {
        return null;
    }

    protected IBlockState getFireboxCasingState() {
        return null;
    }

    protected IBlockState getGearboxCasingState() {
        return null;
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    /**Returns a {@link TraceabilityPredicate} for the specified {@link MultiblockAbility}.
     * @param ability (MultiblockAbility) The {@code MultiblockAbility} for which to generate a {@code TraceabilityPredicate}
     * @return (TraceabilityPredicate) A {@code TraceabilityPredicate} for the specified {@code ability} with no specifications for minimum/maximum/preview amount
     */
    @NotNull
    protected static TraceabilityPredicate getAbilityPredicate(@NotNull MultiblockAbility<?> ability) {
        return abilities(ability);
    }

    /**Returns a {@link TraceabilityPredicate} for the specified {@link MultiblockAbility}.
     * <p>This method also sets the number of hatches with the specified {@code ability} that should show up in the JEI preview.</p>
     * @param ability (MultiblockAbility) The {@code MultiblockAbility} for which to generate a {@code TraceabilityPredicate}
     * @param previewHatches (int) The number of hatches with this ability that should be present on the multiblock preview in JEI
     * @return (TraceabilityPredicate) A {@code TraceabilityPredicate} for the specified {@code ability} with details specified by the parameters of this method
     */
    @NotNull
    protected static TraceabilityPredicate getAbilityPredicate(@NotNull MultiblockAbility<?> ability, int previewHatches) {
        return abilities(ability).setPreviewCount(previewHatches);
    }

    /**Returns a {@link TraceabilityPredicate} for the specified {@link MultiblockAbility}.
     * <p>This method also sets the minimum and maximum number of hatches that can have the specified {@code ability}.</p>
     * @param ability (MultiblockAbility) The {@code MultiblockAbility} for which to generate a {@code TraceabilityPredicate}
     * @param minHatches (int) The minimum number of hatches with this ability that should be present on the multiblock
     * @param maxHatches (int) The maximum number of hatches with this ability that should be present on the multiblock
     * @return (TraceabilityPredicate) A {@code TraceabilityPredicate} for the specified {@code ability} with details specified by the parameters of this method
     */
    @NotNull
    protected static TraceabilityPredicate getAbilityPredicate(@NotNull MultiblockAbility<?> ability, int minHatches, int maxHatches) {
        return abilities(ability).setMinGlobalLimited(minHatches).setMaxGlobalLimited(maxHatches).setPreviewCount(1);
    }

    /**Returns a {@link TraceabilityPredicate} for the specified {@link MultiblockAbility}.
     * <p>This method also sets the minimum and maximum number of hatches that can have the specified {@code ability}.</p>
     * <p>This method also sets the number of hatches with the specified {@code ability} that should show up in the JEI preview.</p>
     * @param ability (MultiblockAbility) The {@code MultiblockAbility} for which to generate a {@code TraceabilityPredicate}
     * @param minHatches (int) The minimum number of hatches with this ability that should be present on the multiblock
     * @param maxHatches (int) The maximum number of hatches with this ability that should be present on the multiblock
     * @param previewHatches (int) The number of hatches with this ability that should be present on the multiblock preview in JEI
     * @return (TraceabilityPredicate) A {@code TraceabilityPredicate} for the specified {@code ability} with details specified by the parameters of this method
     */
    @NotNull
    protected static TraceabilityPredicate getAbilityPredicate(@NotNull MultiblockAbility<?> ability, int minHatches, int maxHatches, int previewHatches) {
        return abilities(ability).setMinGlobalLimited(minHatches).setMaxGlobalLimited(maxHatches).setPreviewCount(previewHatches);
    }
}
