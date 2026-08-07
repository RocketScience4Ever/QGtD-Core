package com.github.RocketScience4Ever.MCProjects.QGtD_Core.common.blocks.coils;

import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDMaterials;

import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.block.VariantActiveBlock;
import gregtech.api.block.VariantItemBlock;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityMultiSmelter;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QGtDWireCoil extends VariantActiveBlock<QGtDWireCoil.QGtDCoilType> {
    /**Sets the block information for an EBF coil. Mimics the constructor in {@link BlockWireCoil gregtech.common.blocks.BlockWireCoil}.
     */
    public QGtDWireCoil() {
        super(net.minecraft.block.material.Material.IRON);
        this.setTranslationKey("wire_coil");
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("wrench", 2);
        this.setDefaultState(this.getState(QGtDCoilType.RUTAPTU));
    }

    public @NotNull BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    /**Adds the tooltips for QGtD EBF coils.
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack itemStack, @Nullable World worldIn, @NotNull List<String> lines, @NotNull ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, worldIn, lines, tooltipFlag);
        VariantItemBlock itemBlock = (VariantItemBlock)itemStack.getItem();
        IBlockState stackState = itemBlock.getBlockState(itemStack);
        QGtDCoilType coilType = this.getState(stackState);
        lines.add(I18n.format("tile.wire_coil.tooltip_heat",coilType.coilTemperature));
        if (TooltipHelper.isShiftDown()) {
            int coilTier = coilType.getTier();
            lines.add(I18n.format("tile.wire_coil.tooltip_smelter"));
            lines.add(I18n.format("tile.wire_coil.tooltip_parallel_smelter",coilType.level * 32));
            int EUt = MetaTileEntityMultiSmelter.getEUtForParallel(MetaTileEntityMultiSmelter.getMaxParallel(coilType.getLevel()),coilType.getEnergyDiscount());
            lines.add(I18n.format("tile.wire_coil.tooltip_energy_smelter",EUt));
            lines.add(I18n.format("tile.wire_coil.tooltip_pyro"));
            lines.add(I18n.format("tile.wire_coil.tooltip_speed_pyro",coilTier == 0 ? 75 : 50 * (coilTier + 1)));
            lines.add(I18n.format("tile.wire_coil.tooltip_cracking"));
            lines.add(I18n.format("tile.wire_coil.tooltip_energy_cracking",100 - 10 * coilTier));
        } else {
            lines.add(I18n.format("tile.wire_coil.tooltip_extended_info"));
        }

    }

    /**Mobs cannot spawn on EBF coils. Always returns {@code false}, regardless of the arguments passed to this method.
     * @return (boolean) {@code false}
     */
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull EntityLiving.@NotNull SpawnPlacementType type) {
        return false;
    }

    /**Determines whether the bloom effect for active coils is enabled in the GTCEu config.
     * @return (boolean) {@code true} if coil bloom is enabled in the GTCEu config; {@code false} otherwise
     */
    protected boolean isBloomEnabled(QGtDCoilType value) {
        return ConfigHolder.client.coilsActiveEmissiveTextures;
    }

    /**Enum to store EBF coil types added by QGtD. Each entry contains the necessary stats to register an EBF coil to {@code GregTechAPI.HEATING_COILS}.
     */
    public enum QGtDCoilType implements IStringSerializable, IHeatingCoilBlockStats {
        RUTAPTU("rutaptu_alloy",4500,3,4,2,QGtDMaterials.RuTaPtUAlloy);

        private final String name;
        private final int coilTemperature;
        private final int tier;
        private final int level;
        private final int energyDiscount;
        private final gregtech.api.unification.material.Material material;

        QGtDCoilType(String name, int coilTemperature, int tier, int level, int energyDiscount, gregtech.api.unification.material.Material material) {
            this.name = name;
            this.coilTemperature = coilTemperature;
            this.tier = tier;
            this.level = level;
            this.energyDiscount = energyDiscount;
            this.material = material;
        }

        public @NotNull String getName() {
            return this.name;
        }

        public int getCoilTemperature() {
            return this.coilTemperature;
        }

        public int getLevel() {
            return this.level;
        }

        public int getEnergyDiscount() {
            return this.energyDiscount;
        }

        public int getTier() {
            return this.tier;
        }

        @Nullable
        public gregtech.api.unification.material.Material getMaterial() {
            return this.material;
        }

        public @NotNull String toString() {
            return this.getName();
        }
    }
}