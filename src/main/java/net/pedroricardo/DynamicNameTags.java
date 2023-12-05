package net.pedroricardo;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(DynamicNameTags.MOD_ID)
public class DynamicNameTags {
	public static final String MOD_ID = "dynamicnametags";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public DynamicNameTags() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			DynamicNameTagsRegistry.register("jeb_", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
				if (!(entity instanceof Sheep)) return text;
				int n = entity.tickCount / 25 + entity.getId();
				int o = DyeColor.values().length;
				int p = n % o;
				int q = (n + 1) % o;
				float r = ((float)(entity.tickCount % 25) + 1.0f) / 25.0F;
				float[] fs = Sheep.getColorArray(DyeColor.byId(p));
				float[] gs = Sheep.getColorArray(DyeColor.byId(q));
				int s = (int)(255.0f * (fs[0] * (1.0f - r) + gs[0] * r));
				int t = (int)(255.0f * (fs[1] * (1.0f - r) + gs[1] * r));
				int u = (int)(255.0f * (fs[2] * (1.0f - r) + gs[2] * r));
				return text.copy().setStyle(Style.EMPTY.withColor((s << 16) | (t << 8) | u));
			});
			DynamicNameTagsRegistry.register("Dinnerbone", (entity, text, matrices, vertexConsumers, light, dispatcher) -> Component.literal("ǝuoqɹǝuuᴉᗡ"));
			DynamicNameTagsRegistry.register("Grumm", (entity, text, matrices, vertexConsumers, light, dispatcher) -> Component.literal("ɯɯnɹ⅁"));
			DynamicNameTagsRegistry.register("Johnny", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
				if (!(entity instanceof Vindicator)) return text;
				return text.copy().setStyle(Style.EMPTY.applyFormat(ChatFormatting.RED));
			});
			DynamicNameTagsRegistry.register("Toast", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
				if (!(entity instanceof Rabbit)) return text;
				return text.copy().setStyle(Style.EMPTY.applyFormat(ChatFormatting.GOLD));
			});
			DynamicNameTagsRegistry.register("The Killer Bunny", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
				if (!(entity instanceof Rabbit) || ((Rabbit)entity).getVariant() != Rabbit.Variant.EVIL) return text;
				return text.copy().setStyle(Style.EMPTY.applyFormats(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
			});
		}
	}
}