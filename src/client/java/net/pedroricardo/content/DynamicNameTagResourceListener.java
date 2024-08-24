package net.pedroricardo.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.pedroricardo.DynamicNameTags;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class DynamicNameTagResourceListener implements IdentifiableResourceReloadListener {
    private static final Identifier ID = Identifier.of(DynamicNameTags.MOD_ID, "dynamic_name_tags");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        List<Function<RegistryWrapper.WrapperLookup, DynamicNameTag>> list = new ArrayList<>();

        Map<Identifier, Resource> resources = manager.findResources("dynamic_name_tags", id -> id.getPath().endsWith(".json"));

        synchronizer.whenPrepared(null);

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try {
                final JsonElement element = JsonParser.parseReader(new InputStreamReader(entry.getValue().getInputStream()));
                list.add(lookup -> DynamicNameTag.CODEC.parse(lookup.getOps(JsonOps.INSTANCE), element).getOrThrow());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DynamicNameTags.DYNAMIC_NAME_TAGS.clear();
        DynamicNameTags.DYNAMIC_NAME_TAGS.addAll(list);
        return CompletableFuture.allOf();
    }
}
