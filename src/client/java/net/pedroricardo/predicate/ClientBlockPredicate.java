package net.pedroricardo.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public record ClientBlockPredicate(Optional<RegistryEntryList<Block>> blocks, Optional<StatePredicate> state, Optional<NbtPredicate> nbt) {
    public static final Codec<ClientBlockPredicate> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RegistryCodecs.entryList(RegistryKeys.BLOCK).optionalFieldOf("blocks").forGetter(ClientBlockPredicate::blocks), StatePredicate.CODEC.optionalFieldOf("state").forGetter(ClientBlockPredicate::state), NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(ClientBlockPredicate::nbt)).apply(instance, ClientBlockPredicate::new);
    });

    public boolean test(World world, BlockPos pos) {
        if (!world.canSetBlock(pos)) {
            return false;
        } else if (!this.testState(world.getBlockState(pos))) {
            return false;
        } else {
            return !this.nbt.isPresent() || testBlockEntity(world, world.getBlockEntity(pos), this.nbt.get());
        }
    }

    public boolean test(CachedBlockPosition pos) {
        if (!this.testState(pos.getBlockState())) {
            return false;
        } else {
            return !this.nbt.isPresent() || testBlockEntity(pos.getWorld(), pos.getBlockEntity(), this.nbt.get());
        }
    }

    private boolean testState(BlockState state) {
        if (this.blocks.isPresent() && !state.isIn(this.blocks.get())) {
            return false;
        } else {
            return !this.state.isPresent() || this.state.get().test(state);
        }
    }

    private static boolean testBlockEntity(WorldView world, @Nullable BlockEntity blockEntity, NbtPredicate nbtPredicate) {
        return blockEntity != null && nbtPredicate.test(blockEntity.createNbtWithIdentifyingData(world.getRegistryManager()));
    }

    public boolean hasNbt() {
        return this.nbt.isPresent();
    }

    public static class Builder {
        private Optional<RegistryEntryList<Block>> blocks = Optional.empty();
        private Optional<StatePredicate> state = Optional.empty();
        private Optional<NbtPredicate> nbt = Optional.empty();

        private Builder() {
        }

        public static ClientBlockPredicate.Builder create() {
            return new ClientBlockPredicate.Builder();
        }

        public ClientBlockPredicate.Builder blocks(Block... blocks) {
            this.blocks = Optional.of(RegistryEntryList.of(Block::getRegistryEntry, blocks));
            return this;
        }

        public ClientBlockPredicate.Builder blocks(Collection<Block> blocks) {
            this.blocks = Optional.of(RegistryEntryList.of(Block::getRegistryEntry, blocks));
            return this;
        }

        public ClientBlockPredicate.Builder tag(TagKey<Block> tag) {
            this.blocks = Optional.of(Registries.BLOCK.getOrCreateEntryList(tag));
            return this;
        }

        public ClientBlockPredicate.Builder nbt(NbtCompound nbt) {
            this.nbt = Optional.of(new NbtPredicate(nbt));
            return this;
        }

        public ClientBlockPredicate.Builder state(StatePredicate.Builder state) {
            this.state = state.build();
            return this;
        }

        public ClientBlockPredicate build() {
            return new ClientBlockPredicate(this.blocks, this.state, this.nbt);
        }
    }
}
