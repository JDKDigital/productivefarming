package cy.jdkdigital.productivefarming.gametest;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Emits the datapack JSON backing each gametest: a {@code minecraft:function} test instance and an
 * empty {@code minecraft:all_of} environment per registered test, both pointing at the
 * {@code empty_9x9} structure. The function key resolves to the lambda registered by
 * {@link TestFunctions}; the JSON exists so both server and client datapack loads find it.
 */
public class TestEntriesProvider implements DataProvider
{
    private final PackOutput.PathProvider envPath;
    private final PackOutput.PathProvider instancePath;

    public TestEntriesProvider(PackOutput output) {
        this.envPath = output.createPathProvider(PackOutput.Target.DATA_PACK, "test_environment");
        this.instancePath = output.createPathProvider(PackOutput.Target.DATA_PACK, "test_instance");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        // Force <clinit> of ProductiveFarmingGameTests so register(...) populates MAX_TICKS.
        @SuppressWarnings("unused")
        var force = ProductiveFarmingGameTests.MAX_TICKS;

        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ProductiveFarmingGameTests.MAX_TICKS.entrySet()) {
            String name = entry.getKey();
            int maxTicks = entry.getValue();
            Identifier envId = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, name + "_env");
            Identifier instanceId = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, name);

            futures.add(writeJson(cache, envPath.json(envId), environmentJson()));
            futures.add(writeJson(cache, instancePath.json(instanceId), instanceJson(name, envId, maxTicks)));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private static CompletableFuture<?> writeJson(CachedOutput cache, Path path, JsonObject json) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                HashingOutputStream hashed = new HashingOutputStream(Hashing.sha1(), bytes);
                try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(hashed, StandardCharsets.UTF_8))) {
                    writer.setSerializeNulls(false);
                    writer.setIndent("  ");
                    GsonHelper.writeValue(writer, json, null);
                }
                cache.writeIfNeeded(path, bytes.toByteArray(), hashed.hash());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write " + path, e);
            }
        });
    }

    private static JsonObject environmentJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:all_of");
        obj.add("definitions", new JsonArray());
        return obj;
    }

    private static JsonObject instanceJson(String name, Identifier envId, int maxTicks) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:function");
        obj.addProperty("environment", envId.toString());
        obj.addProperty("function", Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, name).toString());
        obj.addProperty("structure", Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "empty_9x9").toString());
        obj.addProperty("max_ticks", maxTicks);
        obj.addProperty("required", true);
        obj.addProperty("setup_ticks", 0);
        return obj;
    }

    @Override
    public String getName() {
        return "Productive Farming GameTest Entries";
    }
}
