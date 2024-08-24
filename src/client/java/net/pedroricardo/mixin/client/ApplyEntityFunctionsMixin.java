package net.pedroricardo.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.pedroricardo.DynamicNameTags;
import net.pedroricardo.content.DynamicNameTag;
import net.pedroricardo.content.TextFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Function;

@Mixin(EntityRenderer.class)
public class ApplyEntityFunctionsMixin<T extends Entity> {
    @Mixin(EntityRenderer.class)
    private interface EntityRendererAccessor<T extends Entity> {
        @Accessor("dispatcher")
        EntityRenderDispatcher dispatcher();

        @Invoker("renderLabelIfPresent")
        void invokeRenderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta);
    }

    @Unique
    boolean isRecall = false;

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
        if (!this.isRecall) {
            this.isRecall = true;

            for (Function<RegistryWrapper.WrapperLookup, Optional<DynamicNameTag>> function : DynamicNameTags.DYNAMIC_NAME_TAGS) {
                Optional<DynamicNameTag> optional = function.apply(entity.getWorld().getRegistryManager());
                DynamicNameTag dynamicNameTag;
                if (optional.isPresent() && (dynamicNameTag = optional.get()).pattern().matcher(text.getString()).matches() && (dynamicNameTag.predicate().isEmpty() || dynamicNameTag.predicate().get().test(entity.getWorld(), MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getPos() : null, entity))) {
                    Text original = text;
                    text = dynamicNameTag.text().copy();
                    if (text.getContent() instanceof TranslatableTextContent translatable) {
                        text = Text.translatable(translatable.getKey(), original).setStyle(text.getStyle());
                    }
                    for (TextFunction textFunction : dynamicNameTag.textFunctions()) {
                        text = textFunction.apply(entity, text, matrices, vertexConsumers, light, tickDelta, ((EntityRendererAccessor<T>)this).dispatcher());
                    }
                }
            }

            ((EntityRendererAccessor<T>)this).invokeRenderLabelIfPresent(entity, text, matrices, vertexConsumers, light, tickDelta);
            ci.cancel();
        } else {
            this.isRecall = false;
        }
    }
}
