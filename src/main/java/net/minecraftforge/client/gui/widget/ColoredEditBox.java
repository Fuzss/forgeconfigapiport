package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ColoredEditBox extends EditBox {
    private boolean bordered = true;
    
    public ColoredEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
    }

    public ColoredEditBox(Font font, int i, int j, int k, int l, @Nullable EditBox editBox, Component component) {
        super(font, i, j, k, l, editBox, component);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(poseStack, mouseX, mouseY, partialTicks);
        // Forge does this via an ASM patch, this works alright too though by drawing over the border again
        if (this.isVisible() && this.bordered) {
            int i = this.isFocused() ? getBorderColorFocused() : getBorderColor();
            fill(poseStack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y, i);
            fill(poseStack, this.x - 1, this.y - 1, this.x, this.y + this.height + 1, i);
            fill(poseStack, this.x + this.width, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
            fill(poseStack, this.x - 1, this.y + this.height, this.x + this.width + 1, this.y + this.height + 1, i);
        }
    }

    @Override
    public void setBordered(boolean bordered) {
        super.setBordered(bordered);
        this.bordered = bordered;
    }

    public int getBorderColor() {
        return -6250336;
    }

    public int getBorderColorFocused() {
        return -1;
    }
}
