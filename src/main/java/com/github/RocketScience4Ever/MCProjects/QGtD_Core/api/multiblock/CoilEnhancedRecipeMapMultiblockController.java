package com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.multiblock;

import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**A variant of an {@link EnhancedRecipeMapMultiblockController} which adds support for coil bonuses.
 * This class exists to prevent the duplication of logic shared by any multiblock which has the ability to receive bonuses from tiered GTCEu coils.
 */
public abstract class CoilEnhancedRecipeMapMultiblockController extends EnhancedRecipeMapMultiblockController {
    public static String[] coilMaterialNamesByTier = new String[] {
            "gregtech.material.cupronickel",
            "gregtech.material.kanthal",
            "gregtech.material.nichrome",
            "qgtd.material.rhtaptu",
            "gregtech.material.hssg",
            "gregtech.material.naquadah",
            "gregtech.material.trinium",
            "gregtech.material.tritanium"
    };

    private int coilTier = -1; //The tier of coils currently installed on this multiblock (0 = cupronickel, 1 = kanthal, 2 = nichrome, etc.)

    private final boolean hasEnergyCoilBonus; //true if this multiblock receives energy bonuses/penalties based on the tier of its GTCEu coils; false otherwise
    private int energyBonusBaseCoilTier = 1; //The coil tier at which this multiblock receives neither an energy penalty nor an energy bonus
    private double energyBonusPerTierUp = 0.1; //Recipe energy decreases by this percentage per tier above energyBonusBaseCoilTier (stacks additively)
    private double energyPenaltyPerTierDown = 0.25; //Recipe energy increases by this percentage per tier below energyBonusBaseCoilTier (stacks additively)
    private int energyPercentage = -1; //The percentage multiplier for the energy usage of this multiblock (dependent on this.coilTier)

    private final boolean hasDurationCoilBonus; //true if this multiblock receives duration bonuses/penalties based on the tier of its GTCEu coils; false otherwise
    private int durationBonusBaseCoilTier = 1; //The coil tier at which this multiblock receives neither a duration penalty nor a duration bonus
    private double durationBonusPerTierUp = 0.1; //Recipe duration decreases by this percentage per tier above durationBonusBaseCoilTier (stacks additively)
    private double durationPenaltyPerTierDown = 0.25; //Recipe duration increases by this percentage per tier below durationBonusBaseCoilTier (stacks additively)
    private int speedPercentage = -1; //The percentage multiplier for the speed of this multiblock (dependent on this.coilTier)

    /**Creates a new {@link CoilEnhancedRecipeMapMultiblockController} using the specified parameters.
     * @param metaTileEntityId (ResourceLocation) The unique id for this type of multiblock
     * @param recipeMap (RecipeMap) The {@link RecipeMap} which stores recipes for this type of multiblock
     * @param hasEnergyCoilBonus (boolean) {@code true} if bonuses and/or penalties to the energy consumption of recipes in this multiblock should be applied based on the tier of coils installed; {@code false} otherwise
     * @param hasDurationCoilBonus (boolean) {@code true} if bonuses and/or penalties to the duration of recipes in this multiblock should be applied based on the tier of coils installed; {@code false} otherwise
     */
    public CoilEnhancedRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, boolean hasEnergyCoilBonus, boolean hasDurationCoilBonus) {
        super(metaTileEntityId,recipeMap);
        this.recipeMapWorkable = new CoilEnhancedMultiblockWorkableHandler(this);

        this.hasEnergyCoilBonus = hasEnergyCoilBonus;
        this.hasDurationCoilBonus = hasDurationCoilBonus;
    }

    //Getters for coil bonuses/penalties
    protected int getCoilTier() {
        return this.coilTier;
    }

    protected boolean isHasEnergyCoilBonus() {
        return hasEnergyCoilBonus;
    }

    protected int getEnergyBonusBaseCoilTier() {
        return energyBonusBaseCoilTier;
    }

    protected double getEnergyBonusPerTierUp() {
        return energyBonusPerTierUp;
    }

    protected double getEnergyPenaltyPerTierDown() {
        return energyPenaltyPerTierDown;
    }

    protected boolean isHasDurationCoilBonus() {
        return hasDurationCoilBonus;
    }

    protected int getDurationBonusBaseCoilTier() {
        return durationBonusBaseCoilTier;
    }

    protected double getDurationBonusPerTierUp() {
        return durationBonusPerTierUp;
    }

    protected double getDurationPenaltyPerTierDown() {
        return durationPenaltyPerTierDown;
    }

    //Setters for coil bonuses/penalties
    protected void setEnergyBonusBaseCoilTier(int energyBonusBaseCoilTier) {
        this.energyBonusBaseCoilTier = Math.abs(energyBonusBaseCoilTier);
    }

    protected void setEnergyBonusPerTierUp(double energyBonusPerTierUp) {
        this.energyBonusPerTierUp = Math.abs(energyBonusPerTierUp);
    }

    protected void setEnergyPenaltyPerTierDown(double energyPenaltyPerTierDown) {
        this.energyPenaltyPerTierDown = Math.abs(energyPenaltyPerTierDown);
    }

    protected void setDurationBonusBaseCoilTier(int durationBonusBaseCoilTier) {
        this.durationBonusBaseCoilTier = Math.abs(durationBonusBaseCoilTier);
    }

    protected void setDurationBonusPerTierUp(double durationBonusPerTierUp) {
        this.durationBonusPerTierUp = Math.abs(durationBonusPerTierUp);
    }

    protected void setDurationPenaltyPerTierDown(double durationPenaltyPerTierDown) {
        this.durationPenaltyPerTierDown = Math.abs(durationPenaltyPerTierDown);
    }

    //Coil bonus implementation
    /**Handles any logic which needs to occur when a multiblock is formed.
     * <p>This override calls the superclass method, then applies coil bonuses when the multiblock is formed.</p>
     * @param context (PatternMatchContext) Used to get information about specific block types (ie coils) in the multiblock
     */
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);

        Object coilType = context.get("CoilType"); //Get the type of coils on the multiblock
        if (coilType instanceof IHeatingCoilBlockStats) {
            this.coilTier = ((IHeatingCoilBlockStats)coilType).getTier(); //Set the coil tier according to the coils in the multiblock
        } else {
            this.coilTier = 0; //Apply cupronickel if coil tier not found
        }

        this.updateCoilBonuses();
    }

    /**Handles any logic which needs to occur when a multiblock is invalidated.
     * <p>This override calls the superclass method, then invalidates the necessary fields to deactivate coil bonuses for an invalid multiblock.</p>
     */
    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.coilTier = -1; //Invalidate coils when structure is invalidated
        this.energyPercentage = -1; //Invalidate energy multiplier when structure is invalidated
        this.speedPercentage = -1; //Invalidate speed multiplier when structure is invalidated
    }

    /**Updates the coil bonus percentages in the multiblock GUI to match the currently installed coils.
     * This method is called each time the multiblock forms in case the player changed the coils while the multiblock was invalid.
     */
    private void updateCoilBonuses() {
        if (getCoilTier() >= 0) {
            //Update energy coil bonuses
            if (this.hasEnergyCoilBonus) {
                if (this.coilTier < this.energyBonusBaseCoilTier) {
                    this.energyPercentage = (int)Math.max(1.0,Math.ceil(100 * (1.0 + this.energyPenaltyPerTierDown * (this.energyBonusBaseCoilTier - this.coilTier)))); //Apply energy penalty for low tier coils
                } else if (this.coilTier > this.energyBonusBaseCoilTier) {
                    this.energyPercentage = (int)Math.max(1.0,Math.ceil(100 * (1.0 - this.energyBonusPerTierUp * (this.coilTier - this.energyBonusBaseCoilTier)))); //Apply energy bonus for high tier coils
                }
            }

            //Update duration coil bonuses
            if (this.hasDurationCoilBonus) {
                if (this.coilTier < this.durationBonusBaseCoilTier) {
                    this.speedPercentage = (int) Math.max(1.0,Math.ceil(100 * (1.0 - this.durationPenaltyPerTierDown * (this.durationBonusBaseCoilTier - this.coilTier)))); //Apply duration penalty for low tier coils
                } else if (coilTier > this.durationBonusBaseCoilTier) {
                    this.speedPercentage = (int) Math.max(1.0,Math.ceil(100 * (1.0 + this.durationBonusPerTierUp * (this.coilTier - this.durationBonusBaseCoilTier)))); //Apply duration bonus for high tier coils
                }
            }
        }
    }

    /**Adds the text to the multiblock GUI which appears when you right-click the controller of a GTCEu multiblock.
     * <p>This method needs to be overridden here to add the information about active coil bonuses to the existing multiblock GUI.</p>
     * @param textList (List&lt;ITextComponent&gt;) A list of {@link ITextComponent} objects which comprises the text in the multiblock GUI
     */
    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList,this.isStructureFormed())
                .setWorkingStatus(this.recipeMapWorkable.isWorkingEnabled(),this.recipeMapWorkable.isActive())
                .addEnergyUsageLine(this.recipeMapWorkable.getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(this.recipeMapWorkable.getMaxVoltage()))
                .addCustom((tl) -> {
                    //Energy coil bonus tooltip in multiblock GUI
                    if (this.isStructureFormed() && this.hasEnergyCoilBonus) {
                        ITextComponent energyBonus = TextComponentUtil.stringWithColor(this.getEnergyColor(),this.energyPercentage + "%");
                        ITextComponent base = TextComponentUtil.translationWithColor(TextFormatting.GRAY,"qgtd.multiblock.enhancement.coil_bonus_energy",energyBonus);
                        ITextComponent hover = TextComponentUtil.translationWithColor(TextFormatting.GRAY,"qgtd.multiblock.enhancement.coil_bonus_energy_hover");
                        tl.add(TextComponentUtil.setHover(base,hover));
                    }
                }).addCustom((tl) -> {
                    //Duration coil bonus tooltip in multiblock GUI
                    if (this.isStructureFormed() && this.hasDurationCoilBonus) {
                        ITextComponent durationBonus = TextComponentUtil.stringWithColor(this.getSpeedColor(),this.speedPercentage + "%");
                        ITextComponent base = TextComponentUtil.translationWithColor(TextFormatting.GRAY,"qgtd.multiblock.enhancement.coil_bonus_speed",durationBonus);
                        ITextComponent hover = TextComponentUtil.translationWithColor(TextFormatting.GRAY,"qgtd.multiblock.enhancement.coil_bonus_speed_hover");
                        tl.add(TextComponentUtil.setHover(base,hover));
                    }
                }).addParallelsLine(this.recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(this.recipeMapWorkable.getProgressPercent());
    }

    /**Add coil bonus information to the tooltip for any multiblock controller which extends {@link CoilEnhancedRecipeMapMultiblockController}.
     * @param stack (ItemStack) An {@link ItemStack} representing the item to add this tooltip to
     * @param player (World) Unused, but required to pass to the superclass method
     * @param tooltip (List&lt;String&gt;) A list of all lines of text to add to the tooltip for the item represented by {@code stack}
     * @param advanced (boolean) {@code true} if this tooltip is currently showing information from {@code F3 + H}, {@code false} otherwise
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        //Energy coil bonus tooltip in JEI
        if (this.hasEnergyCoilBonus) {
            tooltip.add(I18n.format("qgtd.multiblock.enhancement.coil_bonus_energy_tooltip",this.getCoilMaterialNameByTier(this.energyBonusBaseCoilTier),100 * this.energyPenaltyPerTierDown,this.getCoilMaterialNameByTier(this.energyBonusBaseCoilTier),100 * this.energyBonusPerTierUp)); //Add tooltip for energy bonus
        }

        //Duration coil bonus tooltip in JEI
        if (this.hasDurationCoilBonus) {
            tooltip.add(I18n.format("qgtd.multiblock.enhancement.coil_bonus_speed_tooltip",this.getCoilMaterialNameByTier(this.durationBonusBaseCoilTier),100 * this.durationPenaltyPerTierDown,this.getCoilMaterialNameByTier(this.durationBonusBaseCoilTier),100 * this.durationBonusPerTierUp)); //Add tooltip for duration bonus
        }
    }

    /**Determines the color for the energy percentage from coil bonuses in the multiblock GUI based on the value of {@code this.energyPercentage}.
     * @return (TextFormatting) An enum constant for the appropriate color in which to display the energy multiplier for this multiblock based on currently active coil bonuses
     */
    private TextFormatting getEnergyColor() {
        if (this.energyPercentage > 100) {
            return TextFormatting.RED;
        } else if (this.energyPercentage == 100) {
            return TextFormatting.GRAY;
        } else {
            return this.energyPercentage >= 70 ? TextFormatting.GREEN : TextFormatting.LIGHT_PURPLE;
        }
    }

    /**Determines the color for the speed percentage from coil bonuses in the multiblock GUI based on the value of {@code this.speedPercentage}.
     * @return (TextFormatting) An enum constant for the appropriate color in which to display the speed multiplier for this multiblock based on currently active coil bonuses
     */
    private TextFormatting getSpeedColor() {
        if (this.speedPercentage < 100) {
            return TextFormatting.RED;
        } else if (this.speedPercentage == 100) {
            return TextFormatting.GRAY;
        } else {
            return this.speedPercentage < 250 ? TextFormatting.GREEN : TextFormatting.LIGHT_PURPLE;
        }
    }

    public String getCoilMaterialNameByTier(int tier) {
        return I18n.format(CoilEnhancedRecipeMapMultiblockController.coilMaterialNamesByTier[tier]); //Format according to the translation keys for the coil materials
    }

    /**A variation of a {@link MultiblockRecipeLogic} which applies the coil bonuses from a {@link CoilEnhancedRecipeMapMultiblockController} in {@link AbstractRecipeLogic#modifyOverclockPost(int[],IRecipePropertyStorage)}.
     */
    private static class CoilEnhancedMultiblockWorkableHandler extends MultiblockRecipeLogic {
        /**This constructor only exists to wrap the superclass constructor {@link MultiblockRecipeLogic#MultiblockRecipeLogic(RecipeMapMultiblockController)}.
         * @param tileEntity (RecipeMapMultiblockController) The {@link CoilEnhancedRecipeMapMultiblockController} to which this {@link CoilEnhancedMultiblockWorkableHandler} is attached.
         */
        public CoilEnhancedMultiblockWorkableHandler(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        /**Apply coil bonuses from this multiblock to the recipe energy usage and duration after main overclock and maintenance effects are applied.
         * @param overclockResults (int[]) An array containing the EU/t of this recipe at index 0 and the duration of the recipe in ticks at index 1
         * @param storage (IRecipePropertyStorage) Unused for this method, but must be present to pass to the superclass implementation of {@code modifyOverclockPost}
         */
        @Override
        protected void modifyOverclockPost(int[] overclockResults, @NotNull IRecipePropertyStorage storage) {
            super.modifyOverclockPost(overclockResults,storage);

            CoilEnhancedRecipeMapMultiblockController multiblock = (CoilEnhancedRecipeMapMultiblockController)this.metaTileEntity; //Duck type this.metaTileEntity to allow access to coil bonus information

            if (multiblock.getCoilTier() >= 0) {
                //Apply energy coil bonuses
                if (multiblock.isHasEnergyCoilBonus()) {
                    if (multiblock.getCoilTier() < multiblock.getEnergyBonusBaseCoilTier()) {
                        overclockResults[0] = (int) Math.max(1.0,Math.ceil(overclockResults[0] * (1.0 + multiblock.getEnergyPenaltyPerTierDown() * (multiblock.getEnergyBonusBaseCoilTier() - multiblock.getCoilTier())))); //Apply energy penalty for low tier coils
                    } else if (multiblock.getCoilTier() > multiblock.getEnergyBonusBaseCoilTier()) {
                        overclockResults[0] = (int) Math.max(1.0,Math.ceil(overclockResults[0] * (1.0 - multiblock.getEnergyBonusPerTierUp() * (multiblock.getCoilTier() - multiblock.getEnergyBonusBaseCoilTier())))); //Apply energy bonus for high tier coils
                    }
                }

                //Apply duration coil bonuses
                if (multiblock.isHasDurationCoilBonus()) {
                    if (multiblock.getCoilTier() < multiblock.getDurationBonusBaseCoilTier()) {
                        overclockResults[1] = (int) Math.max(1.0,Math.ceil(overclockResults[1] * (1.0 + multiblock.getDurationPenaltyPerTierDown() * (multiblock.getDurationBonusBaseCoilTier() - multiblock.getCoilTier())))); //Apply duration penalty for low tier coils
                    } else if (multiblock.getCoilTier() > multiblock.getDurationBonusBaseCoilTier()) {
                        overclockResults[1] = (int) Math.max(1.0,Math.ceil(overclockResults[1] / (1.0 + multiblock.getDurationBonusPerTierUp() * (multiblock.getCoilTier() - multiblock.getDurationBonusBaseCoilTier())))); //Apply duration bonus for high tier coils
                    }
                }
            }
        }
    }
}