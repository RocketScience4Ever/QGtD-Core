package com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification;

import gregtech.api.GTValues;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class QGtDRecipes {
    public static void createNewMaterialRecipes() {
        RecipeMaps.MIXER_RECIPES.recipeBuilder() //RuTaPtU3 mixture recipe in EV mixer
                .input(OrePrefix.dust,Materials.Ruthenium,1)
                .input(OrePrefix.dust,Materials.Tantalum,1)
                .input(OrePrefix.dust,Materials.Platinum,1)
                .input(OrePrefix.dust,Materials.Uranium238,3)
                .output(OrePrefix.dust,QGtDMaterials.RuTaPtU3Alloy,6)
                .EUt(GTValues.VA[GTValues.EV])
                .duration(300) //15 seconds
                .buildAndRegister();

        RecipeMaps.BLAST_RECIPES.recipeBuilder() //B4C in EV EBF (double HV hatches and kanthal coils)
                .input(OrePrefix.dust,Materials.Boron,1)
                .input(OrePrefix.dust,Materials.Carbon,7)
                .fluidInputs(Materials.Oxygen.getFluid(6000))
                .output(OrePrefix.ingotHot,QGtDMaterials.BoronCarbide,1)
                .fluidOutputs(Materials.CarbonMonoxide.getFluid(6000))
                .EUt(GTValues.VA[GTValues.EV])
                .duration(1200) //60 seconds
                .blastFurnaceTemp(2700)
                .buildAndRegister();

        RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                .input(OrePrefix.ingotHot,QGtDMaterials.BoronCarbide,1)
                .output(OrePrefix.ingot,QGtDMaterials.BoronCarbide,1)
                .EUt(GTValues.VA[3])
                .duration(168) //8.4 seconds
                .buildAndRegister();

        RecipeMaps.MIXER_RECIPES.recipeBuilder() //CoV mixture recipe in HV mixer
                .input(OrePrefix.dust,Materials.Cobalt,1)
                .input(OrePrefix.dust,Materials.Vanadium,1)
                .output(OrePrefix.dust,QGtDMaterials.CobaltVanadiumMixture,2)
                .EUt(GTValues.VA[GTValues.HV])
                .duration(300) //15 seconds
                .buildAndRegister();

        RecipeMaps.BLAST_RECIPES.recipeBuilder() //CoVO4 in EV EBF (double HV hatches and kanthal coils)
                .input(OrePrefix.dust,QGtDMaterials.CobaltVanadiumMixture,1)
                .fluidInputs(Materials.Oxygen.getFluid(4000))
                .output(OrePrefix.ingotHot,QGtDMaterials.CobaltOrthovanadate,1)
                .EUt(GTValues.VA[GTValues.EV])
                .duration(1300) //65 seconds
                .blastFurnaceTemp(2400)
                .buildAndRegister();
    }

    public static void alterMaterialRecipes() {
        //MAKE SURE TO INCLUDE THE CIRCUIT ITEM TO FIND RECIPES WITH PROGRAMMED CIRCUITS
        Recipe activeRecipe; //Recipe object to allow modifications to existing recipes

        activeRecipe = QGtDRecipes.removeRecipeFromMachine(RecipeMaps.POLARIZER_RECIPES,new ItemStack[]{OreDictUnifier.get(OrePrefix.ingot,QGtDMaterials.CobaltOrthovanadate)});
        (new SimpleRecipeBuilder(activeRecipe,RecipeMaps.POLARIZER_RECIPES)).EUt(GTValues.VA[GTValues.HV]).buildAndRegister(); //Polarize CoVO4 ingot at HV+

        activeRecipe = QGtDRecipes.removeRecipeFromMachine(RecipeMaps.POLARIZER_RECIPES,new ItemStack[]{OreDictUnifier.get(OrePrefix.stick,QGtDMaterials.CobaltOrthovanadate)});
        (new SimpleRecipeBuilder(activeRecipe,RecipeMaps.POLARIZER_RECIPES)).EUt(GTValues.VA[GTValues.HV]).buildAndRegister(); //Polarize CoVO4 rod at HV+

        activeRecipe = QGtDRecipes.removeRecipeFromMachine(RecipeMaps.POLARIZER_RECIPES,new ItemStack[]{OreDictUnifier.get(OrePrefix.stickLong,QGtDMaterials.CobaltOrthovanadate)});
        (new SimpleRecipeBuilder(activeRecipe,RecipeMaps.POLARIZER_RECIPES)).EUt(GTValues.VA[GTValues.HV]).buildAndRegister(); //Polarize CoVO4 long rod at HV+

        activeRecipe = QGtDRecipes.removeRecipeFromMachine(RecipeMaps.VACUUM_RECIPES,new ItemStack[]{OreDictUnifier.get(OrePrefix.ingotHot,QGtDMaterials.RuTaPtU3Alloy)});
        (new SimpleRecipeBuilder(activeRecipe,RecipeMaps.VACUUM_RECIPES)).duration(250).buildAndRegister(); //change duration of RuTaPtU3 vacuum freeze to 12.5 seconds to match RTM from vanilla GTCEu
    }

    @Nullable
    public static <B extends RecipeBuilder<B>> Recipe removeRecipeFromMachine(@NotNull RecipeMap<B> machineRecipeMap, ItemStack[] itemInputs) {
        return QGtDRecipes.removeRecipeFromMachine(machineRecipeMap,itemInputs,new FluidStack[0]);
    }

    @Nullable
    public static <B extends RecipeBuilder<B>> Recipe removeRecipeFromMachine(@NotNull RecipeMap<B> machineRecipeMap, FluidStack[] fluidInputs) {
        return QGtDRecipes.removeRecipeFromMachine(machineRecipeMap,new ItemStack[0],fluidInputs);
    }

    @Nullable
    public static <B extends RecipeBuilder<B>> Recipe removeRecipeFromMachine(@NotNull RecipeMap<B> machineRecipeMap, ItemStack[] itemInputs, FluidStack[] fluidInputs) {
        ArrayList<ItemStack> itemsInList = new ArrayList<>();
        if (itemInputs != null) {
            Collections.addAll(itemsInList, itemInputs);
        }

        ArrayList<FluidStack> fluidsInList = new ArrayList<>();
        if (fluidInputs != null) {
            Collections.addAll(fluidsInList, fluidInputs);
        }

        Set<Recipe> recipes = machineRecipeMap.findRecipeCollisions(itemsInList,fluidsInList);
        if (recipes != null && !recipes.isEmpty()) {
            Recipe r = recipes.iterator().next();
            machineRecipeMap.removeRecipe(r);
            return r;
        }

        return null;
    }

    @Nullable
    public static <B extends RecipeBuilder<B>> Recipe removeRecipeFromMachine(@NotNull RecipeMap<B> machineRecipeMap, ItemStack[] itemInputs, @NotNull Predicate<Recipe> selectionPredicate) {
        return QGtDRecipes.removeRecipeFromMachine(machineRecipeMap,itemInputs,new FluidStack[0],selectionPredicate);
    }

    @Nullable
    public static <B extends RecipeBuilder<B>> Recipe removeRecipeFromMachine(@NotNull RecipeMap<B> machineRecipeMap, FluidStack[] fluidInputs, @NotNull Predicate<Recipe> selectionPredicate) {
        return QGtDRecipes.removeRecipeFromMachine(machineRecipeMap,new ItemStack[0],fluidInputs,selectionPredicate);
    }

    @Nullable
    public static <B extends RecipeBuilder<B>> Recipe removeRecipeFromMachine(@NotNull RecipeMap<B> machineRecipeMap, @Nullable ItemStack[] itemInputs, @Nullable FluidStack[] fluidInputs, @NotNull Predicate<Recipe> selectionPredicate) {
        ArrayList<ItemStack> itemsInList = new ArrayList<>();
        if (itemInputs != null) {
            Collections.addAll(itemsInList, itemInputs);
        }

        ArrayList<FluidStack> fluidsInList = new ArrayList<>();
        if (fluidInputs != null) {
            Collections.addAll(fluidsInList, fluidInputs);
        }

        Set<Recipe> recipes = machineRecipeMap.findRecipeCollisions(itemsInList,fluidsInList);
        if (recipes != null && !recipes.isEmpty()) {
            for (Recipe r : recipes) {
                if (selectionPredicate.test(r)) {
                    machineRecipeMap.removeRecipe(r);
                    return r;
                }
            }
        }

        return null;
    }

    /**Calculates whether the EU/t of the specified {@code recipe} is within the range of the voltage specified by the provided {@code voltageIndex}.
     * @param recipe (Recipe) The {@link Recipe} to check for the voltage tier specified by {@code voltageIndex}
     * @param voltageIndex (@MagicConstant int) An integer representing the number of the voltage tier to check for (0 = ULV, 1 = LV, 2 = MV, etc.)
     * @return (boolean) {@code true} if the EU/t of {@code recipe} is greater than the EU/t for the voltage {@code voltageIndex - 1} AND less than or equal to the EU/t for the voltage {@code voltageIndex}
     */
    public static boolean atVoltage(@NotNull Recipe recipe, @MagicConstant(flags = {GTValues.ULV, GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV, GTValues.IV, GTValues.LuV, GTValues.ZPM, GTValues.UV, GTValues.UHV, GTValues.UEV, GTValues.UIV, GTValues.UXV, GTValues.OpV, GTValues.MAX}) int voltageIndex) {
        return recipe.getEUt() <= GTValues.V[voltageIndex] && recipe.getEUt() > GTValues.VH[Math.max(0,voltageIndex - 1)];
    }

    /**Returns {@link QGtDRecipes#atVoltage(Recipe, int)} as a {@link Predicate} using the specified {@code voltageIndex}.
     * @param voltageIndex (@MagicConstant int) An integer representing the number of the voltage tier to check for (0 = ULV, 1 = LV, 2 = MV, etc.)
     * @return (Predicate&lt;Recipe&gt;) A {@code Predicate}, which returns {@code true} if the EU/t of a {@link Recipe} is greater than the EU/t for the voltage {@code voltageIndex - 1} AND less than or equal to the EU/t for the voltage {@code voltageIndex} and {@code false} otherwise
     */
    @NotNull
    public static Predicate<Recipe> atVoltage(@MagicConstant(flags = {GTValues.ULV, GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV, GTValues.IV, GTValues.LuV, GTValues.ZPM, GTValues.UV, GTValues.UHV, GTValues.UEV, GTValues.UIV, GTValues.UXV, GTValues.OpV, GTValues.MAX}) int voltageIndex) {
        return (Recipe r) -> QGtDRecipes.atVoltage(r,voltageIndex);
    }
}