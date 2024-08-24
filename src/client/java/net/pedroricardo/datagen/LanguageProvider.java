package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LanguageProvider extends FabricLanguageProvider {
    public LanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("dynamicnametags.literal", "%s");
        translationBuilder.add("dynamicnametags.dinnerbone", "ǝuoqɹǝuuᴉᗡ");
        translationBuilder.add("dynamicnametags.grumm", "ɯɯnɹ⅁");
    }
}
