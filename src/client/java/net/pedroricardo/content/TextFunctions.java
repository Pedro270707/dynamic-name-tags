package net.pedroricardo.content;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.registry.Registry;
import net.minecraft.text.Style;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.pedroricardo.DynamicNameTags;

public class TextFunctions {
    private TextFunctions() {}

    public static final TextFunction JEB = register("jeb", (entity, text, matrices, vertexConsumers, light, tickDelta, dispatcher) -> {
        int n = entity.age / 25 + entity.getId();
        int o = DyeColor.values().length;
        int p = n % o;
        int q = (n + 1) % o;
        float r = ((float)(entity.age % 25) + tickDelta) / 25.0f;
        int s = SheepEntity.getRgbColor(DyeColor.byId(p));
        int t = SheepEntity.getRgbColor(DyeColor.byId(q));
        return text.copy().setStyle(Style.EMPTY.withColor(ColorHelper.Argb.lerp(r, s, t)));
    });

    public static void init() {
        DynamicNameTags.LOGGER.debug("Registering text functions");
    }

    public static TextFunction register(String id, TextFunction function) {
        return Registry.register(TextFunctionRegistry.REGISTRY, Identifier.of(DynamicNameTags.MOD_ID, id), function);
    }
}
