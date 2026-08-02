package com.github.RocketScience4Ever.MCProjects.QGtD_Core.metatileentities.multiblock;

import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.multiblock.EnhancedRecipeMapMultiblockController;

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
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockTurbineCasing;
import gregtech.common.blocks.MetaBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

public class MetaTileEntityCelestialArtifactory extends EnhancedRecipeMapMultiblockController {
    private static final RecipeMap<SimpleRecipeBuilder> CELESTIAL_ARTIFACTORY_RECIPES = new RecipeMap<>("celestial_artifactory",9,1,1,0,new SimpleRecipeBuilder(),false);

    public MetaTileEntityCelestialArtifactory(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId,CELESTIAL_ARTIFACTORY_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityCelestialArtifactory(this.metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(" RRR "," ### "," ### "," AAA "," ### ","     ")
                .aisle("RRGRR","#F#F#","#AAA#","A###A","##U##"," ### ")
                .aisle("RGGGR","##G##","#AAA#","A###A","#U#U#"," #X# ")
                .aisle("RRGRR","#F#F#","#AAA#","A###A","##U##"," ### ")
                .aisle(" RSR "," ### "," ### "," AAA "," ### ","     ")
                .where('S',selfPredicate())
                .where('R',states(this.getCasingState())
                        .setMinGlobalLimited(10)
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.INPUT_ENERGY,1,1,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.IMPORT_ITEMS,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.EXPORT_ITEMS,1))
                        .or(this.maintenancePredicate())
                )
                .where('G',states(this.getGearboxCasingState()))
                .where('F',frames(Materials.TungstenSteel))
                .where('U',frames(Materials.Aluminium))
                .where('A',states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF)))
                .where('X',states(Blocks.REDSTONE_BLOCK.getDefaultState()))
                .where('#',air())
                .build();
    }

    @Override
    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST);
    }

    @Override
    protected IBlockState getGearboxCasingState() {
        return MetaBlocks.TURBINE_CASING.getState(BlockTurbineCasing.TurbineCasingType.TUNGSTENSTEEL_GEARBOX);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.ROBUST_TUNGSTENSTEEL_CASING;
    }
}