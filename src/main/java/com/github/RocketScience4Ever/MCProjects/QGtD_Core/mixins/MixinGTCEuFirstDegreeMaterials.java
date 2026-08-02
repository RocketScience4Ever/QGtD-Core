package com.github.RocketScience4Ever.MCProjects.QGtD_Core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.materials.FirstDegreeMaterials;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FirstDegreeMaterials.class)
public class MixinGTCEuFirstDegreeMaterials {
    /**Intercept the {@link Material.Builder} for uvarovite to force the creation of uvarovite ore.
     * @param instance (Material.Builder) The intercepted {@code Material.Builder}
     * @param original (Operation<Material>) The intercepted call to {@code instance.build()}
     * @return (Material) The {@link Material} for uvarovite, including the modifications applied by this method
     */
    @WrapOperation(method = "register",at = @At(value = "INVOKE", target = "Lgregtech/api/unification/material/Material$Builder;build()Lgregtech/api/unification/material/Material;",ordinal = 81))
    private static Material onUvaroviteMaterialBuild(Material.Builder instance, Operation<Material> original) {
        return instance.ore(3,1).build();
    }
}