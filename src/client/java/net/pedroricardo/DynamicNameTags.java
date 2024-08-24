package net.pedroricardo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceType;
import net.pedroricardo.content.DynamicNameTag;
import net.pedroricardo.content.DynamicNameTagResourceListener;
import net.pedroricardo.content.TextFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DynamicNameTags implements ClientModInitializer {
	public static final String MOD_ID = "dynamicnametags";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final List<Function<RegistryWrapper.WrapperLookup, DynamicNameTag>> DYNAMIC_NAME_TAGS = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		TextFunctions.init();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new DynamicNameTagResourceListener());
//		TextFunctionRegistry.register("Dinnerbone", (entity, text, matrices, vertexConsumers, light, dispatcher) -> Text.literal("ǝuoqɹǝuuᴉᗡ"));
//		TextFunctionRegistry.register("Grumm", (entity, text, matrices, vertexConsumers, light, dispatcher) -> Text.literal("ɯɯnɹ⅁"));
//		TextFunctionRegistry.register("Johnny", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
//			if (!(entity instanceof VindicatorEntity)) return text;
//			return text.copy().setStyle(Style.EMPTY.withFormatting(Formatting.RED));
//		});
//		TextFunctionRegistry.register("Toast", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
//			if (!(entity instanceof RabbitEntity)) return text;
//			return text.copy().setStyle(Style.EMPTY.withFormatting(Formatting.GOLD));
//		});
//		TextFunctionRegistry.register("The Killer Bunny", (entity, text, matrices, vertexConsumers, light, dispatcher) -> {
//			if (!(entity instanceof RabbitEntity) || ((RabbitEntity)entity).getVariant() != RabbitEntity.RabbitType.EVIL) return text;
//			return text.copy().setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED, Formatting.ITALIC));
//		});
	}
}