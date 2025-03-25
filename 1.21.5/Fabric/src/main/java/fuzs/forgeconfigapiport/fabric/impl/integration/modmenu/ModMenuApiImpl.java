package fuzs.forgeconfigapiport.fabric.impl.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import fuzs.forgeconfigapiport.fabric.impl.client.core.ConfigScreenFactoryRegistryImpl;
import net.minecraft.client.gui.screens.Screen;

import java.util.Map;
import java.util.function.UnaryOperator;

public final class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // this is called too early, when no config screens have been able to register yet
        // by returning null we allow the factory to be overridden again when the mod list screen is opened via the provided config screen factories
        return null;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ((ConfigScreenFactoryRegistryImpl) ConfigScreenFactoryRegistry.INSTANCE).getConfigScreenFactories(
                (UnaryOperator<Screen> operator) -> {
                    return operator::apply;
                });
    }
}
