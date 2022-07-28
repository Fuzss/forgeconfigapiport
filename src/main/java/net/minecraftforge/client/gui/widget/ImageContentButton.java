/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ImageContentButton extends ImageButton
{
    public ImageContentButton(final int x, final int y, final int width, final int height, final int u, final int v, final ResourceLocation texture, final OnPress onPress)
    {
        super(x, y, width, height, u, v, texture, onPress);
    }

    public ImageContentButton(final int x, final int y, final int width, final int height, final int u, final int v, final int vOffset, final ResourceLocation texture, final OnPress onPress)
    {
        super(x, y, width, height, u, v, vOffset, texture, onPress);
    }

    public ImageContentButton(final int x, final int y, final int width, final int height, final int u, final int v, final int vOffset, final ResourceLocation texture, final int textureWidth, final int textureHeight, final OnPress onPres)
    {
        super(x, y, width, height, u, v, vOffset, texture, textureWidth, textureHeight, onPres);
    }

    public ImageContentButton(final int x, final int y, final int width, final int height, final int u, final int v, final int vOffset, final ResourceLocation texture, final int textureWidth, final int textureHeight, final OnPress onPress, final Component name)
    {
        super(x, y, width, height, u, v, vOffset, texture, textureWidth, textureHeight, onPress, name);
    }

    public ImageContentButton(final int x, final int y, final int width, final int height, final int u, final int v, final int vOffset, final ResourceLocation texture, final int textureWidth, final int textureHeight, final OnPress onPress, final OnTooltip onTooltip, final Component name)
    {
        super(x, y, width, height, u, v, vOffset, texture, textureWidth, textureHeight, onPress, onTooltip, name);
    }

    @Override
    public void renderButton(final @NotNull PoseStack poseStack, final int mouseX, final int mouseY, final float partialTickTime)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused() && this.isActive());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        super.renderButton(poseStack, mouseX, mouseY, partialTickTime);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    public void setActive(final boolean isActive) {
        if (this.active == isActive)
            return;

        this.active = isActive;
        this.isHovered = false;
        this.setFocused(false);
    }
}
