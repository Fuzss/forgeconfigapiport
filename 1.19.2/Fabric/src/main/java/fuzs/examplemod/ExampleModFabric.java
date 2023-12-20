package fuzs.examplemod;

import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(ExampleMod.MOD_ID).accept(new ExampleMod());
    }
}
