package com.github.RocketScience4Ever.MCProjects.QGtD_Core.main;

import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDHeatingCoils;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDMaterials;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDMultiblocks;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDRecipes;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDUtils;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.qgtd_core.Tags;

import gregtech.api.block.VariantItemBlock;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.PostMaterialEvent;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = "qgtd_core", name = "QGtD Core", version = "0.1.0", dependencies = "required:gregtech@[2.8.10-beta,);required:gcym@[1.2.11,);") //required:mixinbooter@[11.12,);
@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class QGtDCore {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMaterialRegistry(MaterialEvent event) {
        QGtDMaterials.registerNewMaterials(); //Add materials that are specific to QGtD
    }

    @SubscribeEvent
    public static void onMaterialPostRegistry(PostMaterialEvent event) {
        QGtDMaterials.flagExistingMaterials(); //Add new components to existing GTCEu or GCYM materials
        QGtDMaterials.addExistingMaterialOres(); //Add ores to existing GTCEu or GCYM materials
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> blockRegistry = event.getRegistry(); //Get the block registry from Forge
        QGtDMultiblocks.registerMetaTileEntities(); //Register multiblocks
        blockRegistry.register(QGtDHeatingCoils.SAMPLE_QGTD_COIL); //Register the QGtD EBF coils as blocks
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> itemRegistry = event.getRegistry(); //Get the item registry from Forge
        itemRegistry.register(QGtDUtils.createItemBlockFromBlock(QGtDHeatingCoils.SAMPLE_QGTD_COIL,VariantItemBlock::new)); //Register the items for QGtD EBF coils
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        QGtDRecipes.createNewMaterialRecipes(); //Register recipes which do not rely on finding and removing an existing GTCEu or GCYM recipe
    }

    @Mod.EventHandler
    public static void onPreInit(FMLPreInitializationEvent event) {
        QGtDHeatingCoils.onPreInit(event); //Create a sample instance of a QGtDWireCoil to use during registry
        QGtDHeatingCoils.registerCoilsToGregTech(); //Register QGtD EBF coils to the GTCEu coil map
        QGtDHeatingCoils.removeExistingCoils(); //Remove any vanilla GTCEu EBF coils which are not used in QGtD
    }

    @Mod.EventHandler
    public static void onInit(FMLInitializationEvent event) {
        QGtDRecipes.alterMaterialRecipes(); //Register recipes which need to be registered after recipes from GTCEu and GCYM have been added to the recipe maps
    }

    @Mod.EventBusSubscriber(Side.CLIENT)
    public static class ClientOnlyCore {
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            QGtDHeatingCoils.SAMPLE_QGTD_COIL.onModelRegister(); //Register models for QGtD EBF coils
        }
    }
}