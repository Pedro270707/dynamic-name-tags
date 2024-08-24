package net.pedroricardo.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.pedroricardo.predicate.ClientEntityPredicate;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public record DynamicNameTag(Pattern pattern, Optional<ClientEntityPredicate> predicate, Text text, List<TextFunction> textFunctions) {
    public static final Codec<DynamicNameTag> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codecs.REGULAR_EXPRESSION.fieldOf("matches").forGetter(DynamicNameTag::pattern), ClientEntityPredicate.CODEC.optionalFieldOf("predicate").forGetter(DynamicNameTag::predicate), TextCodecs.CODEC.fieldOf("text").forGetter(DynamicNameTag::text), TextFunctionRegistry.REGISTRY.getCodec().listOf().fieldOf("text_functions").forGetter(DynamicNameTag::textFunctions)).apply(instance, DynamicNameTag::new));
}
