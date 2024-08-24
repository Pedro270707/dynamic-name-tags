package net.pedroricardo.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record ClientLightPredicate(NumberRange.IntRange range) {
    public static final Codec<ClientLightPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(NumberRange.IntRange.CODEC.optionalFieldOf("light", NumberRange.IntRange.ANY).forGetter(ClientLightPredicate::range)).apply(instance, ClientLightPredicate::new);
    });

    public boolean test(World world, BlockPos pos) {
        if (!world.canSetBlock(pos)) {
            return false;
        } else {
            return this.range.test(world.getLightLevel(pos));
        }
    }

    public static class Builder {
        private NumberRange.IntRange light;

        public Builder() {
            this.light = NumberRange.IntRange.ANY;
        }

        public static ClientLightPredicate.Builder create() {
            return new ClientLightPredicate.Builder();
        }

        public ClientLightPredicate.Builder light(NumberRange.IntRange light) {
            this.light = light;
            return this;
        }

        public ClientLightPredicate build() {
            return new ClientLightPredicate(this.light);
        }
    }
}
