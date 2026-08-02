package com.github.RocketScience4Ever.MCProjects.QGtD_Core.metatileentities.multiblock;

import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.multiblock.CoilEnhancedRecipeMapMultiblockController;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.multiblock.EnhancedRecipeMapMultiblockController;

import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.MetaBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MetaTileEntityBecquerelicAccelerator extends CoilEnhancedRecipeMapMultiblockController {
    private static final RecipeMap<SimpleRecipeBuilder> BECQUERELIC_ACCELERATOR_RECIPES = new RecipeMap<>("becquerelic_accelerator",1,1,1,1,new SimpleRecipeBuilder(),false);

    public MetaTileEntityBecquerelicAccelerator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId,BECQUERELIC_ACCELERATOR_RECIPES,false,true);
        this.setDurationBonusBaseCoilTier(2); //Nichrome coils = 100% speed
        this.setDurationPenaltyPerTierDown(0.25); //25% speed penalty per coil below nichrome (applied after overclock and maintenance)
        this.setDurationBonusPerTierUp(0.20); //20% speed bonus per coil above nichrome (applied after overclock and maintenance)
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityBecquerelicAccelerator(this.metaTileEntityId);
    }

    @Override
    @NotNull
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("RRR","RRR","RRR")
                .aisle("RRR","RPR","RRR")
                .aisle("CCC","CIC","CCC")
                .aisle("RRR","RPR","RRR")
                .aisle("RRR","RSR","RRR")
                .where('S',selfPredicate())
                .where('R',states(this.getCasingState())
                        .setMinGlobalLimited(26)
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.INPUT_ENERGY,1,2,2))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.IMPORT_ITEMS,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.EXPORT_ITEMS,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.IMPORT_FLUIDS,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.EXPORT_FLUIDS,1))
                        .or(this.maintenancePredicate())
                )
                .where('C',heatingCoils())
                .where('P',states(MetaBlocks.COMPRESSED.get(Materials.Platinum).getDefaultState()))
                .where('I',states(this.getPipeCasingState()))
                .where('#',air())
                .build();
    }

    @Override
    protected IBlockState getCasingState() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.ATOMIC_CASING);
    }

    @Override
    protected IBlockState getPipeCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TITANIUM_PIPE);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GCYMTextures.ATOMIC_CASING;
    }
}