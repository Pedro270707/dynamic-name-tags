package net.pedroricardo.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.pedroricardo.DynamicNameTagsRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderer.class)
public class ApplyEntityFunctionsMixin<T extends Entity> {


    @Unique
    boolean isRecall = false;

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!this.isRecall) {
            this.isRecall = true;

            Map<String, DynamicNameTagsRegistry.EntityFunction> registry = DynamicNameTagsRegistry.getRegistry();

            if (registry.containsKey(text.getString())) {
                text = registry.get(text.getString()).apply(entity, text, matrices, vertexConsumers, light, ((EntityRenderer)(Object)this).dispatcher);
            }

            ((EntityRenderer)(Object)this).renderLabelIfPresent(entity, text, matrices, vertexConsumers, light);
            ci.cancel();
        } else {
            this.isRecall = false;
        }
    }
}
