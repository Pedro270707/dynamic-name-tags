package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.pedroricardo.DynamicNameTags;
import net.pedroricardo.content.DynamicNameTag;
import net.pedroricardo.content.TextFunctions;
import net.pedroricardo.predicate.ClientEntityPredicate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class DynamicNameTagProvider extends AbstractDynamicNameTagProvider {
    public DynamicNameTagProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateDynamicNameTags(RegistryWrapper.WrapperLookup registryLookup, Map<Identifier, DynamicNameTag> dynamicNameTags) {
        dynamicNameTags.put(Identifier.of(DynamicNameTags.MOD_ID, "jeb"), new DynamicNameTag(Pattern.compile("jeb_"), Optional.of(ClientEntityPredicate.Builder.create().type(EntityType.SHEEP).build()), Text.translatable("dynamicnametags.literal"), List.of(TextFunctions.JEB)));
        dynamicNameTags.put(Identifier.of(DynamicNameTags.MOD_ID, "dinnerbone"), new DynamicNameTag(Pattern.compile("Dinnerbone"), Optional.empty(), Text.translatable("dynamicnametags.dinnerbone"), List.of()));
        dynamicNameTags.put(Identifier.of(DynamicNameTags.MOD_ID, "grumm"), new DynamicNameTag(Pattern.compile("Grumm"), Optional.empty(), Text.translatable("dynamicnametags.grumm"), List.of()));
        dynamicNameTags.put(Identifier.of(DynamicNameTags.MOD_ID, "johnny"), new DynamicNameTag(Pattern.compile("Johnny"), Optional.of(ClientEntityPredicate.Builder.create().type(EntityType.VINDICATOR).build()), Text.translatable("dynamicnametags.literal").formatted(Formatting.RED), List.of()));
        dynamicNameTags.put(Identifier.of(DynamicNameTags.MOD_ID, "toast"), new DynamicNameTag(Pattern.compile("Toast"), Optional.of(ClientEntityPredicate.Builder.create().type(EntityType.RABBIT).build()), Text.translatable("dynamicnametags.literal").formatted(Formatting.GOLD), List.of()));
        NbtCompound evilRabbitCompound = new NbtCompound();
        evilRabbitCompound.putInt("RabbitType", RabbitEntity.RabbitType.EVIL.getId());
        dynamicNameTags.put(Identifier.of(DynamicNameTags.MOD_ID, "the_killer_bunny"), new DynamicNameTag(Pattern.compile("The Killer Bunny"), Optional.of(ClientEntityPredicate.Builder.create().type(EntityType.RABBIT).nbt(new NbtPredicate(evilRabbitCompound)).build()), Text.translatable("dynamicnametags.literal").formatted(Formatting.DARK_RED, Formatting.ITALIC), List.of()));
    }
}
