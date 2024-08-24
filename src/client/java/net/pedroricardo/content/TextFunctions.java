package net.pedroricardo.content;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.registry.Registry;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
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
    public static final TextFunction DAMAGE = register("damage", (entity, text, matrices, vertexConsumers, light, tickDelta, dispatcher) -> {
        if (!(entity instanceof LivingEntity livingEntity)) return text;
        TextColor originalColor = text.getStyle().getColor();
        if (originalColor == null) {
            originalColor = TextColor.fromRgb(0xFFFFFF);
        }
        float originalRed = ((originalColor.getRgb() >> 16) & 0xFF) * (livingEntity.getHealth() / livingEntity.getMaxHealth());
        float originalGreen = ((originalColor.getRgb() >> 8) & 0xFF) * (livingEntity.getHealth() / livingEntity.getMaxHealth());
        float originalBlue = (originalColor.getRgb() & 0xFF) * (livingEntity.getHealth() / livingEntity.getMaxHealth());
        float damageRed = ((Formatting.DARK_RED.getColorValue() >> 16) & 0xFF) * (1.0f - livingEntity.getHealth() / livingEntity.getMaxHealth());
        float damageGreen = ((Formatting.DARK_RED.getColorValue() >> 8) & 0xFF) * (1.0f - livingEntity.getHealth() / livingEntity.getMaxHealth());
        float damageBlue = (Formatting.DARK_RED.getColorValue() & 0xFF) * (1.0f - livingEntity.getHealth() / livingEntity.getMaxHealth());
        return text.copy().setStyle(text.getStyle().withColor(
                Math.round((((int)originalRed) << 16 | ((int)originalGreen) << 8 | ((int)originalBlue)) + (((int)damageRed) << 16 | ((int)damageGreen) << 8 | ((int)damageBlue)))
        ));
    });

    public static void init() {
        DynamicNameTags.LOGGER.debug("Registering text functions");
    }

    public static TextFunction register(String id, TextFunction function) {
        return Registry.register(TextFunctionRegistry.REGISTRY, Identifier.of(DynamicNameTags.MOD_ID, id), function);
    }
}
