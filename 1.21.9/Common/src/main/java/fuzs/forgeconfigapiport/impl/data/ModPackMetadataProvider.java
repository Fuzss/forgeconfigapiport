package fuzs.forgeconfigapiport.impl.data;

import fuzs.forgeconfigapiport.impl.services.CommonAbstractions;
import net.minecraft.DetectedVersion;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

public final class ModPackMetadataProvider extends PackMetadataGenerator {

    public ModPackMetadataProvider(String modId, PackOutput packOutput) {
        this(PackType.SERVER_DATA, modId, packOutput);
    }

    public ModPackMetadataProvider(PackType packType, String modId, PackOutput packOutput) {
        super(packOutput);
        Component component = getPackDescription(modId);
        this.add(PackMetadataSection.forPackType(packType),
                new PackMetadataSection(component,
                        new InclusiveRange<>(DetectedVersion.BUILT_IN.packVersion(packType))));
    }

    public static Component getPackDescription(String modId) {
        return CommonAbstractions.INSTANCE.getDisplayName(modId).map((String name) -> {
            return Component.literal("Resources for " + name);
        }).orElseGet(() -> Component.literal("Resources (" + modId + ")"));
    }
}
