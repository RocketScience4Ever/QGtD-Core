package com.github.RocketScience4Ever.MCProjects.QGtD_Core.main;

import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDMaterials;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDMultiblocks;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification.QGtDRecipes;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.qgtd_core.Tags;

import gregtech.api.unification.material.event.MaterialEvent;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "qgtd_core", name = "QGtD Core", version = "0.1.0", dependencies = "required:gregtech@[2.8.10-beta,);required:gcym@[1.2.11,);") //required:mixinbooter@[11.12,);
@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class QGtDCore {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMaterialRegistry(MaterialEvent event) {
        QGtDMaterials.registerNewMaterials(); //Add materials that are specific to QGtD
        QGtDMaterials.flagExistingMaterials(); //Add new components to existing GTCEu or GCYM materials
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        QGtDMultiblocks.registerMetaTileEntities(); //Register multiblocks
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        QGtDRecipes.registerMaterialRecipes(); //Register special recipes for specific materials
    }
}