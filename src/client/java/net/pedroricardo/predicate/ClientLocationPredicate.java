package net.pedroricardo.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.CampfireBlock;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public record ClientLocationPredicate(Optional<ClientLocationPredicate.PositionRange> position, Optional<RegistryEntryList<Biome>> biomes, Optional<RegistryKey<World>> dimension, Optional<Boolean> smokey, Optional<ClientLightPredicate> light, Optional<ClientBlockPredicate> block, Optional<ClientFluidPredicate> fluid, Optional<Boolean> canSeeSky) {
    public static final Codec<ClientLocationPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ClientLocationPredicate.PositionRange.CODEC.optionalFieldOf("position").forGetter(ClientLocationPredicate::position), RegistryCodecs.entryList(RegistryKeys.BIOME).optionalFieldOf("biomes").forGetter(ClientLocationPredicate::biomes), RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("dimension").forGetter(ClientLocationPredicate::dimension), Codec.BOOL.optionalFieldOf("smokey").forGetter(ClientLocationPredicate::smokey), ClientLightPredicate.CODEC.optionalFieldOf("light").forGetter(ClientLocationPredicate::light), ClientBlockPredicate.CODEC.optionalFieldOf("block").forGetter(ClientLocationPredicate::block), ClientFluidPredicate.CODEC.optionalFieldOf("fluid").forGetter(ClientLocationPredicate::fluid), Codec.BOOL.optionalFieldOf("can_see_sky").forGetter(ClientLocationPredicate::canSeeSky)).apply(instance, ClientLocationPredicate::new);
    });

    public boolean test(World world, double x, double y, double z) {
        if (this.position.isPresent() && !this.position.get().test(x, y, z)) {
            return false;
        } else if (this.dimension.isPresent() && this.dimension.get() != world.getRegistryKey()) {
            return false;
        } else {
            BlockPos blockPos = BlockPos.ofFloored(x, y, z);
            boolean bl = world.canSetBlock(blockPos);
            if (this.biomes.isPresent() && (!bl || !this.biomes.get().contains(world.getBiome(blockPos)))) {
                return false;
            } else if (this.smokey.isEmpty() || bl && this.smokey.get() == CampfireBlock.isLitCampfireInRange(world, blockPos)) {
                if (this.light.isPresent() && !this.light.get().test(world, blockPos)) {
                    return false;
                } else if (this.block.isPresent() && !this.block.get().test(world, blockPos)) {
                    return false;
                } else if (this.fluid.isPresent() && !this.fluid.get().test(world, blockPos)) {
                    return false;
                } else {
                    return this.canSeeSky.isEmpty() || this.canSeeSky.get() == world.isSkyVisible(blockPos);
                }
            } else {
                return false;
            }
        }
    }

    private record PositionRange(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z) {
        public static final Codec<ClientLocationPredicate.PositionRange> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(NumberRange.DoubleRange.CODEC.optionalFieldOf("x", NumberRange.DoubleRange.ANY).forGetter(ClientLocationPredicate.PositionRange::x), NumberRange.DoubleRange.CODEC.optionalFieldOf("y", NumberRange.DoubleRange.ANY).forGetter(ClientLocationPredicate.PositionRange::y), NumberRange.DoubleRange.CODEC.optionalFieldOf("z", NumberRange.DoubleRange.ANY).forGetter(ClientLocationPredicate.PositionRange::z)).apply(instance, ClientLocationPredicate.PositionRange::new);
        });

        static Optional<ClientLocationPredicate.PositionRange> create(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z) {
            return x.isDummy() && y.isDummy() && z.isDummy() ? Optional.empty() : Optional.of(new ClientLocationPredicate.PositionRange(x, y, z));
        }

        public boolean test(double x, double y, double z) {
            return this.x.test(x) && this.y.test(y) && this.z.test(z);
        }
    }

    public static class Builder {
        private NumberRange.DoubleRange x;
        private NumberRange.DoubleRange y;
        private NumberRange.DoubleRange z;
        private Optional<RegistryEntryList<Biome>> biome;
        private Optional<RegistryKey<World>> dimension;
        private Optional<Boolean> smokey;
        private Optional<ClientLightPredicate> light;
        private Optional<ClientBlockPredicate> block;
        private Optional<ClientFluidPredicate> fluid;
        private Optional<Boolean> canSeeSky;

        public Builder() {
            this.x = NumberRange.DoubleRange.ANY;
            this.y = NumberRange.DoubleRange.ANY;
            this.z = NumberRange.DoubleRange.ANY;
            this.biome = Optional.empty();
            this.dimension = Optional.empty();
            this.smokey = Optional.empty();
            this.light = Optional.empty();
            this.block = Optional.empty();
            this.fluid = Optional.empty();
            this.canSeeSky = Optional.empty();
        }

        public static ClientLocationPredicate.Builder create() {
            return new ClientLocationPredicate.Builder();
        }

        public static ClientLocationPredicate.Builder createBiome(RegistryEntry<Biome> biome) {
            return create().biome(RegistryEntryList.of(biome));
        }

        public static ClientLocationPredicate.Builder createDimension(RegistryKey<World> dimension) {
            return create().dimension(dimension);
        }

        public static ClientLocationPredicate.Builder createY(NumberRange.DoubleRange y) {
            return create().y(y);
        }

        public ClientLocationPredicate.Builder x(NumberRange.DoubleRange x) {
            this.x = x;
            return this;
        }

        public ClientLocationPredicate.Builder y(NumberRange.DoubleRange y) {
            this.y = y;
            return this;
        }

        public ClientLocationPredicate.Builder z(NumberRange.DoubleRange z) {
            this.z = z;
            return this;
        }

        public ClientLocationPredicate.Builder biome(RegistryEntryList<Biome> biome) {
            this.biome = Optional.of(biome);
            return this;
        }

        public ClientLocationPredicate.Builder dimension(RegistryKey<World> dimension) {
            this.dimension = Optional.of(dimension);
            return this;
        }

        public ClientLocationPredicate.Builder light(ClientLightPredicate.Builder light) {
            this.light = Optional.of(light.build());
            return this;
        }

        public ClientLocationPredicate.Builder block(ClientBlockPredicate.Builder block) {
            this.block = Optional.of(block.build());
            return this;
        }

        public ClientLocationPredicate.Builder fluid(ClientFluidPredicate.Builder fluid) {
            this.fluid = Optional.of(fluid.build());
            return this;
        }

        public ClientLocationPredicate.Builder smokey(boolean smokey) {
            this.smokey = Optional.of(smokey);
            return this;
        }

        public ClientLocationPredicate.Builder canSeeSky(boolean canSeeSky) {
            this.canSeeSky = Optional.of(canSeeSky);
            return this;
        }

        public ClientLocationPredicate build() {
            Optional<ClientLocationPredicate.PositionRange> optional = ClientLocationPredicate.PositionRange.create(this.x, this.y, this.z);
            return new ClientLocationPredicate(optional, this.biome, this.dimension, this.smokey, this.light, this.block, this.fluid, this.canSeeSky);
        }
    }
}
