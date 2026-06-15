package cy.jdkdigital.productivefarming.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CropTraitStore
{
    private final Map<BlockPos, CropTraitState> traits;

    public static final MapCodec<CropTraitStore> CODEC = Codec.list(Entry.CODEC).xmap(
            entries -> {
                Map<BlockPos, CropTraitState> map = new HashMap<>();
                entries.forEach(entry -> map.put(entry.pos(), entry.state()));
                return new CropTraitStore(map);
            },
            store -> store.traits.entrySet().stream().map(e -> new Entry(e.getKey(), e.getValue())).toList()
    ).fieldOf("traits");

    public CropTraitStore() {
        this(new HashMap<>());
    }

    private CropTraitStore(Map<BlockPos, CropTraitState> traits) {
        this.traits = traits;
    }

    @Nullable
    public CropTraitState get(BlockPos pos) {
        return traits.get(pos);
    }

    public void put(BlockPos pos, CropTraitState state) {
        traits.put(pos.immutable(), state);
    }

    public void remove(BlockPos pos) {
        traits.remove(pos);
    }

    public boolean isEmpty() {
        return traits.isEmpty();
    }

    private record Entry(BlockPos pos, CropTraitState state)
    {
        static final Codec<Entry> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos),
                        CropTraitState.CODEC.fieldOf("state").forGetter(Entry::state)
                )
                .apply(builder, Entry::new));
    }
}
