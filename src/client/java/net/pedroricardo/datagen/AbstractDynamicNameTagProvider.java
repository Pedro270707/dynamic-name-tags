package net.pedroricardo.datagen;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.pedroricardo.content.DynamicNameTag;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDynamicNameTagProvider implements DataProvider {
    protected final FabricDataOutput dataOutput;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

    public AbstractDynamicNameTagProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        this.dataOutput = dataOutput;
        this.registryLookup = registryLookup;
    }

    /**
     * Implement this method to register dynamic name tags.
     * @param registryLookup the registry lookup.
     * @param dynamicNameTags add items to this map to generate dynamic name tags.
     */
    public abstract void generateDynamicNameTags(RegistryWrapper.WrapperLookup registryLookup, Map<Identifier, DynamicNameTag> dynamicNameTags);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookup.thenCompose(lookup -> this.run(writer, lookup));
    }

    public CompletableFuture<?> run(DataWriter writer, RegistryWrapper.WrapperLookup registryLookup) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        Map<Identifier, DynamicNameTag> dynamicNameTags = new HashMap<>();

        generateDynamicNameTags(registryLookup, dynamicNameTags);

        for (Map.Entry<Identifier, DynamicNameTag> entry : dynamicNameTags.entrySet()) {
            list.add(DataProvider.writeCodecToPath(writer, registryLookup, DynamicNameTag.CODEC, entry.getValue(), getPath(entry.getKey())));
        }
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    private Path getPath(Identifier id) {
        return this.dataOutput.getResolver(DataOutput.OutputType.RESOURCE_PACK, "dynamic_name_tags")
                .resolveJson(id);
    }

    @Override
    public String getName() {
        return "Dynamic Name Tags";
    }
}
