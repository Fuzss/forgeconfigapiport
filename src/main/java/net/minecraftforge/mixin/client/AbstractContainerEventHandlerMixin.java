package net.minecraftforge.mixin.client;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(AbstractContainerEventHandler.class)
public abstract class AbstractContainerEventHandlerMixin extends GuiComponent implements ContainerEventHandler {
    @Shadow
    @Nullable
    private GuiEventListener focused;

    @Inject(method = "setFocused", at = @At("HEAD"))
    public void setFocused$inject$head(@Nullable GuiEventListener p_94677_, CallbackInfo callback) {
        if (this.focused != null && this.focused != p_94677_) {  // Forge: Ensure that we first unselect the current, else Editboxes in a list get stuck in focus mode. MC-254202
            if (this.focused instanceof ContainerEventHandler containerEventHandler) {
                containerEventHandler.setFocused(null);
                return;
            }

            this.focused.changeFocus(false);
        }
    }
}
