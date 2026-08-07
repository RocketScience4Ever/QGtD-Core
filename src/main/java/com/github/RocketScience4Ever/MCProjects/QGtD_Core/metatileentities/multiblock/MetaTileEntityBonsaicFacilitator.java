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
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public class MetaTileEntityBonsaicFacilitator extends EnhancedRecipeMapMultiblockController {
    private static final RecipeMap<SimpleRecipeBuilder> BONSAIC_FACILITATOR_RECIPES = new RecipeMap<>("bonsaic_facilitator",3,4,1,0,new SimpleRecipeBuilder(),false);

    /**Create a new {@link MetaTileEntityBonsaicFacilitator} using the specified {@code metaTileEntityId}.
     * @param metaTileEntityId (ResourceLocation) The meta tile entity id to use when creating this bonsaic facilitator
     */
    public MetaTileEntityBonsaicFacilitator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId,BONSAIC_FACILITATOR_RECIPES);
    }

    /**Create a new {@link MetaTileEntityBonsaicFacilitator} using the {@code metaTileEntityId} of this {@code MetaTileEntityBonsaicFacilitator}.
     * @param iGregTechTileEntity (IGregTechTileEntity) Unused, but required by the superclass of this object
     */
    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityBonsaicFacilitator(this.metaTileEntityId);
    }

    /**Defines the structure the user must build to construct this multiblock.
     * @return (BlockPattern) A {@link BlockPattern} representing the structure the user must build to form this multiblock
     */
    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle(" CCC "," CCC "," GGG "," GGG "," GGG ","     ")
                .aisle("CCCCC","CDDDC","G###G","G###G","G###G"," GGG ")
                .aisle("CCCCC","CDDDC","G###G","G###G","G###G"," GLG ")
                .aisle("CCCCC","CDDDC","G###G","G###G","G###G"," GGG ")
                .aisle(" CCC "," CSC "," GGG "," GGG "," GGG ","     ")
                .where('S',selfPredicate())
                .where('C',states(this.getCasingState())
                        .setMinGlobalLimited(26)
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.INPUT_ENERGY,1,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.IMPORT_ITEMS,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.EXPORT_ITEMS,1))
                        .or(EnhancedRecipeMapMultiblockController.getAbilityPredicate(MultiblockAbility.IMPORT_FLUIDS,1))
                        .or(this.maintenancePredicate())
                )
                .where('G',states(this.getGlassCasingState()))
                .where('D',states(Blocks.DIRT.getDefaultState(),Blocks.GRASS.getDefaultState()))
                .where('L',states(Blocks.REDSTONE_LAMP.getDefaultState()))
                .where('#',air())
                .build();
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.FERMENTER_OVERLAY;
    }

    /**Defines the texture used to make various I/O ports/hatches/etc match the rest of the multiblock casing.
     * @param iMultiblockPart (IMultiblockPart) Unused. All parts should take on the texture of solid steel machine casing
     * @return (ICubeRenderer) The {@link ICubeRenderer} for solid steel machine casing
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    protected IBlockState getGlassCasingState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
    }
}