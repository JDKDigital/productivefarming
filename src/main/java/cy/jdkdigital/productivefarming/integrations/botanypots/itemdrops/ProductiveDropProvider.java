package cy.jdkdigital.productivefarming.integrations.botanypots.itemdrops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.integrations.botanypots.BotanyPotsCompat;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.bookshelf.common.api.util.MathsHelper;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public record ProductiveDropProvider(List<ProductiveDrop> drops) implements ItemDropProvider
{
    public static final MapCodec<ProductiveDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapCodecs.flexibleList(ProductiveDrop.CODEC.codec()).fieldOf("items").forGetter(ProductiveDropProvider::drops)
    ).apply(instance, ProductiveDropProvider::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProductiveDropProvider> STREAM = StreamCodec.of(
        (buffer, value) -> {
            buffer.writeInt(value.drops.size());
            for (ProductiveDrop drop : value.drops) {
                ProductiveDrop.STREAM.encode(buffer, drop);
            }
        },
        (buffer) -> {
            final int size = buffer.readInt();
            final List<ProductiveDrop> drops = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                drops.add(ProductiveDrop.STREAM.decode(buffer));
            }
            return new ProductiveDropProvider(drops);
        }
    );

    @Override
    public void apply(BotanyPotContext botanyPotContext, Level level, Consumer<ItemStack> consumer) {
        this.drops.forEach(drop -> {
            if (MathsHelper.percentChance(drop.chance())) {
                var dropCopy = drop.drop().copy();
                dropCopy.grow(botanyPotContext.getSeedItem().getOrDefault(FarmingDataComponents.YIELD, 0));
                consumer.accept(dropCopy);
            }
        });
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return BotanyPotsCompat.PRODUCTIVE_PROVIDER_TYPE;
    }

    @Override
    public List<ItemStack> getDisplayItems() {
        return this.drops.stream().map(ProductiveDrop::drop).collect(Collectors.toList());
    }

    public record ProductiveDrop(ItemStack drop, float chance) {
        public static final MapCodec<ProductiveDrop> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
