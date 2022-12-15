package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

// Forge Config API Port: a checkbox with a custom texture
public class DynamicCheckbox extends Checkbox {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");

    private final boolean showLabel;

    public DynamicCheckbox(int i, int j, int k, int l, Component component, boolean bl) {
        this(i, j, k, l, component, bl, true);
    }

    public DynamicCheckbox(int i, int j, int k, int l, Component component, boolean bl, boolean bl2) {
        super(i, j, k, l, component, bl, bl2);
        this.showLabel = bl2;
    }

    public void renderButton(PoseStack poseStack, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, this.getTexture());
        RenderSystem.enableDepthTest();
        Font font = minecraft.font;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(poseStack, getX(), getY(), this.isFocused() ? 20.0F : 0.0F, this.selected() ? 20.0F : 0.0F, 20, this.height, 64, 64);
        this.renderBg(poseStack, minecraft, i, j);
        if (this.showLabel) {
            drawString(poseStack, font, this.getMessage(), getX() + 24, getY() + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
        }

    }

    @NotNull
    protected ResourceLocation getTexture()
    {
        return TEXTURE;
    }
}
