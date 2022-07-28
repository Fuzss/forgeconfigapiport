package net.minecraftforge.mixin.client;

import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> extends AbstractContainerEventHandler implements Widget, NarratableEntry {

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;mouseClicked(DDI)Z"))
    public void mouseClicked$inject$invoke(double p_93420_, double p_93421_, int p_93422_, CallbackInfoReturnable<Boolean> callback) {
        this.setFocused(null); // Forge: Ensure that we first unselect the current, else Editboxes in a list get stuck in focus mode. MC-254202
    }
}
