package fuzs.examplemod.client;

import fuzs.examplemod.ExampleMod;
import fuzs.puzzleslib.client.core.ClientFactories;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientFactories.INSTANCE.clientModConstructor(ExampleMod.MOD_ID).accept(new ExampleModClient());
    }
}
