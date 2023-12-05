package net.pedroricardo.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.pedroricardo.DynamicNameTagsRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderer.class)
public abstract class ApplyEntityFunctionsMixin<T extends Entity> {
    @Shadow
    protected abstract void renderNameTag(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light);

    @Final
    @Shadow
    protected EntityRenderDispatcher entityRenderDispatcher;

    @Unique
    boolean forge_mdk$isRecall = false;

    @Inject(method = "renderNameTag*", at = @At("HEAD"), cancellable = true)
    private void renderNameTag2(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (!this.forge_mdk$isRecall) {
            this.forge_mdk$isRecall = true;

            Map<String, DynamicNameTagsRegistry.EntityFunction> registry = DynamicNameTagsRegistry.getRegistry();

            if (registry.containsKey(text.getString())) {
                text = registry.get(text.getString()).apply(entity, text, matrices, vertexConsumers, light, this.entityRenderDispatcher);
            }

            this.renderNameTag(entity, text, matrices, vertexConsumers, light);
            ci.cancel();
        } else {
            this.forge_mdk$isRecall = false;
        }
    }
}
