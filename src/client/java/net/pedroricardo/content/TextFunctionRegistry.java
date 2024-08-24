package net.pedroricardo.content;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.pedroricardo.DynamicNameTags;

public class TextFunctionRegistry {
    private TextFunctionRegistry() {}

    public static final RegistryKey<Registry<TextFunction>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(DynamicNameTags.MOD_ID, "text_functions"));
    public static final Registry<TextFunction> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();
}