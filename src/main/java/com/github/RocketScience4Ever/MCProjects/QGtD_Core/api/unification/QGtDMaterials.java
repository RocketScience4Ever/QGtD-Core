package com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification;

import gregicality.multiblocks.api.unification.GCYMMaterials;

import gregtech.api.GTValues;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.info.MaterialIconSet;
import gregtech.api.unification.material.properties.BlastProperty;
import gregtech.api.unification.material.properties.OreProperty;
import gregtech.api.unification.material.properties.PropertyKey;

public class QGtDMaterials {
    private static int NEXT_ID;

    public static Material RuTaPtU3Alloy;
    public static Material BoronCarbide;
    public static Material CobaltVanadiumMixture;
    public static Material CobaltOrthovanadate;
    public static Material MagneticCobaltOrthovanadate;

    public static int nextMaterialID() {
        return NEXT_ID++;
    }

    public static void flagExistingMaterials() {
        GCYMMaterials.TitaniumCarbide.addFlags(MaterialFlags.GENERATE_ROD,MaterialFlags.GENERATE_FRAME);
        Materials.Diamond.addFlags(MaterialFlags.NO_WORKING);
    }

    public static void addExistingMaterialOres() {
        Materials.Uvarovite.setProperty(PropertyKey.ORE,new OreProperty(3,1));
        Materials.Borax.setProperty(PropertyKey.ORE,new OreProperty(2,1));
    }

    public static void registerNewMaterials() {
        QGtDMaterials.NEXT_ID = 31000; //Start QGtD material ids at 31000

        //RuTaPtU3
        RuTaPtU3Alloy = (new Material.Builder(QGtDMaterials.nextMaterialID(),QGtDUtils.createQGtDId("rutaptu_alloy")))
                .ingot().fluid().color(7465205).iconSet(MaterialIconSet.METALLIC)
                .components(Materials.Ruthenium,1,Materials.Tantalum,1,Materials.Platinum,1,Materials.Uranium238,3)
                .blast((BlastProperty.Builder blastBuilder) -> blastBuilder.temp(3000,BlastProperty.GasTier.LOW).blastStats(GTValues.VA[4],1400).vacuumStats(GTValues.VA[3]))
                .flags(MaterialFlags.GENERATE_ROD,MaterialFlags.GENERATE_LONG_ROD,MaterialFlags.GENERATE_SPRING,MaterialFlags.DECOMPOSITION_BY_CENTRIFUGING)
                .cableProperties(GTValues.V[4],6,2)
                .build().setFormula("RuTaPtU3");

        //Boron Carbide
        BoronCarbide = (new Material.Builder(QGtDMaterials.nextMaterialID(),QGtDUtils.createQGtDId("boron_carbide")))
                .ingot().color(2500903).iconSet(MaterialIconSet.DULL)
                .components(Materials.Boron,4,Materials.Carbon,1)
                .blast((BlastProperty.Builder blastBuilder) -> blastBuilder.temp(2700,BlastProperty.GasTier.LOW).blastStats(GTValues.VA[4],1200).vacuumStats(GTValues.VA[3]))
                .flags(MaterialFlags.GENERATE_PLATE,MaterialFlags.GENERATE_DENSE,MaterialFlags.NO_SMASHING,MaterialFlags.NO_SMELTING,MaterialFlags.NO_WORKING)
                .build();

        //Cobalt orthovanadate
        CobaltVanadiumMixture = (new Material.Builder(QGtDMaterials.nextMaterialID(),QGtDUtils.createQGtDId("cobalt_vanadium_mixture")))
                .dust().color(264484).iconSet(MaterialIconSet.METALLIC)
                .components(Materials.Cobalt,1,Materials.Vanadium,1)
                .flags(MaterialFlags.NO_SMASHING,MaterialFlags.NO_SMELTING,MaterialFlags.NO_WORKING,MaterialFlags.DECOMPOSITION_BY_CENTRIFUGING)
                .build();

        CobaltOrthovanadate = (new Material.Builder(QGtDMaterials.nextMaterialID(),QGtDUtils.createQGtDId("cobalt_orthovanadate")))
                .ingot().fluid().color(660551).iconSet(MaterialIconSet.METALLIC)
                .components(Materials.Cobalt,1,Materials.Vanadium,1,Materials.Oxygen,4)
                .blast((BlastProperty.Builder blastBuilder) -> blastBuilder.temp(2400,BlastProperty.GasTier.LOW).blastStats(GTValues.VA[4],1300).vacuumStats(GTValues.VA[3]))
                .flags(MaterialFlags.GENERATE_ROD,MaterialFlags.GENERATE_LONG_ROD,MaterialFlags.DECOMPOSITION_BY_ELECTROLYZING)
                .cableProperties(GTValues.V[5],4,3)
                .build();

        MagneticCobaltOrthovanadate = (new Material.Builder(QGtDMaterials.nextMaterialID(),QGtDUtils.createQGtDId("magnetic_cobalt_orthovanadate")))
                .ingot().color(660551).iconSet(MaterialIconSet.MAGNETIC)
                .ingotSmeltInto(CobaltOrthovanadate).arcSmeltInto(CobaltOrthovanadate)
                .components(Materials.Cobalt,1,Materials.Vanadium,1,Materials.Oxygen,4)
                .flags(MaterialFlags.GENERATE_ROD,MaterialFlags.GENERATE_LONG_ROD,MaterialFlags.IS_MAGNETIC,MaterialFlags.NO_SMASHING,MaterialFlags.NO_WORKING,MaterialFlags.NO_UNIFICATION,MaterialFlags.EXCLUDE_BLOCK_CRAFTING_RECIPES,MaterialFlags.DISABLE_DECOMPOSITION)
                .build();
        CobaltOrthovanadate.getProperty(PropertyKey.INGOT).setMagneticMaterial(MagneticCobaltOrthovanadate); //Allow CoVO4 to be magnetized into magnetic CoVO4
    }
}