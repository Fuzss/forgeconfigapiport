package fuzs.examplemod;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(ExampleMod.MOD_ID, ExampleMod::new);
    }
}
