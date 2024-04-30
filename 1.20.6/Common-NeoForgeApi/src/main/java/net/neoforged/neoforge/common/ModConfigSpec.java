/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.common;

import com.electronwill.nightconfig.core.*;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionListener;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import fuzs.forgeconfigapiport.impl.CommonAbstractions;
import fuzs.forgeconfigapiport.impl.ForgeConfigAPIPort;
import net.neoforged.fml.config.IConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction.*;

/*
 * Like {@link com.electronwill.nightconfig.core.ConfigSpec} except in builder format, and extended to accept comments, language keys,
 * and other things Forge configs would find useful.
 */
public class ModConfigSpec extends UnmodifiableConfigWrapper<UnmodifiableConfig> implements IConfigSpec<ModConfigSpec>//TODO: Remove extends and pipe everything through getSpec/getValues?
{
    private final Map<List<String>, String> levelComments;
    private final Map<List<String>, String> levelTranslationKeys;

    private final UnmodifiableConfig values;
    private Config childConfig;

    private boolean isCorrecting = false;

    private static final Logger LOGGER = LogManager.getLogger();

    private ModConfigSpec(UnmodifiableConfig storage, UnmodifiableConfig values, Map<List<String>, String> levelComments, Map<List<String>, String> levelTranslationKeys) {
        super(storage);
        this.values = values;
        this.levelComments = levelComments;
        this.levelTranslationKeys = levelTranslationKeys;
    }

    public String getLevelComment(List<String> path) {
        return this.levelComments.get(path);
    }

    public String getLevelTranslationKey(List<String> path) {
        return this.levelTranslationKeys.get(path);
    }

    public void setConfig(CommentedConfig config) {
        this.childConfig = config;
        if (config != null && !this.isCorrect(config)) {
            String configName = config instanceof FileConfig ?
                    ((FileConfig) config).getNioPath().toString() :
                    config.toString();
            // Forge Config API Port: replace with SLF4J logger
            ForgeConfigAPIPort.LOGGER.warn("Configuration file {} is not correct. Correcting", configName);
            this.correct(config, (action, path, incorrectValue, correctedValue) -> {
                // Forge Config API Port: replace with SLF4J logger
                ForgeConfigAPIPort.LOGGER.warn("Incorrect key {} was corrected from {} to its default, {}. {}",
                        DOT_JOINER.join(path),
                        incorrectValue,
                        correctedValue,
                        incorrectValue == correctedValue ? "This seems to be an error." : ""
                );
            }, (action, path, incorrectValue, correctedValue) -> {
                // Forge Config API Port: replace with SLF4J logger
                ForgeConfigAPIPort.LOGGER.debug(
                        "The comment on key {} does not match the spec. This may create a backup.",
                        DOT_JOINER.join(path)
                );
            });

            if (config instanceof FileConfig) {
                ((FileConfig) config).save();
            }
        }
        this.afterReload();
    }

    @Override
    public void acceptConfig(final CommentedConfig data) {
        this.setConfig(data);
    }

    public boolean isCorrecting() {
        return this.isCorrecting;
    }

    public boolean isLoaded() {
        return this.childConfig != null;
    }

    public UnmodifiableConfig getSpec() {
        return this.config;
    }

    public UnmodifiableConfig getValues() {
        return this.values;
    }

    public void afterReload() {
        this.resetCaches(this.getValues().valueMap().values());
    }

    private void resetCaches(final Iterable<Object> configValues) {
        configValues.forEach(value -> {
            if (value instanceof ConfigValue<?> configValue) {
                configValue.clearCache();
            } else if (value instanceof Config innerConfig) {
                this.resetCaches(innerConfig.valueMap().values());
            }
        });
    }

    public void save() {
        Preconditions.checkNotNull(this.childConfig, "Cannot save config value without assigned Config object present");
        if (this.childConfig instanceof FileConfig) {
            ((FileConfig) this.childConfig).save();
        }
    }

    public synchronized boolean isCorrect(CommentedConfig config) {
        LinkedList<String> parentPath = new LinkedList<>();
        // Forge Config API Port: add default values map read from 'defaultconfigs' directory as method parameter
        return this.correct(this.config,
                config,
                null,
                parentPath,
                Collections.unmodifiableList(parentPath),
                (a, b, c, d) -> {
                },
                null,
                true
        ) == 0;
    }

    public int correct(CommentedConfig config) {
        // Forge Config API Port: add proper listeners for corrections
        return this.correct(config, (action, path, incorrectValue, correctedValue) -> {
            ForgeConfigAPIPort.LOGGER.warn("Incorrect key {} was corrected from {} to its default, {}. {}",
                    DOT_JOINER.join(path),
                    incorrectValue,
                    correctedValue,
                    incorrectValue == correctedValue ? "This seems to be an error." : ""
            );
        }, (action, path, incorrectValue, correctedValue) -> {
            ForgeConfigAPIPort.LOGGER.debug("The comment on key {} does not match the spec. This may create a backup.",
                    DOT_JOINER.join(path)
            );
        });
    }

    public synchronized int correct(CommentedConfig config, CorrectionListener listener) {
        return this.correct(config, listener, null);
    }

    public synchronized int correct(CommentedConfig config, CorrectionListener listener, CorrectionListener commentListener) {
        LinkedList<String> parentPath = new LinkedList<>(); //Linked list for fast add/removes
        int ret = -1;
        try {
            this.isCorrecting = true;
            // Forge Config API Port: add default values map read from 'defaultconfigs' directory as method parameter
            final Map<String, Object> defaultMap;
            if (config instanceof FileConfig fileConfig) {
                defaultMap = CommonAbstractions.getDefaultMap(fileConfig);
            } else {
                defaultMap = null;
            }
            ret = this.correct(this.config,
                    config,
                    defaultMap,
                    parentPath,
                    Collections.unmodifiableList(parentPath),
                    listener,
                    commentListener,
                    false
            );
        } finally {
            this.isCorrecting = false;
        }
        return ret;
    }

    // Forge Config API Port: add default values map read from 'defaultconfigs' directory as method parameter
    private int correct(UnmodifiableConfig spec, CommentedConfig config, @Nullable Map<String, Object> defaultMap, LinkedList<String> parentPath, List<String> parentPathUnmodifiable, CorrectionListener listener, CorrectionListener commentListener, boolean dryRun) {
        int count = 0;

        Map<String, Object> specMap = spec.valueMap();
        Map<String, Object> configMap = config.valueMap();

        for (Map.Entry<String, Object> specEntry : specMap.entrySet()) {
            final String key = specEntry.getKey();
            final Object specValue = specEntry.getValue();
            final Object configValue = configMap.get(key);
            final CorrectionAction action = configValue == null ? ADD : REPLACE;

            parentPath.addLast(key);

            if (specValue instanceof Config) {
                if (configValue instanceof CommentedConfig) {
                    // Forge Config API Port: add default values map read from 'defaultconfigs' directory as method parameter
                    count += this.correct((Config) specValue,
                            (CommentedConfig) configValue,
                            defaultMap != null && defaultMap.get(key) instanceof Config defaultConfig ?
                                    defaultConfig.valueMap() :
                                    null,
                            parentPath,
                            parentPathUnmodifiable,
                            listener,
                            commentListener,
                            dryRun
                    );
                    if (count > 0 && dryRun) return count;
                } else if (dryRun) {
                    return 1;
                } else {
                    CommentedConfig newValue = config.createSubConfig();
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    count++;
                    // Forge Config API Port: add default values map read from 'defaultconfigs' directory as method parameter
                    count += this.correct((Config) specValue,
                            newValue,
                            defaultMap != null && defaultMap.get(key) instanceof Config defaultConfig ?
                                    defaultConfig.valueMap() :
                                    null,
                            parentPath,
                            parentPathUnmodifiable,
                            listener,
                            commentListener,
                            dryRun
                    );
                }

                String newComment = this.levelComments.get(parentPath);
                String oldComment = config.getComment(key);
                if (!this.stringsMatchIgnoringNewlines(oldComment, newComment)) {
                    if (commentListener != null) {
                        commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, newComment);
                    }

                    if (dryRun) return 1;

                    config.setComment(key, newComment);
                }
            } else {
                ValueSpec valueSpec = (ValueSpec) specValue;
                if (!valueSpec.test(configValue)) {
                    if (dryRun) return 1;

                    // Forge Config API Port: try to get the value from the default config first before falling back to the built-in default config value
                    Object newValue;
                    if (defaultMap != null && defaultMap.containsKey(key)) {
                        if (valueSpec.getRange() != null) {
                            newValue = valueSpec.getRange().correct(configValue, defaultMap.get(key));
                        } else {
                            newValue = defaultMap.get(key);
                        }
                        if (!valueSpec.test(newValue)) {
                            newValue = valueSpec.correct(configValue);
                        }
                    } else {
                        newValue = valueSpec.correct(configValue);
                    }

                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    count++;
                }
                String oldComment = config.getComment(key);
                if (!this.stringsMatchIgnoringNewlines(oldComment, valueSpec.getComment())) {
                    if (commentListener != null) {
                        commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, valueSpec.getComment());
                    }

                    if (dryRun) return 1;

                    config.setComment(key, valueSpec.getComment());
                }
            }

            parentPath.removeLast();
        }

        // Second step: removes the unspecified values
        for (Iterator<Map.Entry<String, Object>> ittr = configMap.entrySet().iterator(); ittr.hasNext(); ) {
            Map.Entry<String, Object> entry = ittr.next();
            if (!specMap.containsKey(entry.getKey())) {
                if (dryRun) return 1;

                ittr.remove();
                parentPath.addLast(entry.getKey());
                listener.onCorrect(REMOVE, parentPathUnmodifiable, entry.getValue(), null);
                parentPath.removeLast();
                count++;
            }
        }
        return count;
    }

    private boolean stringsMatchIgnoringNewlines(@Nullable Object obj1, @Nullable Object obj2) {
        if (obj1 instanceof String string1 && obj2 instanceof String string2) {
            if (string1.length() > 0 && string2.length() > 0) {
                return string1.replaceAll("\r\n", "\n").equals(string2.replaceAll("\r\n", "\n"));

            }
        }
        // Fallback for when we're not given Strings, or one of them is empty
        return Objects.equals(obj1, obj2);
    }

    public static class Builder {
        private final Config storage = Config.of(LinkedHashMap::new,
                InMemoryFormat.withUniversalSupport()
        ); // Use LinkedHashMap for consistent ordering
        private BuilderContext context = new BuilderContext();
        private final Map<List<String>, String> levelComments = new HashMap<>();
        private final Map<List<String>, String> levelTranslationKeys = new HashMap<>();
        private final List<String> currentPath = new ArrayList<>();
        private final List<ConfigValue<?>> values = new ArrayList<>();

        //Object
        public <T> ConfigValue<T> define(String path, T defaultValue) {
            return this.define(split(path), defaultValue);
        }

        public <T> ConfigValue<T> define(List<String> path, T defaultValue) {
            return this.define(path,
                    defaultValue,
                    o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass())
            );
        }

        public <T> ConfigValue<T> define(String path, T defaultValue, Predicate<Object> validator) {
            return this.define(split(path), defaultValue, validator);
        }

        public <T> ConfigValue<T> define(List<String> path, T defaultValue, Predicate<Object> validator) {
            Objects.requireNonNull(defaultValue, "Default value can not be null");
            return this.define(path, () -> defaultValue, validator);
        }

        public <T> ConfigValue<T> define(String path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
            return this.define(split(path), defaultSupplier, validator);
        }

        public <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
            return this.define(path, defaultSupplier, validator, Object.class);
        }

        public <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz) {
            this.context.setClazz(clazz);
            return this.define(path, new ValueSpec(defaultSupplier, validator, this.context, path), defaultSupplier);
        }

        public <T> ConfigValue<T> define(List<String> path, ValueSpec value, Supplier<T> defaultSupplier) { // This is the root where everything at the end of the day ends up.
            if (!this.currentPath.isEmpty()) {
                List<String> tmp = new ArrayList<>(this.currentPath.size() + path.size());
                tmp.addAll(this.currentPath);
                tmp.addAll(path);
                path = tmp;
            }
            this.storage.set(path, value);
            this.context = new BuilderContext();
            return new ConfigValue<>(this, path, defaultSupplier);
        }

        public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(String path, V defaultValue, V min, V max, Class<V> clazz) {
            return this.defineInRange(split(path), defaultValue, min, max, clazz);
        }

        public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, V defaultValue, V min, V max, Class<V> clazz) {
            return this.defineInRange(path, (Supplier<V>) () -> defaultValue, min, max, clazz);
        }

        public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(String path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
            return this.defineInRange(split(path), defaultSupplier, min, max, clazz);
        }

        public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
            Range<V> range = new Range<>(clazz, min, max);
            this.context.setRange(range);
            this.comment("Range: " + range);
            if (min.compareTo(max) > 0) throw new IllegalArgumentException("Range min most be less then max.");
            return this.define(path, defaultSupplier, range);
        }

        public <T> ConfigValue<T> defineInList(String path, T defaultValue, Collection<? extends T> acceptableValues) {
            return this.defineInList(split(path), defaultValue, acceptableValues);
        }

        public <T> ConfigValue<T> defineInList(String path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
            return this.defineInList(split(path), defaultSupplier, acceptableValues);
        }

        public <T> ConfigValue<T> defineInList(List<String> path, T defaultValue, Collection<? extends T> acceptableValues) {
            return this.defineInList(path, () -> defaultValue, acceptableValues);
        }

        public <T> ConfigValue<T> defineInList(List<String> path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
            // Forge Config API Port: add null check, some immutable collection implementations (like List::of) throw a NullPointerException here
            return this.define(path, defaultSupplier, o -> o != null && acceptableValues.contains(o));
        }

        public <T> ConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return this.defineList(split(path), defaultValue, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineList(String path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
            return this.defineList(split(path), defaultSupplier, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineList(List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return this.defineList(path, () -> defaultValue, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineList(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
            this.context.setClazz(List.class);
            return this.define(path,
                    new ValueSpec(defaultSupplier,
                            x -> x instanceof List && ((List<?>) x).stream().allMatch(elementValidator),
                            this.context,
                            path
                    ) {
                        @Override
                        public Object correct(Object value) {
                            if (value == null || !(value instanceof List) || ((List<?>) value).isEmpty()) {
                                // Forge Config API Port: replace with SLF4J logger
                                ForgeConfigAPIPort.LOGGER.debug(
                                        "List on key {} is deemed to need correction. It is null, not a list, or an empty list. Modders, consider defineListAllowEmpty?",
                                        path.get(path.size() - 1)
                                );
                                return this.getDefault();
                            }
                            List<?> list = Lists.newArrayList((List<?>) value);
                            list.removeIf(elementValidator.negate());
                            if (list.isEmpty()) {
                                // Forge Config API Port: replace with SLF4J logger
                                ForgeConfigAPIPort.LOGGER.debug(
                                        "List on key {} is deemed to need correction. It failed validation.",
                                        path.get(path.size() - 1)
                                );
                                return this.getDefault();
                            }
                            return list;
                        }
                    },
                    defaultSupplier
            );
        }

        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return this.defineListAllowEmpty(split(path), defaultValue, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(String path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
            return this.defineListAllowEmpty(split(path), defaultSupplier, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return this.defineListAllowEmpty(path, () -> defaultValue, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(List<String> path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
            this.context.setClazz(List.class);
            return this.define(path,
                    new ValueSpec(defaultSupplier,
                            x -> x instanceof List && ((List<?>) x).stream().allMatch(elementValidator),
                            this.context,
                            path
                    ) {
                        @Override
                        public Object correct(Object value) {
                            if (value == null || !(value instanceof List)) {
                                // Forge Config API Port: replace with SLF4J logger
                                ForgeConfigAPIPort.LOGGER.debug(
                                        "List on key {} is deemed to need correction, as it is null or not a list.",
                                        path.get(path.size() - 1)
                                );
                                return this.getDefault();
                            }
                            List<?> list = Lists.newArrayList((List<?>) value);
                            list.removeIf(elementValidator.negate());
                            if (list.isEmpty()) {
                                // Forge Config API Port: replace with SLF4J logger
                                ForgeConfigAPIPort.LOGGER.debug(
                                        "List on key {} is deemed to need correction. It failed validation.",
                                        path.get(path.size() - 1)
                                );
                                return this.getDefault();
                            }
                            return list;
                        }
                    },
                    defaultSupplier
            );
        }

        //Enum
        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue) {
            return this.defineEnum(split(path), defaultValue);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter) {
            return this.defineEnum(split(path), defaultValue, converter);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue) {
            return this.defineEnum(path, defaultValue, defaultValue.getDeclaringClass().getEnumConstants());
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter) {
            return this.defineEnum(path, defaultValue, converter, defaultValue.getDeclaringClass().getEnumConstants());
        }

        @SuppressWarnings("unchecked")
        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, V... acceptableValues) {
            return this.defineEnum(split(path), defaultValue, acceptableValues);
        }

        @SuppressWarnings("unchecked")
        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
            return this.defineEnum(split(path), defaultValue, converter, acceptableValues);
        }

        @SuppressWarnings("unchecked")
        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, V... acceptableValues) {
            return this.defineEnum(path, defaultValue, Arrays.asList(acceptableValues));
        }

        @SuppressWarnings("unchecked")
        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
            return this.defineEnum(path, defaultValue, converter, Arrays.asList(acceptableValues));
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, Collection<V> acceptableValues) {
            return this.defineEnum(split(path), defaultValue, acceptableValues);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
            return this.defineEnum(split(path), defaultValue, converter, acceptableValues);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, Collection<V> acceptableValues) {
            return this.defineEnum(path, defaultValue, EnumGetMethod.NAME_IGNORECASE, acceptableValues);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
            return this.defineEnum(path, defaultValue, converter, obj -> {
                if (obj instanceof Enum) {
                    return acceptableValues.contains(obj);
                }
                if (obj == null) {
                    return false;
                }
                try {
                    return acceptableValues.contains(converter.get(obj, defaultValue.getDeclaringClass()));
                } catch (IllegalArgumentException | ClassCastException e) {
                    return false;
                }
            });
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, Predicate<Object> validator) {
            return this.defineEnum(split(path), defaultValue, validator);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
            return this.defineEnum(split(path), defaultValue, converter, validator);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, Predicate<Object> validator) {
            return this.defineEnum(path, () -> defaultValue, validator, defaultValue.getDeclaringClass());
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
            return this.defineEnum(path, () -> defaultValue, converter, validator, defaultValue.getDeclaringClass());
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
            return this.defineEnum(split(path), defaultSupplier, validator, clazz);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
            return this.defineEnum(split(path), defaultSupplier, converter, validator, clazz);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
            return this.defineEnum(path, defaultSupplier, EnumGetMethod.NAME_IGNORECASE, validator, clazz);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
            this.context.setClazz(clazz);
            V[] allowedValues = clazz.getEnumConstants();
            this.comment("Allowed Values: " +
                    Arrays.stream(allowedValues).filter(validator).map(Enum::name).collect(Collectors.joining(", ")));
            return new EnumValue<V>(this,
                    this.define(path, new ValueSpec(defaultSupplier, validator, this.context, path), defaultSupplier)
                            .getPath(),
                    defaultSupplier,
                    converter,
                    clazz
            );
        }

        //boolean
        public BooleanValue define(String path, boolean defaultValue) {
            return this.define(split(path), defaultValue);
        }

        public BooleanValue define(List<String> path, boolean defaultValue) {
            return this.define(path, () -> defaultValue);
        }

        public BooleanValue define(String path, Supplier<Boolean> defaultSupplier) {
            return this.define(split(path), defaultSupplier);
        }

        public BooleanValue define(List<String> path, Supplier<Boolean> defaultSupplier) {
            return new BooleanValue(this, this.define(path, defaultSupplier, o -> {
                if (o instanceof String) {
                    return ((String) o).equalsIgnoreCase("true") || ((String) o).equalsIgnoreCase("false");
                }
                return o instanceof Boolean;
            }, Boolean.class).getPath(), defaultSupplier);
        }

        //Double
        public DoubleValue defineInRange(String path, double defaultValue, double min, double max) {
            return this.defineInRange(split(path), defaultValue, min, max);
        }

        public DoubleValue defineInRange(List<String> path, double defaultValue, double min, double max) {
            return this.defineInRange(path, () -> defaultValue, min, max);
        }

        public DoubleValue defineInRange(String path, Supplier<Double> defaultSupplier, double min, double max) {
            return this.defineInRange(split(path), defaultSupplier, min, max);
        }

        public DoubleValue defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max) {
            return new DoubleValue(this,
                    this.defineInRange(path, defaultSupplier, min, max, Double.class).getPath(),
                    defaultSupplier
            );
        }

        //Ints
        public IntValue defineInRange(String path, int defaultValue, int min, int max) {
            return this.defineInRange(split(path), defaultValue, min, max);
        }

        public IntValue defineInRange(List<String> path, int defaultValue, int min, int max) {
            return this.defineInRange(path, () -> defaultValue, min, max);
        }

        public IntValue defineInRange(String path, Supplier<Integer> defaultSupplier, int min, int max) {
            return this.defineInRange(split(path), defaultSupplier, min, max);
        }

        public IntValue defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max) {
            return new IntValue(this,
                    this.defineInRange(path, defaultSupplier, min, max, Integer.class).getPath(),
                    defaultSupplier
            );
        }

        //Longs
        public LongValue defineInRange(String path, long defaultValue, long min, long max) {
            return this.defineInRange(split(path), defaultValue, min, max);
        }

        public LongValue defineInRange(List<String> path, long defaultValue, long min, long max) {
            return this.defineInRange(path, () -> defaultValue, min, max);
        }

        public LongValue defineInRange(String path, Supplier<Long> defaultSupplier, long min, long max) {
            return this.defineInRange(split(path), defaultSupplier, min, max);
        }

        public LongValue defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max) {
            return new LongValue(this,
                    this.defineInRange(path, defaultSupplier, min, max, Long.class).getPath(),
                    defaultSupplier
            );
        }

        public Builder comment(String comment) {
            this.context.addComment(comment);
            return this;
        }

        public Builder comment(String... comment) {
            // Iterate list first, to throw meaningful errors
            // Don't add any comments until we make sure there is no nulls
            for (int i = 0; i < comment.length; i++)
                Preconditions.checkNotNull(comment[i], "Comment string at " + i + " is null.");

            for (String s : comment)
                this.context.addComment(s);

            return this;
        }

        public Builder translation(String translationKey) {
            this.context.setTranslationKey(translationKey);
            return this;
        }

        public Builder worldRestart() {
            this.context.worldRestart();
            return this;
        }

        public Builder push(String path) {
            return this.push(split(path));
        }

        public Builder push(List<String> path) {
            this.currentPath.addAll(path);
            if (this.context.hasComment()) {
                this.levelComments.put(new ArrayList<>(this.currentPath), this.context.buildComment(path));
                this.context.clearComment(); // Set to empty
            }
            if (this.context.getTranslationKey() != null) {
                this.levelTranslationKeys.put(new ArrayList<String>(this.currentPath),
                        this.context.getTranslationKey()
                );
                this.context.setTranslationKey(null);
            }
            this.context.ensureEmpty();
            return this;
        }

        public Builder pop() {
            return this.pop(1);
        }

        public Builder pop(int count) {
            if (count > this.currentPath.size()) {
                throw new IllegalArgumentException(
                        "Attempted to pop " + count + " elements when we only had: " + this.currentPath);
            }
            for (int x = 0; x < count; x++)
                this.currentPath.remove(this.currentPath.size() - 1);
            return this;
        }

        public <T> Pair<T, ModConfigSpec> configure(Function<Builder, T> consumer) {
            T o = consumer.apply(this);
            return Pair.of(o, this.build());
        }

        public ModConfigSpec build() {
            this.context.ensureEmpty();
            Config valueCfg = Config.of(Config.getDefaultMapCreator(true, true),
                    InMemoryFormat.withSupport(ConfigValue.class::isAssignableFrom)
            );
            this.values.forEach(v -> valueCfg.set(v.getPath(), v));

            ModConfigSpec ret = new ModConfigSpec(this.storage,
                    valueCfg,
                    this.levelComments,
                    this.levelTranslationKeys
            );
            this.values.forEach(v -> v.spec = ret);
            return ret;
        }

        public interface BuilderConsumer {
            void accept(Builder builder);
        }
    }

    private static class BuilderContext {
        private final List<String> comment = new LinkedList<>();
        private String langKey;
        private Range<?> range;
        private boolean worldRestart = false;
        private Class<?> clazz;

        public void addComment(String value) {
            // Don't use `validate` because it throws IllegalStateException, not NullPointerException
            Preconditions.checkNotNull(value, "Passed in null value for comment");

            this.comment.add(value);
        }

        public void clearComment() {
            this.comment.clear();
        }

        public boolean hasComment() {
            return this.comment.size() > 0;
        }

        public String buildComment() {
            return this.buildComment(List.of("unknown", "unknown"));
        }

        public String buildComment(final List<String> path) {
            if (this.comment.stream().allMatch(String::isBlank)) {
                if (!CommonAbstractions.isDevelopmentEnvironment()) {
                    ForgeConfigAPIPort.LOGGER.warn(
                            "Detected a comment that is all whitespace for config option {}, which causes obscure bugs in NeoForge's config system and will cause a crash in the future. Please report this to the mod author.",
                            DOT_JOINER.join(path)
                    );
                } else {
                    throw new IllegalStateException("Can not build comment for config option " + DOT_JOINER.join(path) +
                            " as it comprises entirely of blank lines/whitespace. This is not allowed as it causes a \"constantly correcting config\" bug with NightConfig in NeoForge's config system.");
                }

                return "A developer of this mod has defined this config option with a blank comment, which causes obscure bugs in NeoForge's config system and will cause a crash in the future. Please report this to the mod author.";
            }

            return LINE_JOINER.join(this.comment);
        }

        public void setTranslationKey(String value) {
            this.langKey = value;
        }

        public String getTranslationKey() {
            return this.langKey;
        }

        public <V extends Comparable<? super V>> void setRange(Range<V> value) {
            this.range = value;
            this.setClazz(value.getClazz());
        }

        @SuppressWarnings("unchecked")
        public <V extends Comparable<? super V>> Range<V> getRange() {
            return (Range<V>) this.range;
        }

        public void worldRestart() {
            this.worldRestart = true;
        }

        public boolean needsWorldRestart() {
            return this.worldRestart;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public void ensureEmpty() {
            this.validate(this.hasComment(), "Non-empty comment when empty expected");
            this.validate(this.langKey, "Non-null translation key when null expected");
            this.validate(this.range, "Non-null range when null expected");
            this.validate(this.worldRestart, "Dangeling world restart value set to true");
        }

        private void validate(Object value, String message) {
            if (value != null) throw new IllegalStateException(message);
        }

        private void validate(boolean value, String message) {
            if (value) throw new IllegalStateException(message);
        }
    }

    @SuppressWarnings("unused")
    public static class Range<V extends Comparable<? super V>> implements Predicate<Object> {
        private final Class<? extends V> clazz;
        private final V min;
        private final V max;

        private Range(Class<V> clazz, V min, V max) {
            this.clazz = clazz;
            this.min = min;
            this.max = max;
        }

        public Class<? extends V> getClazz() {
            return this.clazz;
        }

        public V getMin() {
            return this.min;
        }

        public V getMax() {
            return this.max;
        }

        private boolean isNumber(Object other) {
            return Number.class.isAssignableFrom(this.clazz) && other instanceof Number;
        }

        @Override
        public boolean test(Object t) {
            if (this.isNumber(t)) {
                Number n = (Number) t;
                boolean result = ((Number) this.min).doubleValue() <= n.doubleValue() &&
                        n.doubleValue() <= ((Number) this.max).doubleValue();
                if (!result) {
                    // Forge Config API Port: replace with SLF4J logger
                    ForgeConfigAPIPort.LOGGER.debug("Range value {} is not within its bounds {}-{}",
                            n.doubleValue(),
                            ((Number) this.min).doubleValue(),
                            ((Number) this.max).doubleValue()
                    );
                }
                return result;
            }
            if (!this.clazz.isInstance(t)) return false;
            V c = this.clazz.cast(t);

            boolean result = c.compareTo(this.min) >= 0 && c.compareTo(this.max) <= 0;
            if (!result) {
                // Forge Config API Port: replace with SLF4J logger
                ForgeConfigAPIPort.LOGGER.debug("Range value {} is not within its bounds {}-{}", c, this.min, this.max);
            }
            return result;
        }

        public Object correct(Object value, Object def) {
            if (this.isNumber(value)) {
                Number n = (Number) value;
                return n.doubleValue() < ((Number) this.min).doubleValue() ?
                        this.min :
                        n.doubleValue() > ((Number) this.max).doubleValue() ? this.max : value;
            }
            if (!this.clazz.isInstance(value)) return def;
            V c = this.clazz.cast(value);
            return c.compareTo(this.min) < 0 ? this.min : c.compareTo(this.max) > 0 ? this.max : value;
        }

        @Override
        public String toString() {
            if (this.clazz == Integer.class) {
                if (this.max.equals(Integer.MAX_VALUE)) {
                    return "> " + this.min;
                } else if (this.min.equals(Integer.MIN_VALUE)) {
                    return "< " + this.max;
                }
            } // TODO add more special cases?
            return this.min + " ~ " + this.max;
        }
    }

    public static class ValueSpec {
        private final String comment;
        private final String langKey;
        private final Range<?> range;
        private final boolean worldRestart;
        private final Class<?> clazz;
        private final Supplier<?> supplier;
        private final Predicate<Object> validator;

        private ValueSpec(Supplier<?> supplier, Predicate<Object> validator, BuilderContext context, List<String> path) {
            Objects.requireNonNull(supplier, "Default supplier can not be null");
            Objects.requireNonNull(validator, "Validator can not be null");

            this.comment = context.hasComment() ? context.buildComment(path) : null;
            this.langKey = context.getTranslationKey();
            this.range = context.getRange();
            this.worldRestart = context.needsWorldRestart();
            this.clazz = context.getClazz();
            this.supplier = supplier;
            this.validator = validator;
        }

        public String getComment() {
            return this.comment;
        }

        public String getTranslationKey() {
            return this.langKey;
        }

        @SuppressWarnings("unchecked")
        public <V extends Comparable<? super V>> Range<V> getRange() {
            return (Range<V>) this.range;
        }

        public boolean needsWorldRestart() {
            return this.worldRestart;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public boolean test(Object value) {
            return this.validator.test(value);
        }

        public Object correct(Object value) {
            return this.range == null ? this.getDefault() : this.range.correct(value, this.getDefault());
        }

        public Object getDefault() {
            return this.supplier.get();
        }
    }

    public static class ConfigValue<T> implements Supplier<T> {
        private static final boolean USE_CACHES = true;

        private final Builder parent;
        private final List<String> path;
        private final Supplier<T> defaultSupplier;

        private T cachedValue = null;

        private ModConfigSpec spec;

        ConfigValue(Builder parent, List<String> path, Supplier<T> defaultSupplier) {
            this.parent = parent;
            this.path = path;
            this.defaultSupplier = defaultSupplier;
            this.parent.values.add(this);
        }

        public List<String> getPath() {
            return Lists.newArrayList(this.path);
        }

        /**
         * Returns the actual value for the configuration setting, throwing if the config has not yet been loaded.
         *
         * @return the actual value for the setting
         *
         * @throws NullPointerException  if the {@link ModConfigSpec config spec} object that will contain this has not
         *                               yet been built
         * @throws IllegalStateException if the associated config has not yet been loaded
         */
        @Override
        public T get() {
            Preconditions.checkNotNull(this.spec, "Cannot get config value before spec is built");
            // TODO: Remove this dev-time check so this errors out on both production and dev
            // This is dev-time-only in 1.19.x, to avoid breaking already published mods while forcing devs to fix their errors
            if (CommonAbstractions.isDevelopmentEnvironment()) {
                // When the above if-check is removed, change message to "Cannot get config value before config is loaded"
                Preconditions.checkState(this.spec.childConfig != null, """
                                                                        Cannot get config value before config is loaded.
                                                                        This error is currently only thrown in the development environment, to avoid breaking published mods.
                                                                        In a future version, this will also throw in the production environment.
                                                                        """);
            }

            if (this.spec.childConfig == null) return this.defaultSupplier.get();

            if (USE_CACHES && this.cachedValue == null) {
                this.cachedValue = this.getRaw(this.spec.childConfig, this.path, this.defaultSupplier);
            } else if (!USE_CACHES) return this.getRaw(this.spec.childConfig, this.path, this.defaultSupplier);

            return this.cachedValue;
        }

        protected T getRaw(Config config, List<String> path, Supplier<T> defaultSupplier) {
            return config.getOrElse(path, defaultSupplier);
        }

        /**
         * {@return the default value for the configuration setting}
         */
        public T getDefault() {
            return this.defaultSupplier.get();
        }

        public Builder next() {
            return this.parent;
        }

        public void save() {
            Preconditions.checkNotNull(this.spec, "Cannot save config value before spec is built");
            Preconditions.checkNotNull(this.spec.childConfig,
                    "Cannot save config value without assigned Config object present"
            );
            this.spec.save();
        }

        public void set(T value) {
            Preconditions.checkNotNull(this.spec, "Cannot set config value before spec is built");
            Preconditions.checkNotNull(this.spec.childConfig,
                    "Cannot set config value without assigned Config object present"
            );
            this.spec.childConfig.set(this.path, value);
            this.cachedValue = value;
        }

        public void clearCache() {
            this.cachedValue = null;
        }
    }

    public static class BooleanValue extends ConfigValue<Boolean> implements BooleanSupplier {
        BooleanValue(Builder parent, List<String> path, Supplier<Boolean> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        @Override
        public boolean getAsBoolean() {
            return this.get();
        }

        public boolean isTrue() {
            return this.getAsBoolean();
        }

        public boolean isFalse() {
            return !this.getAsBoolean();
        }
    }

    public static class IntValue extends ConfigValue<Integer> implements IntSupplier {
        IntValue(Builder parent, List<String> path, Supplier<Integer> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        @Override
        protected Integer getRaw(Config config, List<String> path, Supplier<Integer> defaultSupplier) {
            return config.getIntOrElse(path, () -> defaultSupplier.get());
        }

        @Override
        public int getAsInt() {
            return this.get();
        }
    }

    public static class LongValue extends ConfigValue<Long> implements LongSupplier {
        LongValue(Builder parent, List<String> path, Supplier<Long> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        @Override
        protected Long getRaw(Config config, List<String> path, Supplier<Long> defaultSupplier) {
            return config.getLongOrElse(path, () -> defaultSupplier.get());
        }

        @Override
        public long getAsLong() {
            return this.get();
        }
    }

    public static class DoubleValue extends ConfigValue<Double> implements DoubleSupplier {
        DoubleValue(Builder parent, List<String> path, Supplier<Double> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        @Override
        protected Double getRaw(Config config, List<String> path, Supplier<Double> defaultSupplier) {
            Number n = config.get(path);
            return n == null ? defaultSupplier.get() : n.doubleValue();
        }

        @Override
        public double getAsDouble() {
            return this.get();
        }
    }

    public static class EnumValue<T extends Enum<T>> extends ConfigValue<T> {
        private final EnumGetMethod converter;
        private final Class<T> clazz;

        EnumValue(Builder parent, List<String> path, Supplier<T> defaultSupplier, EnumGetMethod converter, Class<T> clazz) {
            super(parent, path, defaultSupplier);
            this.converter = converter;
            this.clazz = clazz;
        }

        @Override
        protected T getRaw(Config config, List<String> path, Supplier<T> defaultSupplier) {
            return config.getEnumOrElse(path, this.clazz, this.converter, defaultSupplier);
        }
    }

    private static final Joiner LINE_JOINER = Joiner.on("\n");
    private static final Joiner DOT_JOINER = Joiner.on(".");
    private static final Splitter DOT_SPLITTER = Splitter.on(".");

    private static List<String> split(String path) {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }
}
