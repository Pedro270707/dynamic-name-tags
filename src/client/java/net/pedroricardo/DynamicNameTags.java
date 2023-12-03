package net.pedroricardo;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicNameTags implements ClientModInitializer {
	public static final String MOD_ID = "dynamicnametags";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		DynamicNameTagsRegistry.register("jeb_", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
			if (!(entity instanceof SheepEntity)) return text;
			int n = entity.age / 25 + entity.getId();
			int o = DyeColor.values().length;
			int p = n % o;
			int q = (n + 1) % o;
			float r = ((float)(entity.age % 25) + 1.0f) / 25.0F;
			float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
			float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
			int s = (int)(255.0f * (fs[0] * (1.0f - r) + gs[0] * r));
			int t = (int)(255.0f * (fs[1] * (1.0f - r) + gs[1] * r));
			int u = (int)(255.0f * (fs[2] * (1.0f - r) + gs[2] * r));
			return text.copy().setStyle(Style.EMPTY.withColor((s << 16) | (t << 8) | u));
		});
		DynamicNameTagsRegistry.register("Dinnerbone", (entity, text, matrices, vertexConsumers, light, dispatcher) -> Text.literal("ǝuoqɹǝuuᴉᗡ"));
		DynamicNameTagsRegistry.register("Grumm", (entity, text, matrices, vertexConsumers, light, dispatcher) -> Text.literal("ɯɯnɹ⅁"));
		DynamicNameTagsRegistry.register("Johnny", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
			if (!(entity instanceof VindicatorEntity)) return text;
			return text.copy().setStyle(Style.EMPTY.withFormatting(Formatting.RED));
		});
		DynamicNameTagsRegistry.register("Toast", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
			if (!(entity instanceof RabbitEntity)) return text;
			return text.copy().setStyle(Style.EMPTY.withFormatting(Formatting.GOLD));
		});
		DynamicNameTagsRegistry.register("The Killer Bunny", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
			if (!(entity instanceof RabbitEntity) || ((RabbitEntity)entity).getVariant() != RabbitEntity.RabbitType.EVIL) return text;
			return text.copy().setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED, Formatting.ITALIC));
		});
	}
}