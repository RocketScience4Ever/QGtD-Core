package com.github.RocketScience4Ever.MCProjects.QGtD_Core.api.unification;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class QGtDUtils {
    public static ResourceLocation createQGtDId(String name) {
        return new ResourceLocation("qgtd_core",name);
    }

    public static <T extends Block> ItemBlock createItemBlockFromBlock(@NotNull T block, @NotNull Function<T, ItemBlock> itemBlockProducer) {
        ItemBlock itemBlock = itemBlockProducer.apply(block);
        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return itemBlock;
    }
}