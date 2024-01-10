package fuzs.forgeconfigapiport.neoforge.api.forge.v4;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.EnumGetMethod;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * A bridge for NeoForge's and Forge's config specs. Used in the implementation of {@link ForgeConfigRegistry}.
 * <p>Exposed in the api to allow for further customization during config registration if necessary.
 */
public record ForwardingConfigSpec<S extends net.neoforged.fml.config.IConfigSpec<S>>(net.minecraftforge.fml.config.IConfigSpec<?> configSpec) implements net.neoforged.fml.config.IConfigSpec<S> {

    @Override
    public S self() {
        return (S) this.configSpec.self();
    }

    @Override
    public void acceptConfig(CommentedConfig data) {
        this.configSpec.acceptConfig(data);
    }

    @Override
    public boolean isCorrecting() {
        return this.configSpec.isCorrecting();
    }

    @Override
    public boolean isCorrect(CommentedConfig commentedFileConfig) {
        return this.configSpec.isCorrect(commentedFileConfig);
    }

    @Override
    public int correct(CommentedConfig commentedFileConfig) {
        return this.configSpec.correct(commentedFileConfig);
    }

    @Override
    public void afterReload() {
        this.configSpec.afterReload();
    }

    @Override
    public <T> T get(String path) {
        return this.configSpec.get(path);
    }

    @Override
    public <T> T get(List<String> path) {
        return this.configSpec.get(path);
    }

    @Override
    public <T> T getRaw(String path) {
        return this.configSpec.getRaw(path);
    }

    @Override
    public <T> T getRaw(List<String> path) {
        return this.configSpec.getRaw(path);
    }

    @Override
    public <T> Optional<T> getOptional(String path) {
        return this.configSpec.getOptional(path);
    }

    @Override
    public <T> Optional<T> getOptional(List<String> path) {
        return this.configSpec.getOptional(path);
    }

    @Override
    public <T> T getOrElse(String path, T defaultValue) {
        return this.configSpec.getOrElse(path, defaultValue);
    }

    @Override
    public <T> T getOrElse(List<String> path, T defaultValue) {
        return this.configSpec.getOrElse(path, defaultValue);
    }

    @Override
    public <T> T getOrElse(List<String> path, Supplier<T> defaultValueSupplier) {
        return this.configSpec.getOrElse(path, defaultValueSupplier);
    }

    @Override
    public <T> T getOrElse(String path, Supplier<T> defaultValueSupplier) {
        return this.configSpec.getOrElse(path, defaultValueSupplier);
    }

    @Override
    public <T extends Enum<T>> T getEnum(String path, Class<T> enumType, EnumGetMethod method) {
        return this.configSpec.getEnum(path, enumType, method);
    }

    @Override
    public <T extends Enum<T>> T getEnum(String path, Class<T> enumType) {
        return this.configSpec.getEnum(path, enumType);
    }

    @Override
    public <T extends Enum<T>> T getEnum(List<String> path, Class<T> enumType, EnumGetMethod method) {
        return this.configSpec.getEnum(path, enumType, method);
    }

    @Override
    public <T extends Enum<T>> T getEnum(List<String> path, Class<T> enumType) {
        return this.configSpec.getEnum(path, enumType);
    }

    @Override
    public <T extends Enum<T>> Optional<T> getOptionalEnum(String path, Class<T> enumType, EnumGetMethod method) {
        return this.configSpec.getOptionalEnum(path, enumType, method);
    }

    @Override
    public <T extends Enum<T>> Optional<T> getOptionalEnum(String path, Class<T> enumType) {
        return this.configSpec.getOptionalEnum(path, enumType);
    }

    @Override
    public <T extends Enum<T>> Optional<T> getOptionalEnum(List<String> path, Class<T> enumType, EnumGetMethod method) {
        return this.configSpec.getOptionalEnum(path, enumType, method);
    }

    @Override
    public <T extends Enum<T>> Optional<T> getOptionalEnum(List<String> path, Class<T> enumType) {
        return this.configSpec.getOptionalEnum(path, enumType);
    }

    @Override
    public <T extends Enum<T>> T getEnumOrElse(String path, T defaultValue, EnumGetMethod method) {
        return this.configSpec.getEnumOrElse(path, defaultValue, method);
    }

    @Override
    public <T extends Enum<T>> T getEnumOrElse(String path, T defaultValue) {
        return this.configSpec.getEnumOrElse(path, defaultValue);
    }

    @Override
    public <T extends Enum<T>> T getEnumOrElse(List<String> path, T defaultValue, EnumGetMethod method) {
        return this.configSpec.getEnumOrElse(path, defaultValue, method);
    }

    @Override
    public <T extends Enum<T>> T getEnumOrElse(List<String> path, T defaultValue) {
        return this.configSpec.getEnumOrElse(path, defaultValue);
    }

    @Override
    public <T extends Enum<T>> T getEnumOrElse(String path, Class<T> enumType, EnumGetMethod method, Supplier<T> defaultValueSupplier) {
        return this.configSpec.getEnumOrElse(path, enumType, method, defaultValueSupplier);
    }

    @Override
    public <T extends Enum<T>> T getEnumOrElse(String path, Class<T> enumType, Supplier<T> defaultValueSupplier) {
        return this.configSpec.getEnumOrElse(path, enumType, defaultValueSupplier);
    }

    @Override
    public <T extends Enum<T>> T getEnumOrElse(List<String> path, Class<T> enumType, EnumGetMethod method, Supplier<T> defaultValueSupplier) {
        return this.configSpec.getEnumOrElse(path, enumType, method, defaultValueSupplier);
    }

    @Override
    public <T extends Enum<T>> T getEnumOrElse(List<String> path, Class<T> enumType, Supplier<T> defaultValueSupplier) {
        return this.configSpec.getEnumOrElse(path, enumType, defaultValueSupplier);
    }

    @Override
    public int getInt(String path) {
        return this.configSpec.getInt(path);
    }

    @Override
    public int getInt(List<String> path) {
        return this.configSpec.getInt(path);
    }

    @Override
    public OptionalInt getOptionalInt(String path) {
        return this.configSpec.getOptionalInt(path);
    }

    @Override
    public OptionalInt getOptionalInt(List<String> path) {
        return this.configSpec.getOptionalInt(path);
    }

    @Override
    public int getIntOrElse(String path, int defaultValue) {
        return this.configSpec.getIntOrElse(path, defaultValue);
    }

    @Override
    public int getIntOrElse(List<String> path, int defaultValue) {
        return this.configSpec.getIntOrElse(path, defaultValue);
    }

    @Override
    public int getIntOrElse(String path, IntSupplier defaultValueSupplier) {
        return this.configSpec.getIntOrElse(path, defaultValueSupplier);
    }

    @Override
    public int getIntOrElse(List<String> path, IntSupplier defaultValueSupplier) {
        return this.configSpec.getIntOrElse(path, defaultValueSupplier);
    }

    @Override
    public long getLong(String path) {
        return this.configSpec.getLong(path);
    }

    @Override
    public long getLong(List<String> path) {
        return this.configSpec.getLong(path);
    }

    @Override
    public OptionalLong getOptionalLong(String path) {
        return this.configSpec.getOptionalLong(path);
    }

    @Override
    public OptionalLong getOptionalLong(List<String> path) {
        return this.configSpec.getOptionalLong(path);
    }

    @Override
    public long getLongOrElse(String path, long defaultValue) {
        return this.configSpec.getLongOrElse(path, defaultValue);
    }

    @Override
    public long getLongOrElse(List<String> path, long defaultValue) {
        return this.configSpec.getLongOrElse(path, defaultValue);
    }

    @Override
    public long getLongOrElse(String path, LongSupplier defaultValueSupplier) {
        return this.configSpec.getLongOrElse(path, defaultValueSupplier);
    }

    @Override
    public long getLongOrElse(List<String> path, LongSupplier defaultValueSupplier) {
        return this.configSpec.getLongOrElse(path, defaultValueSupplier);
    }

    @Override
    public byte getByte(String path) {
        return this.configSpec.getByte(path);
    }

    @Override
    public byte getByte(List<String> path) {
        return this.configSpec.getByte(path);
    }

    @Override
    public byte getByteOrElse(String path, byte defaultValue) {
        return this.configSpec.getByteOrElse(path, defaultValue);
    }

    @Override
    public byte getByteOrElse(List<String> path, byte defaultValue) {
        return this.configSpec.getByteOrElse(path, defaultValue);
    }

    @Override
    public short getShort(String path) {
        return this.configSpec.getShort(path);
    }

    @Override
    public short getShort(List<String> path) {
        return this.configSpec.getShort(path);
    }

    @Override
    public short getShortOrElse(String path, short defaultValue) {
        return this.configSpec.getShortOrElse(path, defaultValue);
    }

    @Override
    public short getShortOrElse(List<String> path, short defaultValue) {
        return this.configSpec.getShortOrElse(path, defaultValue);
    }

    @Override
    public char getChar(String path) {
        return this.configSpec.getChar(path);
    }

    @Override
    public char getChar(List<String> path) {
        return this.configSpec.getChar(path);
    }

    @Override
    public char getCharOrElse(String path, char defaultValue) {
        return this.configSpec.getCharOrElse(path, defaultValue);
    }

    @Override
    public char getCharOrElse(List<String> path, char defaultValue) {
        return this.configSpec.getCharOrElse(path, defaultValue);
    }

    @Override
    public boolean contains(String path) {
        return this.configSpec.contains(path);
    }

    @Override
    public boolean contains(List<String> path) {
        return this.configSpec.contains(path);
    }

    @Override
    public boolean isNull(String path) {
        return this.configSpec.isNull(path);
    }

    @Override
    public boolean isNull(List<String> path) {
        return this.configSpec.isNull(path);
    }

    @Override
    public int size() {
        return this.configSpec.size();
    }

    @Override
    public boolean isEmpty() {
        return this.configSpec.isEmpty();
    }

    @Override
    public Map<String, Object> valueMap() {
        return this.configSpec.valueMap();
    }

    @Override
    public Set<? extends Entry> entrySet() {
        return this.configSpec.entrySet();
    }

    @Override
    public ConfigFormat<?> configFormat() {
        return this.configSpec.configFormat();
    }

    @Override
    public <T> T apply(String path) {
        return this.configSpec.apply(path);
    }

    @Override
    public <T> T apply(List<String> path) {
        return this.configSpec.apply(path);
    }
}
