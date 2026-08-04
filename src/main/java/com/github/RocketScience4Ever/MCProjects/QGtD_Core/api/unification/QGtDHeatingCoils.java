package com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification;

import com.github.RocketScience4Ever.MCProjects.QGtD_Core.common.blocks.coils.QGtDWireCoil;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.main.QGtDCore;

import gregtech.api.GregTechAPI;
import gregtech.common.blocks.BlockWireCoil;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class QGtDHeatingCoils {
    public static BlockWireCoil SAMPLE_GTCEU_COIL;
    public static QGtDWireCoil SAMPLE_QGTD_COIL;

    /**Prepares samples of QGtD and GTCEu EBF coils for use during block and item registration
     * @param event (FMLPreInitializationEvent) The event received by {@link QGtDCore#onPreInit(FMLPreInitializationEvent)}
     */
    public static void onPreInit(FMLPreInitializationEvent event) {
        SAMPLE_GTCEU_COIL = new BlockWireCoil(); //Create a sample of a BlockWireCoil for alteration of GTCEu's existing coils
        SAMPLE_QGTD_COIL = new QGtDWireCoil(); //Create a sample of a QGtDWireCoil for registry purposes
        SAMPLE_QGTD_COIL.setRegistryName("machine_coil"); //Registry name for QGtD coil blocks and items
    }

    /**Registers all coils in {@link QGtDWireCoil.QGtDCoilType} to GTCEu's mapping for EBF coils.
     */
    public static void registerCoilsToGregTech() {
        for (QGtDWireCoil.QGtDCoilType type : QGtDWireCoil.QGtDCoilType.values()) {
            GregTechAPI.HEATING_COILS.put(SAMPLE_QGTD_COIL.getState(type),type); //Add each coil in the QGtDCoilType enum to the map that GTCEu uses to store EBF coil stats
        }
    }

    /**De-registers EBF coils from GTCEu which are disabled in QGtD.
     */
    public static void removeExistingCoils() {
        GregTechAPI.HEATING_COILS.remove(SAMPLE_GTCEU_COIL.getState(BlockWireCoil.CoilType.RTM_ALLOY)); //RTM is replaced with RuTaPtU alloy in EV
    }
}