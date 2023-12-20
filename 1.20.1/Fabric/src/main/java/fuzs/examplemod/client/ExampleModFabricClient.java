package fuzs.examplemod.client;

import fuzs.examplemod.ExampleMod;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(ExampleMod.MOD_ID, ExampleModClient::new);
    }
}
