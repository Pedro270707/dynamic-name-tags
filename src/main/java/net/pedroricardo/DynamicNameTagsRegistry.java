package net.pedroricardo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class DynamicNameTagsRegistry {
    private static final Map<String, EntityFunction> REGISTRY = new HashMap<>();

    public static void remove(String entityName) {
        REGISTRY.remove(entityName);
    }

    protected static void freezeRegistry() {
        Map<String, EntityFunction> frozenRegistry = Map.copyOf(REGISTRY);
        REGISTRY.clear();
        REGISTRY.putAll(frozenRegistry);
    }

    public static void register(String entityName, EntityFunction function) {
        if (!REGISTRY.containsKey(entityName)) {
            REGISTRY.put(entityName, function);
        } else {
            DynamicNameTags.LOGGER.error("Duplicate key: " + entityName);
        }
    }

    public static Map<String, EntityFunction> getRegistry() {
        return Map.copyOf(REGISTRY);
    }

    @FunctionalInterface
    public interface EntityFunction {
        Component apply(Entity entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, EntityRenderDispatcher dispatcher);
    }
}