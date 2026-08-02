package com.github.RocketScience4Ever.MCProjects.QGtD_Core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.materials.SecondDegreeMaterials;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SecondDegreeMaterials.class)
public class MixinSecondDegreeMaterials {
    /**Intercept the {@link Material.Builder} for borax to force the creation of borax ore.
     * @param instance (Material.Builder) The intercepted {@code Material.Builder}
     * @param original (Operation<Material>) The intercepted call to {@code instance.build()}
     * @return (Material) The {@link Material} for borax, including the modifications applied by this method
     */
    @WrapOperation(method = "register",at = @At(value = "INVOKE", target = "Lgregtech/api/unification/material/Material$Builder;build()Lgregtech/api/unification/material/Material;",ordinal = 3))
    private static Material onUvaroviteMaterialBuild(Material.Builder instance, Operation<Material> original) {
        return instance.ore(1,1).build();
    }
}