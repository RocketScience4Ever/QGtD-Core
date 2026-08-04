package com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification;

import com.github.RocketScience4Ever.MCProjects.QGtD_Core.metatileentities.multiblock.MetaTileEntityBecquerelicAccelerator;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.metatileentities.multiblock.MetaTileEntityBonsaicFacilitator;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.metatileentities.multiblock.MetaTileEntityCelestialArtifactory;
import com.github.RocketScience4Ever.MCProjects.QGtD_Core.metatileentities.multiblock.MetaTileEntityLaserEnrichmentChamber;

import gregtech.common.metatileentities.MetaTileEntities;

public class QGtDMultiblocks {
    private static int NEXT_ID;

    public static int nextMultiblockID() {
        return NEXT_ID++;
    }

    /**Registers QGtD multiblocks to be able to form using GTCEu's multiblock logic.
     */
    public static void registerMetaTileEntities() {
        QGtDMultiblocks.NEXT_ID = 30000;

        MetaTileEntities.registerMetaTileEntity(QGtDMultiblocks.nextMultiblockID(),new MetaTileEntityBonsaicFacilitator(QGtDUtils.createQGtDId("bonsaic_facilitator")));
        MetaTileEntities.registerMetaTileEntity(QGtDMultiblocks.nextMultiblockID(),new MetaTileEntityBecquerelicAccelerator(QGtDUtils.createQGtDId("becquerelic_accelerator")));
        MetaTileEntities.registerMetaTileEntity(QGtDMultiblocks.nextMultiblockID(),new MetaTileEntityLaserEnrichmentChamber(QGtDUtils.createQGtDId("laser_enrichment_chamber")));
        MetaTileEntities.registerMetaTileEntity(QGtDMultiblocks.nextMultiblockID(),new MetaTileEntityCelestialArtifactory(QGtDUtils.createQGtDId("celestial_artifactory")));
    }
}