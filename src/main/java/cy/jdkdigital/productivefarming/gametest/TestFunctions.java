package cy.jdkdigital.productivefarming.gametest;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Registers our test bodies into {@link BuiltInRegistries#TEST_FUNCTION}.
 *
 * <p>In {@code gameTestServer} mode vanilla runs {@code Bootstrap.bootStrap()} (which freezes
 * {@code TEST_FUNCTION}) <i>before</i> the mod constructor, so the normal {@code TestFunctionLoader}
 * hook is a no-op by the time we load. We therefore unfreeze the registry, register each body, and
 * refreeze — the same approach the productivebees harness uses. Called from the
 * {@link ProductiveFarming} constructor.
 */
public final class TestFunctions
{
    private static final Map<String, Consumer<GameTestHelper>> FUNCTIONS = new LinkedHashMap<>();
    private static boolean published;

    private TestFunctions() {}

    public static ResourceKey<Consumer<GameTestHelper>> register(String name, Consumer<GameTestHelper> body) {
        if (published) {
            throw new IllegalStateException("TestFunctions.register called after init() — registry already published.");
        }
        if (FUNCTIONS.put(name, body) != null) {
            throw new IllegalStateException("Duplicate test function registration: " + name);
        }
        return key(name);
    }

    public static ResourceKey<Consumer<GameTestHelper>> key(String name) {
        return ResourceKey.create(Registries.TEST_FUNCTION, Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, name));
    }

    public static Iterable<String> names() {
        return FUNCTIONS.keySet();
    }

    public static void init() {
        if (published) return;
        published = true;

        Registry<Consumer<GameTestHelper>> registry = BuiltInRegistries.TEST_FUNCTION;
        if (!(registry instanceof MappedRegistry<Consumer<GameTestHelper>> mapped)) {
            throw new IllegalStateException("BuiltInRegistries.TEST_FUNCTION is not a MappedRegistry — cannot unfreeze");
        }

        mapped.unfreeze(false);
        try {
            FUNCTIONS.forEach((name, body) -> Registry.register(mapped, key(name), body));
        } finally {
            mapped.freeze();
        }
    }
}
