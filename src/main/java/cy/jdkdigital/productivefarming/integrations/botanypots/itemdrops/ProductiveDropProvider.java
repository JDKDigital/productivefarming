package cy.jdkdigital.productivefarming.integrations.botanypots.itemdrops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import net.darkhax.bookshelf.common.api.util.MathsHelper;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record ProductiveDropProvider(List<ProductiveDrop> drops) implements ItemDropProvider
{
    public static final Supplier<ItemDropProviderType<?>> TYPE = ItemDropProviderType.getLazy(ResourceLocation.fromNamespaceAndPath(ProductiveFarming.MODID, "productive_drop"));

    public static final MapCodec<ProductiveDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProductiveDrop.CODEC.listOf().fieldOf("items").forGetter(ProductiveDropProvider::drops)
    ).apply(instance, ProductiveDropProvider::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProductiveDropProvider> STREAM = StreamCodec.of(
        (buffer, value) -> {
            ProductiveDrop.STREAM.apply(ByteBufCodecs.list()).encode(buffer, value.drops);
        },
        (buffer) -> {
            return new ProductiveDropProvider(ProductiveDrop.STREAM.apply(ByteBufCodecs.list()).decode(buffer));
        }
    );

    @Override
    public void apply(BotanyPotContext context, Level level, Consumer<ItemStack> consumer) {
        this.drops.forEach(drop -> {
            if (MathsHelper.percentChance(drop.chance())) {
                var dropCopy = drop.drop().copy();
                dropCopy.grow(context.getSeedItem().getOrDefault(FarmingDataComponents.YIELD, 0));
                if (dropCopy.is(context.getSeedItem().getItem())) {
                    TraitsHelper.copyTraitsToStack(context.getSeedItem(), dropCopy);
                }
                consumer.accept(dropCopy);
            }
        });
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return TYPE.get();
    }

    @Override
    public List<ItemStack> getDisplayItems() {
        return this.drops.stream().map(ProductiveDrop::drop).collect(Collectors.toList());
    }

    public record ProductiveDrop(ItemStack drop, float chance) {
        public static final Codec<ProductiveDrop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(ProductiveDrop::drop),
                Codec.floatRange(0f, 1f).optionalFieldOf("chance", 1f).forGetter(ProductiveDrop::chance)
        ).apply(instance, ProductiveDrop::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProductiveDrop> STREAM = StreamCodec.composite(
                ItemStack.STREAM_CODEC,
                ProductiveDrop::drop,
                ByteBufCodecs.FLOAT,
                ProductiveDrop::chance,
                ProductiveDrop::new
        );
    }
}
