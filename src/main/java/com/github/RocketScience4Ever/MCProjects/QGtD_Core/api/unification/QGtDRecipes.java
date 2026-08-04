package com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification;

import gregtech.api.GTValues;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class QGtDRecipes {
    /**Creates QGtD material-related recipes which do not rely on the GTCEu recipe maps being populated.
     * This method is called during the {@link RegistryEvent} for recipes.
     */
    public static void createNewMaterialRecipes() {
        RecipeMaps.MIXER_RECIPES.recipeBuilder() //RuTaPtU mixture recipe in EV mixer
                .input(OrePrefix.dust,Materials.Ruthenium,1)
                .input(OrePrefix.dust,Materials.Tantalum,1)
                .input(OrePrefix.dust,Materials.Platinum,1)
                .input(OrePrefix.dust,Materials.Uranium238,3)
                .output(OrePrefix.dust,QGtDMaterials.RuTaPtUAlloy,6)
                .EUt(GTValues.VA[GTValues.EV])
                .duration(300) //15 seconds
                .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder() //RuTaPtU coils
                .input(OrePrefix.wireGtDouble,QGtDMaterials.RuTaPtUAlloy,8)
                .input(OrePrefix.foil,Materials.VanadiumSteel,8)
                .fluidInputs(Materials.Nichrome.getFluid(144))
                .output(Item.getByNameOrId("qgtd_core:machine_coil"),1)
                .EUt(GTValues.VA[GTValues.EV])
                .duration(500) //25 seconds
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

    /**Alters GTCEu's auto-generated recipes for materials created by QGtD.
     * This method must run during the {@link FMLInitializationEvent} phase to ensure that the GTCEu recipe maps are populated to avoid {@link NullPointerException}.
     */
    public static void alterMaterialRecipes() {
        //MAKE SURE TO INCLUDE THE CIRCUIT ITEM TO FIND RECIPES WITH PROGRAMMED CIRCUITS
        Recipe activeRecipe; //Recipe object to allow modifications to existing recipes

        activeRecipe = QGtDRecipes.removeRecipeFromMachine(RecipeMaps.POLARIZER_RECIPES,new ItemStack[]{OreDictUnifier.get(OrePrefix.ingot,QGtDMaterials.CobaltOrthovanadate)});
        (new SimpleRecipeBuilder(activeRecipe,RecipeMaps.POLARIZER_RECIPES)).EUt(GTValues.VA[GTValues.HV]).buildAndRegister(); //Polarize CoVO4 ingot at HV+

        activeRecipe = QGtDRecipes.removeRecipeFromMachine(RecipeMaps.POLARIZER_RECIPES,new ItemStack[]{OreDictUnifier.get(OrePrefix.stick,QGtDMaterials.CobaltOrthovanadate)});
        (new SimpleRecipeBuilder(activeRecipe,RecipeMaps.POLARIZER_RECIPES)).EUt(GTValues.VA[GTValues.HV]).buildAndRegister(); //Polarize CoVO4 rod at HV+

        activeRecipe = QGtDRecipes.removeRecipeFromMachine(RecipeMaps.POLARIZER_RECIPES,new ItemStack[]{OreDictUnifier.get(OrePrefix.stickLong,QGtDMaterials.CobaltOrthovanadate)});
        (new SimpleRecipeBuilder(activeRecipe,RecipeMaps.POLARIZER_RECIPES)).EUt(GTValues.VA[GTValues.HV]).buildAndRegister(); //Polarize CoVO4 long rod at HV+

        activeRecipe = QGtDRecipes.removeRecipeFromMachine(RecipeMaps.VACUUM_RECIPES,new ItemStack[]{OreDictUnifier.get(OrePrefix.ingotHot,QGtDMaterials.RuTaPtUAlloy)});
        (new SimpleRecipeBuilder(activeRecipe,RecipeMaps.VACUUM_RECIPES)).duration(250).buildAndRegister(); //change duration of RuTaPtU3 vacuum freeze to 12.5 seconds to match RTM from vanilla GTCEu
    }

    /**Searches for a recipe in the specified {@code machineRecipeMap} using the specified {@code itemInputs} (assumes that there are no fluid inputs),
     * then removes and returns the target recipe from the {@code machineRecipeMap} if it was successfully found.
     * <p><b>THIS METHOD WILL FAIL TO FIND RECIPES IF CALLED BEFORE THE {@link RegistryEvent} FOR RECIPES HAS COMPLETED.</b></p>
     * @param machineRecipeMap (RecipeMap&lt;?&gt;) The {@link RecipeMap} for the machine which performs the recipe to remove
     * @param itemInputs (ItemStack[]) An array of {@link ItemStack} containing the item inputs for the target recipe. THE PROGRAMMED CIRCUIT MUST BE IN THIS ARRAY IF THE TARGET RECIPE USES GTCEu's CIRCUIT SYSTEM
     * @return (Recipe) A reference to the {@link Recipe} which was removed by this method, or {@code null} if the target recipe was not found in the {@code machineRecipeMap}
     */
    @Nullable
    public static Recipe removeRecipeFromMachine(@NotNull RecipeMap<?> machineRecipeMap, ItemStack[] itemInputs) {
        return QGtDRecipes.removeRecipeFromMachine(machineRecipeMap,itemInputs,new FluidStack[0]);
    }

    /**Searches for a recipe in the specified {@code machineRecipeMap} using the specified {@code fluidInputs} (assumes that there are no item inputs),
     * then removes and returns the target recipe from the {@code machineRecipeMap} if it was successfully found.
     * <p><b>THIS METHOD WILL FAIL TO FIND RECIPES IF CALLED BEFORE THE {@link RegistryEvent} FOR RECIPES HAS COMPLETED.</b></p>
     * @param machineRecipeMap (RecipeMap&lt;?&gt;) The {@link RecipeMap} for the machine which performs the recipe to remove
     * @param fluidInputs (FluidStack[]) An array of {@link FluidStack} containing the fluid inputs for the target recipe
     * @return (Recipe) A reference to the {@link Recipe} which was removed by this method, or {@code null} if the target recipe was not found in the {@code machineRecipeMap}
     */
    @Nullable
    public static Recipe removeRecipeFromMachine(@NotNull RecipeMap<?> machineRecipeMap, FluidStack[] fluidInputs) {
        return QGtDRecipes.removeRecipeFromMachine(machineRecipeMap,new ItemStack[0],fluidInputs);
    }

    /**Searches for a recipe in the specified {@code machineRecipeMap} using the specified {@code itemInputs} and {@code fluidInputs},
     * then removes and returns the target recipe from the {@code machineRecipeMap} if it was successfully found.
     * <p><b>THIS METHOD WILL FAIL TO FIND RECIPES IF CALLED BEFORE THE {@link RegistryEvent} FOR RECIPES HAS COMPLETED.</b></p>
     * @param machineRecipeMap (RecipeMap&lt;?&gt;) The {@link RecipeMap} for the machine which performs the recipe to remove
     * @param itemInputs (ItemStack[]) An array of {@link ItemStack} containing the item inputs for the target recipe. THE PROGRAMMED CIRCUIT MUST BE IN THIS ARRAY IF THE TARGET RECIPE USES GTCEu's CIRCUIT SYSTEM
     * @param fluidInputs (FluidStack[]) An array of {@link FluidStack} containing the fluid inputs for the target recipe
     * @return (Recipe) A reference to the {@link Recipe} which was removed by this method, or {@code null} if the target recipe was not found in the {@code machineRecipeMap}
     */
    @Nullable
    public static Recipe removeRecipeFromMachine(@NotNull RecipeMap<?> machineRecipeMap, ItemStack[] itemInputs, FluidStack[] fluidInputs) {
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

    /**Searches for a recipe in the specified {@code machineRecipeMap} using the specified {@code itemInputs} (assumes that there are no fluid inputs),
     * then removes and returns the target recipe from the {@code machineRecipeMap} if it was successfully found AND the specified {@code selectionPredicate} returns {@code true}.
     * <p>This method will only remove the FIRST recipe in the {@code machineRecipeMap} which makes the {@code selectionPredicate} {@code true}</p>
     * <p><b>THIS METHOD WILL FAIL TO FIND RECIPES IF CALLED BEFORE THE {@link RegistryEvent} FOR RECIPES HAS COMPLETED.</b></p>
     * @param machineRecipeMap (RecipeMap&lt;?&gt;) The {@link RecipeMap} for the machine which performs the recipe to remove
     * @param itemInputs (ItemStack[]) An array of {@link ItemStack} containing the item inputs for the target recipe. THE PROGRAMMED CIRCUIT MUST BE IN THIS ARRAY IF THE TARGET RECIPE USES GTCEu's CIRCUIT SYSTEM
     * @param selectionPredicate (Predicate&lt;Recipe&gt;) A {@link Predicate} which takes in a {@link Recipe} and returns {@code true} ONLY for recipes which should be removed by this method call
     * @return (Recipe) A reference to the {@link Recipe} which was removed by this method, or {@code null} if the target recipe was not found in the {@code machineRecipeMap}
     */
    @Nullable
    public static Recipe removeRecipeFromMachine(@NotNull RecipeMap<?> machineRecipeMap, ItemStack[] itemInputs, @NotNull Predicate<Recipe> selectionPredicate) {
        return QGtDRecipes.removeRecipeFromMachine(machineRecipeMap,itemInputs,new FluidStack[0],selectionPredicate);
    }

    /**Searches for a recipe in the specified {@code machineRecipeMap} using the specified {@code fluidInputs} (assumes that there are no item inputs),
     * then removes and returns the target recipe from the {@code machineRecipeMap} if it was successfully found AND the specified {@code selectionPredicate} returns {@code true}.
     * <p>This method will only remove the FIRST recipe in the {@code machineRecipeMap} which makes the {@code selectionPredicate} {@code true}</p>
     * <p><b>THIS METHOD WILL FAIL TO FIND RECIPES IF CALLED BEFORE THE {@link RegistryEvent} FOR RECIPES HAS COMPLETED.</b></p>
     * @param machineRecipeMap (RecipeMap&lt;?&gt;) The {@link RecipeMap} for the machine which performs the recipe to remove
     * @param fluidInputs (FluidStack[]) An array of {@link FluidStack} containing the fluid inputs for the target recipe
     * @param selectionPredicate (Predicate&lt;Recipe&gt;) A {@link Predicate} which takes in a {@link Recipe} and returns {@code true} ONLY for recipes which should be removed by this method call
     * @return (Recipe) A reference to the {@link Recipe} which was removed by this method, or {@code null} if the target recipe was not found in the {@code machineRecipeMap}
     */
    @Nullable
    public static Recipe removeRecipeFromMachine(@NotNull RecipeMap<?> machineRecipeMap, FluidStack[] fluidInputs, @NotNull Predicate<Recipe> selectionPredicate) {
        return QGtDRecipes.removeRecipeFromMachine(machineRecipeMap,new ItemStack[0],fluidInputs,selectionPredicate);
    }

    /**Searches for a recipe in the specified {@code machineRecipeMap} using the specified {@code itemInputs} and {@code fluidInputs},
     * then removes and returns the target recipe from the {@code machineRecipeMap} if it was successfully found AND the specified {@code selectionPredicate} returns {@code true}.
     * <p>This method will only remove the FIRST recipe in the {@code machineRecipeMap} which makes the {@code selectionPredicate} {@code true}</p>
     * <p><b>THIS METHOD WILL FAIL TO FIND RECIPES IF CALLED BEFORE THE {@link RegistryEvent} FOR RECIPES HAS COMPLETED.</b></p>
     * @param machineRecipeMap (RecipeMap&lt;?&gt;) The {@link RecipeMap} for the machine which performs the recipe to remove
     * @param itemInputs (ItemStack[]) An array of {@link ItemStack} containing the item inputs for the target recipe. THE PROGRAMMED CIRCUIT MUST BE IN THIS ARRAY IF THE TARGET RECIPE USES GTCEu's CIRCUIT SYSTEM
     * @param fluidInputs (FluidStack[]) An array of {@link FluidStack} containing the fluid inputs for the target recipe
     * @param selectionPredicate (Predicate&lt;Recipe&gt;) A {@link Predicate} which takes in a {@link Recipe} and returns {@code true} ONLY for recipes which should be removed by this method call
     * @return (Recipe) A reference to the {@link Recipe} which was removed by this method, or {@code null} if the target recipe was not found in the {@code machineRecipeMap}
     */
    @Nullable
    public static Recipe removeRecipeFromMachine(@NotNull RecipeMap<?> machineRecipeMap, @Nullable ItemStack[] itemInputs, @Nullable FluidStack[] fluidInputs, @NotNull Predicate<Recipe> selectionPredicate) {
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