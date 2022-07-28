/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.config.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.widget.ColoredEditBox;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.mixin.client.accessor.AbstractWidgetAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Abstract class which describes an input widget that is displayed in a config gui screen.
 * Consider this a mini gui screen that is displayed next to a label of the config entry.
 */
public abstract class ConfigGuiWidget implements ContainerEventHandler, TooltipAccessor, NarratableEntry
{
    @Nullable
    private GuiEventListener focused;
    @Nullable
    private NarratableEntry lastNarratable;
    private boolean dragging;

    @Override
    public boolean isDragging()
    {
        return this.dragging;
    }

    @Override
    public void setDragging(boolean isDragging)
    {
        this.dragging = isDragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused()
    {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener newFocus)
    {
        this.focused = newFocus;
    }

    @Override
    @SuppressWarnings("DuplicatedCode") //Copy of vanila we need it to propagate through the hierarchy
    public void updateNarration(@NotNull NarrationElementOutput output)
    {
        List<? extends NarratableEntry> narratables = this.narratables();
        Screen.NarratableSearchResult searchResult = Screen.findNarratableWidget(narratables, this.lastNarratable);
        if (searchResult != null)
        {
            if (searchResult.priority.isTerminal())
            {
                this.lastNarratable = searchResult.entry;
            }

            if (narratables.size() > 1)
            {
                output.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list",
                        searchResult.index + 1, narratables.size()));
                if (searchResult.priority == NarrationPriority.FOCUSED)
                {
                    output.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
                }
            }

            searchResult.entry.updateNarration(output.nest());
        }
    }

    /**
     * Indicates to scroll list if this widget is currently valid, aka if the value currently entered or represented
     * by the state of this widget is valid for the configuration entry it represents.
     * Should check things like range etc.
     *
     * @return True for a valid entry, false for not.
     */
    public abstract boolean isValid();

    /**
     * The current value for the configuration entry that this widgets value or state represents.
     *
     * @return The current value.
     */
    public abstract Object currentValue();

    /**
     * The narratable entries for this widget.
     * Since this class is not actually gui component, screen etc, we need a way to handle
     * the narration entries for it. So this controls the narration entries for this widget.
     *
     * @return The list of narratable entries.
     */
    public abstract List<? extends NarratableEntry> narratables();

    /**
     * Invoked to render this widget in the config entry.
     * This call also handles positioning the widget since it might be contained in a scrollable object.
     *
     * @param poseStack The rendering pose stack.
     * @param top The top offset to render from.
     * @param left The left offset to render from.
     * @param maxWidth The maximal width that the widget can render to.
     * @param maxHeight The maximal height that the widget can render to.
     * @param mouseX The current mouse x position.
     * @param mouseY The current mouse y position.
     * @param isHovered Whether the mouse is currently hovering over this widget.
     * @param partialTick The partial tick time.
     */
    public abstract void render(final PoseStack poseStack, final int top, final int left, final int maxWidth,
                                final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                                final float partialTick);

    /**
     * The text component that describes the current error state of this widget.
     *
     * @return The error text component.
     * @throws IllegalStateException if the widget is valid.
     */
    public abstract Component getError();

    /**
     * Invoked to reset the state of this widget to the default value.
     * Implementers should use the {@link ValueManager} given to the widget factory to reset the value.
     */
    public abstract void resetToDefault();

    /**
     * Invoked to reset the state of this widget to the initial value when the screen was opened.
     * Implementers should use the {@link ValueManager} given to the widget factory to reset the value.
     */
    public abstract void resetToInitial();

    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link ForgeConfigSpec.BooleanValue}.
     */
    public static class BooleanWidget extends ConfigGuiWidget
    {
        /**
         * The default factory to use for a boolean widget.
         */
        public static ConfigGuiWidgetFactory FACTORY = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.BooleanValue booleanConfigValue)
            {
                return new BooleanWidget(booleanConfigValue, valueSpec, valueManager, spec, name);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".",
                    value.getPath()) + " is not a boolean value!");
        };

        private final CycleButton<Boolean> checkbox;
        private final ForgeConfigSpec.ValueSpec spec;
        private final ValueManager valueManager;

        public BooleanWidget(ForgeConfigSpec.ConfigValue<Boolean> value, final ForgeConfigSpec.ValueSpec spec,
                             ValueManager valueManager, SpecificationData specificationData, Component name)
        {
            this.spec = spec;
            this.valueManager = valueManager;
            this.checkbox = CycleButton.onOffBuilder(value.get())
                                       .displayOnlyValue()
                                       .withCustomNarration(CycleButton::createDefaultNarrationMessage)
                                       .create(0, 0, 44, 20, name, (p_170215_, p_170216_) -> valueManager.setter()
                                                                                                         .accept(p_170216_));

            this.checkbox.active = !specificationData.isSynced();
        }

        @Override
        public boolean isValid()
        {
            return true;
        }

        @Override
        public Object currentValue()
        {
            return this.checkbox.getValue();
        }

        @Override
        public List<? extends NarratableEntry> narratables()
        {
            return Collections.singletonList(this.checkbox);
        }

        @Override
        public void render(final PoseStack poseStack, final int top, final int left, final int maxWidth,
                           final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                           final float partialTick)
        {
            this.checkbox.setWidth(maxWidth);
            ((AbstractWidgetAccessor) this.checkbox).setHeight(maxHeight);
            this.checkbox.x = left;
            this.checkbox.y = top;
            this.checkbox.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public Component getError()
        {
            return this.spec.getError(currentValue());
        }

        @Override
        public void resetToDefault()
        {
            this.checkbox.setValue((Boolean) this.spec.getDefault());
            this.valueManager.setter().accept(this.checkbox.getValue());
        }

        @Override
        public void resetToInitial()
        {
            this.checkbox.setValue((Boolean) this.valueManager.initial().get());
            this.valueManager.setter().accept(this.checkbox.getValue());
        }

        @Override
        public @NotNull List<FormattedCharSequence> getTooltip()
        {
            return this.checkbox.getTooltip();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children()
        {
            return Collections.singletonList(this.checkbox);
        }

        @Override
        public @NotNull NarrationPriority narrationPriority()
        {
            return NarrationPriority.HOVERED;
        }
    }


    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link ForgeConfigSpec.EnumValue}.
     */
    public static class EnumWidget<T extends Enum<T>> extends ConfigGuiWidget
    {

        /**
         * Creates a new factory for the enum widget with the values of the given parameter Z.
         *
         * @return The widget factory for the given parameter Z.
         * @param <Z> The enum type which supplies its values to the selector.
         */
        @SuppressWarnings("unchecked")
        public static <Z extends Enum<Z>> ConfigGuiWidgetFactory getFactory()
        {
            return (value, valueSpec, valueManager, spec, name) ->
            {
                if (value instanceof ForgeConfigSpec.EnumValue enumConfigValue)
                {
                    return new EnumWidget<Z>(enumConfigValue, valueSpec, valueManager, spec, name);
                }

                throw new IllegalArgumentException("The given config value for path: " + String.join(".",
                        value.getPath()) + " is not a boolean value!");
            };
        }

        private final CycleButton<T> enumButton;
        private final ForgeConfigSpec.ValueSpec spec;
        private final ValueManager valueManager;

        public EnumWidget(ForgeConfigSpec.EnumValue<T> value, final ForgeConfigSpec.ValueSpec spec,
                          final ValueManager valueManager, SpecificationData specificationData, Component name)
        {
            this.spec = spec;
            this.valueManager = valueManager;
            this.enumButton = CycleButton.<T>builder(t -> Component.literal(t.name()))
                                         .withValues(List.of(value.getEnumClass().getEnumConstants()))
                                         .displayOnlyValue()
                                         .withCustomNarration(CycleButton::createDefaultNarrationMessage)
                                         .create(0, 0, 44, 20, name, (p_170215_, p_170216_) -> valueManager.setter()
                                                                                                           .accept(p_170216_));

            this.enumButton.active = !specificationData.isSynced();
        }

        @Override
        public boolean isValid()
        {
            return true;
        }

        @Override
        public Object currentValue()
        {
            return this.enumButton.getValue();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void resetToDefault()
        {
            this.enumButton.setValue((T) this.spec.getDefault());
            this.valueManager.setter().accept(this.enumButton.getValue());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void resetToInitial()
        {
            this.enumButton.setValue((T) this.valueManager.initial().get());
            this.valueManager.setter().accept(this.enumButton.getValue());
        }

        @Override
        public Component getError()
        {
            return this.spec.getError(currentValue());
        }

        @Override
        public List<? extends NarratableEntry> narratables()
        {
            return Collections.singletonList(this.enumButton);
        }

        @Override
        public void render(final PoseStack poseStack, final int top, final int left, final int maxWidth,
                           final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                           final float partialTick)
        {
            this.enumButton.setWidth(maxWidth);
            ((AbstractWidgetAccessor) this.enumButton).setHeight(maxHeight);
            this.enumButton.x = left;
            this.enumButton.y = top;
            this.enumButton.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<FormattedCharSequence> getTooltip()
        {
            return this.enumButton.getTooltip();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children()
        {
            return Collections.singletonList(this.enumButton);
        }

        @Override
        public @NotNull NarrationPriority narrationPriority()
        {
            return NarrationPriority.HOVERED;
        }
    }

    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link Number} based {@link ForgeConfigSpec.ConfigValue}.
     * Uses a text box for input.
     *
     * @param <T> The type of the number.
     */
    public static abstract class NumberWidget<T extends Number> extends ConfigGuiWidget
    {

        private final EditBox editBox;
        private final ForgeConfigSpec.ConfigValue<T> value;
        private final ForgeConfigSpec.ValueSpec valueSpec;
        private final ValueManager valueManager;
        private final Function<String, T> parser;

        public NumberWidget(ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec valueSpec,
                            ValueManager valueManager, SpecificationData specificationData, Component name,
                            final Function<String, T> parser)
        {
            this.value = value;
            this.valueSpec = valueSpec;
            this.valueManager = valueManager;
            this.editBox = new ColoredEditBox(Minecraft.getInstance().font, 0, 0, 44, 20, name)
            {
                @Override
                public int getBorderColor()
                {
                    if (isValid())
                    {
                        return super.getBorderColor();
                    }

                    return 0x55FF0000;
                }

                @Override
                public int getBorderColorFocused()
                {
                    if (isValid())
                    {
                        return super.getBorderColor();
                    }

                    return 0xFFFF0000;
                }
            };
            this.parser = parser;
            this.editBox.setValue(valueManager.getter().get().toString());
            this.editBox.setResponder((inputValue) ->
            {
                if (isValid(inputValue))
                {
                    valueManager.setter().accept(parser.apply(inputValue));
                }
            });
            this.editBox.active = !specificationData.isSynced();
            this.editBox.setEditable(this.editBox.active);
        }

        @Override
        public boolean isValid()
        {
            return isValid(this.editBox.getValue());
        }

        @Override
        public Object currentValue()
        {
            try
            {
                return parser.apply(this.editBox.getValue());
            } catch (NumberFormatException e)
            {
                return this.editBox.getValue();
            }
        }

        @Override
        public void resetToDefault()
        {
            this.editBox.setValue(this.value.getDefault().toString());
            this.valueManager.setter().accept(currentValue());
        }

        @Override
        public void resetToInitial()
        {
            this.editBox.setValue(this.valueManager.initial().get().toString());
            this.valueManager.setter().accept(currentValue());
        }

        @Override
        public Component getError()
        {
            return this.valueSpec.getError(currentValue());
        }

        @Override
        public List<? extends NarratableEntry> narratables()
        {
            return Collections.singletonList(this.editBox);
        }

        @Override
        public void render(final PoseStack poseStack, final int top, final int left, final int maxWidth,
                           final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                           final float partialTick)
        {
            this.editBox.setWidth(maxWidth - 4);
            ((AbstractWidgetAccessor) this.editBox).setHeight(maxHeight);
            this.editBox.x = left + 2;
            this.editBox.y = top;
            this.editBox.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<FormattedCharSequence> getTooltip()
        {
            return Collections.emptyList();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children()
        {
            return Collections.singletonList(this.editBox);
        }

        @Override
        public @NotNull NarrationPriority narrationPriority()
        {
            return NarrationPriority.HOVERED;
        }

        private boolean isValid(final String value)
        {
            try
            {
                return isValid(parser.apply(value));
            } catch (NumberFormatException e)
            {
                return false;
            }
        }

        private boolean isValid(T value)
        {
            return this.valueSpec.test(value);
        }
    }

    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link ForgeConfigSpec.IntValue}.
     */
    public static class IntegerWidget extends NumberWidget<Integer>
    {
        public static ConfigGuiWidgetFactory FACTORY = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.IntValue intConfigValue)
            {
                return new IntegerWidget(intConfigValue, valueSpec, valueManager, spec, name);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".",
                    value.getPath()) + " is not a integer value!");
        };

        public IntegerWidget(final ForgeConfigSpec.ConfigValue<Integer> value,
                             final ForgeConfigSpec.ValueSpec valueSpec, final ValueManager valueManager,
                             SpecificationData specificationData, final Component name)
        {
            super(value, valueSpec, valueManager, specificationData, name, Integer::parseInt);
        }
    }

    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link ForgeConfigSpec.LongValue}.
     */
    public static class LongWidget extends NumberWidget<Long>
    {
        public static ConfigGuiWidgetFactory FACTORY = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.LongValue longConfigValue)
            {
                return new LongWidget(longConfigValue, valueSpec, valueManager, spec, name);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".",
                    value.getPath()) + " is not a long value!");
        };

        public LongWidget(final ForgeConfigSpec.ConfigValue<Long> value, final ForgeConfigSpec.ValueSpec valueSpec,
                          final ValueManager valueManager, SpecificationData specificationData, final Component name)
        {
            super(value, valueSpec, valueManager, specificationData, name, Long::parseLong);
        }
    }

    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link ForgeConfigSpec.DoubleValue}.
     */
    public static class DoubleWidget extends NumberWidget<Double>
    {
        public static ConfigGuiWidgetFactory FACTORY = (value, valueSpec, valueManager, spec, name) ->
        {
            if (value instanceof ForgeConfigSpec.DoubleValue doubleConfigValue)
            {
                return new DoubleWidget(doubleConfigValue, valueSpec, valueManager, spec, name);
            }

            throw new IllegalArgumentException("The given config value for path: " + String.join(".",
                    value.getPath()) + " is not a Double value!");
        };

        public DoubleWidget(final ForgeConfigSpec.ConfigValue<Double> value,
                            final ForgeConfigSpec.ValueSpec valueSpec, final ValueManager valueManager,
                            SpecificationData specificationData, final Component name)
        {
            super(value, valueSpec, valueManager, specificationData, name, Double::parseDouble);
        }
    }

    /**
     * Default implementation for a {@link ConfigGuiWidget} for a {@link ForgeConfigSpec.ConfigValue} which references a {@link String}.
     */
    public static class TextWidget extends ConfigGuiWidget
    {
        @SuppressWarnings("unchecked")
        public static ConfigGuiWidgetFactory FACTORY = (value, valueSpec, valueManager, spec, name) ->
        {
            try
            {
                final ForgeConfigSpec.ConfigValue<String> stringValue = (ForgeConfigSpec.ConfigValue<String>) value;
                return new TextWidget(stringValue, valueSpec, valueManager, spec, name);
            } catch (ClassCastException ex)
            {
                throw new IllegalArgumentException("The given config value for path: " + String.join(".",
                        value.getPath()) + " is not a Double value!", ex);
            }
        };


        private final EditBox editBox;
        private final ForgeConfigSpec.ConfigValue<String> value;
        private final ForgeConfigSpec.ValueSpec valueSpec;
        private final ValueManager valueManager;

        public TextWidget(ForgeConfigSpec.ConfigValue<String> value, ForgeConfigSpec.ValueSpec valueSpec,
                          final ValueManager valueManager, SpecificationData specificationData, Component name)
        {
            this.value = value;
            this.valueSpec = valueSpec;
            this.valueManager = valueManager;
            this.editBox = new ColoredEditBox(Minecraft.getInstance().font, 0, 0, 44, 20, name)
            {
                @Override
                public int getBorderColor()
                {
                    if (isValid())
                    {
                        return super.getBorderColor();
                    }

                    return 0x55FF0000;
                }

                @Override
                public int getBorderColorFocused()
                {
                    if (isValid())
                    {
                        return super.getBorderColor();
                    }

                    return 0xFFFF0000;
                }
            };
            this.editBox.setValue(valueManager.getter().get().toString());
            this.editBox.setResponder((inputValue) ->
            {
                if (isValid(inputValue))
                {
                    valueManager.setter().accept(inputValue);
                }
            });
            this.editBox.active = !specificationData.isSynced();
            this.editBox.setEditable(this.editBox.active);
        }

        private boolean isValid(String value)
        {
            return this.valueSpec.test(value);
        }

        @Override
        public boolean isValid()
        {
            return isValid(this.editBox.getValue());
        }

        @Override
        public Object currentValue()
        {
            return this.editBox.getValue();
        }

        @Override
        public void resetToDefault()
        {
            this.editBox.setValue(this.value.getDefault());
            this.valueManager.setter().accept(currentValue());
        }

        @Override
        public void resetToInitial()
        {
            this.editBox.setValue(this.valueManager.initial().get().toString());
            this.valueManager.setter().accept(currentValue());
        }

        @Override
        public Component getError()
        {
            return this.valueSpec.getError(currentValue());
        }

        @Override
        public List<? extends NarratableEntry> narratables()
        {
            return Collections.singletonList(this.editBox);
        }

        @Override
        public void render(final PoseStack poseStack, final int top, final int left, final int maxWidth,
                           final int maxHeight, final int mouseX, final int mouseY, final boolean isHovered,
                           final float partialTick)
        {
            this.editBox.setWidth(maxWidth - 4);
            ((AbstractWidgetAccessor) this.editBox).setHeight(maxHeight);
            this.editBox.x = left + 2;
            this.editBox.y = top;
            this.editBox.render(poseStack, mouseX, mouseY, partialTick);
        }

        @Override
        public @NotNull List<FormattedCharSequence> getTooltip()
        {
            return Collections.emptyList();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children()
        {
            return Collections.singletonList(this.editBox);
        }

        @Override
        public @NotNull NarrationPriority narrationPriority()
        {
            return NarrationPriority.HOVERED;
        }
    }
}
