package net.pedroricardo.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record ClientEntityPredicate(Optional<EntityTypePredicate> type, Optional<DistancePredicate> distance, Optional<MovementPredicate> movement, ClientEntityPredicate.PositionalPredicates location, Optional<EntityEffectPredicate> effects, Optional<NbtPredicate> nbt, Optional<EntityFlagsPredicate> flags, Optional<EntityEquipmentPredicate> equipment, /*Optional<EntitySubPredicate> typeSpecific, */Optional<Integer> periodicTick, Optional<ClientEntityPredicate> vehicle, Optional<ClientEntityPredicate> passenger, Optional<ClientEntityPredicate> targetedEntity, Optional<String> team, Optional<SlotsPredicate> slots) {
    public static final Codec<ClientEntityPredicate> CODEC = Codec.recursive("EntityPredicate", (entityPredicateCodec) -> {
        return RecordCodecBuilder.create((instance) -> {
            return instance.group(EntityTypePredicate.CODEC.optionalFieldOf("type").forGetter(ClientEntityPredicate::type), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(ClientEntityPredicate::distance), MovementPredicate.CODEC.optionalFieldOf("movement").forGetter(ClientEntityPredicate::movement), ClientEntityPredicate.PositionalPredicates.CODEC.forGetter(ClientEntityPredicate::location), EntityEffectPredicate.CODEC.optionalFieldOf("effects").forGetter(ClientEntityPredicate::effects), NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(ClientEntityPredicate::nbt), EntityFlagsPredicate.CODEC.optionalFieldOf("flags").forGetter(ClientEntityPredicate::flags), EntityEquipmentPredicate.CODEC.optionalFieldOf("equipment").forGetter(ClientEntityPredicate::equipment), /*EntitySubPredicate.CODEC.optionalFieldOf("type_specific").forGetter(ClientEntityPredicate::typeSpecific), */Codecs.POSITIVE_INT.optionalFieldOf("periodic_tick").forGetter(ClientEntityPredicate::periodicTick), entityPredicateCodec.optionalFieldOf("vehicle").forGetter(ClientEntityPredicate::vehicle), entityPredicateCodec.optionalFieldOf("passenger").forGetter(ClientEntityPredicate::passenger), entityPredicateCodec.optionalFieldOf("targeted_entity").forGetter(ClientEntityPredicate::targetedEntity), Codec.STRING.optionalFieldOf("team").forGetter(ClientEntityPredicate::team), SlotsPredicate.CODEC.optionalFieldOf("slots").forGetter(ClientEntityPredicate::slots)).apply(instance, ClientEntityPredicate::new);
        });
    });

    public boolean test(PlayerEntity player, @Nullable Entity entity) {
        return this.test(player.getWorld(), player.getPos(), entity);
    }

    public boolean test(World world, @Nullable Vec3d pos, @Nullable Entity entity) {
        if (entity == null) {
            return false;
        } else if (this.type.isPresent() && !this.type.get().matches(entity.getType())) {
            return false;
        } else {
            if (pos == null) {
                if (this.distance.isPresent()) {
                    return false;
                }
            } else if (this.distance.isPresent() && !this.distance.get().test(pos.x, pos.y, pos.z, entity.getX(), entity.getY(), entity.getZ())) {
                return false;
            }

            Vec3d vec3d;
            if (this.movement.isPresent()) {
                vec3d = entity.getMovement();
                Vec3d vec3d2 = vec3d.multiply(20.0);
                if (!this.movement.get().test(vec3d2.x, vec3d2.y, vec3d2.z, entity.fallDistance)) {
                    return false;
                }
            }

            if (this.location.located.isPresent() && !this.location.located.get().test(world, entity.getX(), entity.getY(), entity.getZ())) {
                return false;
            } else {
                if (this.location.steppingOn.isPresent()) {
                    vec3d = Vec3d.ofCenter(entity.getSteppingPos());
                    if (!this.location.steppingOn.get().test(world, vec3d.getX(), vec3d.getY(), vec3d.getZ())) {
                        return false;
                    }
                }

                if (this.location.affectsMovement.isPresent()) {
                    vec3d = Vec3d.ofCenter(entity.getVelocityAffectingPos());
                    if (!this.location.affectsMovement.get().test(world, vec3d.getX(), vec3d.getY(), vec3d.getZ())) {
                        return false;
                    }
                }

                if (this.effects.isPresent() && !this.effects.get().test(entity)) {
                    return false;
                } else if (this.flags.isPresent() && !this.flags.get().test(entity)) {
                    return false;
                } else if (this.equipment.isPresent() && !this.equipment.get().test(entity)) {
                    return false;
//                } else if (this.typeSpecific.isPresent() && !((EntitySubPredicate)this.typeSpecific.get()).test(entity, world, pos)) {
//                    return false;
                } else if (this.vehicle.isPresent() && !this.vehicle.get().test(world, pos, entity.getVehicle())) {
                    return false;
                } else if (this.passenger.isPresent() && entity.getPassengerList().stream().noneMatch((entityx) -> {
                    return this.passenger.get().test(world, pos, entityx);
                })) {
                    return false;
                } else if (this.targetedEntity.isPresent() && !this.targetedEntity.get().test(world, pos, entity instanceof MobEntity ? ((MobEntity)entity).getTarget() : null)) {
                    return false;
                } else if (this.periodicTick.isPresent() && entity.age % this.periodicTick.get() != 0) {
                    return false;
                } else {
                    if (this.team.isPresent()) {
                        AbstractTeam abstractTeam = entity.getScoreboardTeam();
                        if (abstractTeam == null || !(this.team.get()).equals(abstractTeam.getName())) {
                            return false;
                        }
                    }

                    if (this.slots.isPresent() && !(this.slots.get()).matches(entity)) {
                        return false;
                    } else return this.nbt.isEmpty() || (this.nbt.get()).test(entity);
                }
            }
        }
    }

    public record PositionalPredicates(Optional<ClientLocationPredicate> located, Optional<ClientLocationPredicate> steppingOn, Optional<ClientLocationPredicate> affectsMovement) {
        public static final MapCodec<ClientEntityPredicate.PositionalPredicates> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ClientLocationPredicate.CODEC.optionalFieldOf("location").forGetter(ClientEntityPredicate.PositionalPredicates::located), ClientLocationPredicate.CODEC.optionalFieldOf("stepping_on").forGetter(ClientEntityPredicate.PositionalPredicates::steppingOn), ClientLocationPredicate.CODEC.optionalFieldOf("movement_affected_by").forGetter(ClientEntityPredicate.PositionalPredicates::affectsMovement)).apply(instance, ClientEntityPredicate.PositionalPredicates::new);
        });
    }

    public static class Builder {
        private Optional<EntityTypePredicate> type = Optional.empty();
        private Optional<DistancePredicate> distance = Optional.empty();
        private Optional<MovementPredicate> movement = Optional.empty();
        private Optional<ClientLocationPredicate> location = Optional.empty();
        private Optional<ClientLocationPredicate> steppingOn = Optional.empty();
        private Optional<ClientLocationPredicate> movementAffectedBy = Optional.empty();
        private Optional<EntityEffectPredicate> effects = Optional.empty();
        private Optional<NbtPredicate> nbt = Optional.empty();
        private Optional<EntityFlagsPredicate> flags = Optional.empty();
        private Optional<EntityEquipmentPredicate> equipment = Optional.empty();
//        private Optional<EntitySubPredicate> typeSpecific = Optional.empty();
        private Optional<Integer> periodicTick = Optional.empty();
        private Optional<ClientEntityPredicate> vehicle = Optional.empty();
        private Optional<ClientEntityPredicate> passenger = Optional.empty();
        private Optional<ClientEntityPredicate> targetedEntity = Optional.empty();
        private Optional<String> team = Optional.empty();
        private Optional<SlotsPredicate> slots = Optional.empty();

        public Builder() {
        }

        public static ClientEntityPredicate.Builder create() {
            return new ClientEntityPredicate.Builder();
        }

        public ClientEntityPredicate.Builder type(EntityType<?> type) {
            this.type = Optional.of(EntityTypePredicate.create(type));
            return this;
        }

        public ClientEntityPredicate.Builder type(TagKey<EntityType<?>> tag) {
            this.type = Optional.of(EntityTypePredicate.create(tag));
            return this;
        }

        public ClientEntityPredicate.Builder type(EntityTypePredicate type) {
            this.type = Optional.of(type);
            return this;
        }

        public ClientEntityPredicate.Builder distance(DistancePredicate distance) {
            this.distance = Optional.of(distance);
            return this;
        }

        public ClientEntityPredicate.Builder movement(MovementPredicate movement) {
            this.movement = Optional.of(movement);
            return this;
        }

        public ClientEntityPredicate.Builder location(ClientLocationPredicate.Builder location) {
            this.location = Optional.of(location.build());
            return this;
        }

        public ClientEntityPredicate.Builder steppingOn(ClientLocationPredicate.Builder steppingOn) {
            this.steppingOn = Optional.of(steppingOn.build());
            return this;
        }

        public ClientEntityPredicate.Builder movementAffectedBy(ClientLocationPredicate.Builder movementAffectedBy) {
            this.movementAffectedBy = Optional.of(movementAffectedBy.build());
            return this;
        }

        public ClientEntityPredicate.Builder effects(EntityEffectPredicate.Builder effects) {
            this.effects = effects.build();
            return this;
        }

        public ClientEntityPredicate.Builder nbt(NbtPredicate nbt) {
            this.nbt = Optional.of(nbt);
            return this;
        }

        public ClientEntityPredicate.Builder flags(EntityFlagsPredicate.Builder flags) {
            this.flags = Optional.of(flags.build());
            return this;
        }

        public ClientEntityPredicate.Builder equipment(EntityEquipmentPredicate.Builder equipment) {
            this.equipment = Optional.of(equipment.build());
            return this;
        }

        public ClientEntityPredicate.Builder equipment(EntityEquipmentPredicate equipment) {
            this.equipment = Optional.of(equipment);
            return this;
        }

//        public ClientEntityPredicate.Builder typeSpecific(EntitySubPredicate typeSpecific) {
//            this.typeSpecific = Optional.of(typeSpecific);
//            return this;
//        }

        public ClientEntityPredicate.Builder periodicTick(int periodicTick) {
            this.periodicTick = Optional.of(periodicTick);
            return this;
        }

        public ClientEntityPredicate.Builder vehicle(ClientEntityPredicate.Builder vehicle) {
            this.vehicle = Optional.of(vehicle.build());
            return this;
        }

        public ClientEntityPredicate.Builder passenger(ClientEntityPredicate.Builder passenger) {
            this.passenger = Optional.of(passenger.build());
            return this;
        }

        public ClientEntityPredicate.Builder targetedEntity(ClientEntityPredicate.Builder targetedEntity) {
            this.targetedEntity = Optional.of(targetedEntity.build());
            return this;
        }

        public ClientEntityPredicate.Builder team(String team) {
            this.team = Optional.of(team);
            return this;
        }

        public ClientEntityPredicate.Builder slots(SlotsPredicate slots) {
            this.slots = Optional.of(slots);
            return this;
        }

        public ClientEntityPredicate build() {
            return new ClientEntityPredicate(this.type, this.distance, this.movement, new ClientEntityPredicate.PositionalPredicates(this.location, this.steppingOn, this.movementAffectedBy), this.effects, this.nbt, this.flags, this.equipment, /*this.typeSpecific, */this.periodicTick, this.vehicle, this.passenger, this.targetedEntity, this.team, this.slots);
        }
    }
}