package net.pedroricardo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class DynamicNameTagsRegistry {
    private static final Map<String, EntityFunction> REGISTRY = new HashMap<>();

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
        Text apply(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderDispatcher dispatcher);
    }
}