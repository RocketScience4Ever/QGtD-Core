package com.github.RocketScience4Ever.MCProjects.QGtD_Core.metatileentities.multiblock;

import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.multiblock.CoilEnhancedRecipeMapMultiblockController;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.multiblock.EnhancedRecipeMapMultiblockController;

import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.api.unification.GCYMMaterials;
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
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.MetaBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class MetaTileEntityLaserEnrichmentChamber extends CoilEnhancedRecipeMapMultiblockController {
    private static final RecipeMap<SimpleRecipeBuilder> LASER_ENRICHMENT_CHAMBER_RECIPES = new RecipeMap<>("laser_enrichment_chamber",0,0,2,8,new SimpleRecipeBuilder(),false);

    public MetaTileEntityLaserEnrichmentChamber(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId,LASER_ENRICHMENT_CHAMBER_RECIPES,false,true);
        this.setDurationBonusBaseCoilTier(2);
        this.setDurationPenaltyPerTierDown(0.25);
        this.setDurationBonusPerTierUp(0.5);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityLaserEnrichmentChamber(this.metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(" RRR "," CCC "," CCC "," RRR ","     ")
                .aisle("RRRRR","CPPPC","CPPPC","RFLFR"," RRR ")
                .aisle("RRGRR","CPGPC","CPGPC","RLGLR"," RGR ")
                .aisle("RRRRR","CPPPC","CPPPC","RFLFR"," RRR ")
                .aisle(" RSR "," CCC "," CCC "," RRR ","     ")
                .where('S',selfPredicate())
                .where('R',states(this.getCasingState())
                        .setMinGlobalLimited(30)
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.INPUT_ENERGY,1,2,2))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.IMPORT_FLUIDS,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.EXPORT_FLUIDS,1))
                        .or(this.maintenancePredicate())
                )
                .where('C',heatingCoils())
                .where('L',states(this.getGlassCasingState()))
                .where('G',states(this.getGearboxCasingState()))
                .where('P',states(this.getPipeCasingState()))
                .where('F',frames(GCYMMaterials.TitaniumCarbide))
                .where('#',air())
                .build();
    }

    @Override
    protected IBlockState getCasingState() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.ATOMIC_CASING);
    }

    @Override
    protected IBlockState getGlassCasingState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.LAMINATED_GLASS);
    }

    @Override
    protected IBlockState getPipeCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TITANIUM_PIPE);
    }

    @Override
    protected IBlockState getGearboxCasingState() {
        return MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.TITANIUM_GEARBOX);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GCYMTextures.ATOMIC_CASING;
    }
}