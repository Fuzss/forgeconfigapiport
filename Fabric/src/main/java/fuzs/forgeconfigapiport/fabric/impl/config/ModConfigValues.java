/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package fuzs.forgeconfigapiport.fabric.impl.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.UnaryOperator;

public final class ModConfigValues<T> {
    public static final ModConfigValues<String> DEFAULT_CONFIGS_PATH = new ModConfigValues<>("defaultConfigsPath",
            "defaultconfigs",
            "Path to load default configs from, intended for setting global server configs for newly created worlds, but also works when recreating client and common configs.");
    public static final ModConfigValues<Boolean> DISABLE_CONFIG_WATCHER = new ModConfigValues<>("disableConfigWatcher",
            Boolean.FALSE,
            "Disables File Watcher. Used to automatically update config if its file has been modified.");
    public static final ModConfigValues<Boolean> LOG_UNTRANSLATED_CONFIGURATION_WARNINGS = new ModConfigValues<>(
            "logUntranslatedConfigurationWarnings",
            Boolean.TRUE,
            "A config option mainly for developers. Logs out configuration values that do not have translations when running a client in a development environment.");
    static final List<ModConfigValues<?>> VALUES = List.of(DEFAULT_CONFIGS_PATH,
            DISABLE_CONFIG_WATCHER,
            LOG_UNTRANSLATED_CONFIGURATION_WARNINGS);

    final String entry;
    final T defaultValue;
    final String comment;
    final Class<?> valueType;
    final UnaryOperator<T> entryFunction;

    ModConfigValues(final String entry, final T defaultValue, final String comment) {
        this(entry, defaultValue, comment, UnaryOperator.identity());
    }

    ModConfigValues(final String entry, final T defaultValue, final String comment, UnaryOperator<T> entryFunction) {
        this.entry = entry;
        this.defaultValue = defaultValue;
        this.comment = comment;
        this.valueType = defaultValue.getClass();
        this.entryFunction = entryFunction;
    }

    void buildConfigEntry(ConfigSpec spec, CommentedConfig commentedConfig) {
        if (this.defaultValue instanceof List<?> list) {
            spec.defineList(this.entry, list, e -> e instanceof String);
        } else {
            spec.define(this.entry, this.defaultValue);
        }

        commentedConfig.add(this.entry, this.defaultValue);
        commentedConfig.setComment(this.entry, this.comment);
    }

    T getConfigValue(@Nullable CommentedFileConfig fileConfig) {
        return this.entryFunction.apply(
                fileConfig != null && fileConfig.contains(this.entry) ? fileConfig.get(this.entry) : this.defaultValue);
    }

    public void updateValue(final CommentedFileConfig configData, final T value) {
        configData.set(this.entry, value);
    }
}
