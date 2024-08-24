package net.pedroricardo.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public record ClientFluidPredicate(Optional<RegistryEntryList<Fluid>> fluids, Optional<StatePredicate> state) {
    public static final Codec<ClientFluidPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryCodecs.entryList(RegistryKeys.FLUID).optionalFieldOf("fluids").forGetter(ClientFluidPredicate::fluids), StatePredicate.CODEC.optionalFieldOf("state").forGetter(ClientFluidPredicate::state)).apply(instance, ClientFluidPredicate::new);
    });

    public boolean test(World world, BlockPos pos) {
        if (!world.canSetBlock(pos)) {
            return false;
        } else {
            FluidState fluidState = world.getFluidState(pos);
            if (this.fluids.isPresent() && !fluidState.isIn(this.fluids.get())) {
                return false;
            } else {
                return !this.state.isPresent() || this.state.get().test(fluidState);
            }
        }
    }

    public static class Builder {
        private Optional<RegistryEntryList<Fluid>> tag = Optional.empty();
        private Optional<StatePredicate> state = Optional.empty();

        private Builder() {
        }

        public static ClientFluidPredicate.Builder create() {
            return new ClientFluidPredicate.Builder();
        }

        public ClientFluidPredicate.Builder fluid(Fluid fluid) {
            this.tag = Optional.of(RegistryEntryList.of(fluid.getRegistryEntry()));
            return this;
        }

        public ClientFluidPredicate.Builder tag(RegistryEntryList<Fluid> tag) {
            this.tag = Optional.of(tag);
            return this;
        }

        public ClientFluidPredicate.Builder state(StatePredicate state) {
            this.state = Optional.of(state);
            return this;
        }

        public ClientFluidPredicate build() {
            return new ClientFluidPredicate(this.tag, this.state);
        }
    }
}
