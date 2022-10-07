package net.minecraftforge.configured;

import com.mrcrayfish.configured.api.IConfigValue;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ForgeValue<T> implements IConfigValue<T>
{
    public final ForgeConfigSpec.ConfigValue<T> configValue;
    public final ForgeConfigSpec.ValueSpec valueSpec;
    protected final T initialValue;
    protected T value;
    protected Pair<T, T> range;
    protected Component validationHint;

    public ForgeValue(ForgeConfigSpec.ConfigValue<T> configValue, ForgeConfigSpec.ValueSpec valueSpec)
    {
        this.configValue = configValue;
        this.valueSpec = valueSpec;
        this.initialValue = configValue.get();
        this.set(configValue.get());
    }

    @Override
    public T get()
    {
        return this.value;
    }

    @Override
    public void set(T value)
    {
        this.value = value;
    }

    @Override
    public boolean isDefault()
    {
        return Objects.equals(this.get(), this.valueSpec.getDefault());
    }

    @Override
    public boolean isChanged()
    {
        return !Objects.equals(this.get(), this.initialValue);
    }

    @Override
    public void restore()
    {
        this.set(this.getDefault());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getDefault()
    {
        return (T) this.valueSpec.getDefault();
    }

    @Override
    public boolean isValid(T value)
    {
        return this.valueSpec.test(value);
    }

    @Override
    @Nullable
    public Component getComment()
    {
        String rawComment = this.valueSpec.getComment();
        String key = this.getTranslationKey() + ".tooltip";
        if(I18n.exists(key))
        {
            MutableComponent comment = Component.translatable(key);
            if(rawComment != null)
            {
                int rangeIndex = rawComment.indexOf("Range: ");
                int allowedValIndex = rawComment.indexOf("Allowed Values: ");
                if(rangeIndex >= 0 || allowedValIndex >= 0)
                {
                    comment.append(Component.literal(rawComment.substring(Math.max(rangeIndex, allowedValIndex) - 1))); // - 1 to include new line char
                }
            }
            return comment;
        }
        return rawComment != null ? Component.literal(rawComment) : null;
    }

    @Override
    public String getTranslationKey()
    {
        return this.valueSpec.getTranslationKey();
    }

    @Nullable
    @Override
    public Component getValidationHint()
    {
        if(this.validationHint == null)
        {
            this.loadRange();
            if(this.range != null && this.range.getLeft() != null && this.range.getRight() != null)
            {
                this.validationHint = Component.translatable("configured.validator.range_hint", this.range.getLeft().toString(), this.range.getRight().toString());
            }
        }
        return this.validationHint;
    }

    @Override
    public String getName()
    {
        return lastValue(this.configValue.getPath(), "");
    }

    @Override
    public void cleanCache()
    {
        this.configValue.clearCache();
    }

    @Override
    public boolean requiresWorldRestart()
    {
        return this.valueSpec.needsWorldRestart();
    }

    @Override
    public boolean requiresGameRestart()
    {
        return false;
    }

    /**
     * Gets the last element in a list
     *
     * @param list         the list of get the value from
     * @param defaultValue if the list is empty, return this value instead
     * @param <V>          the type of list
     * @return the last element
     */
    public static <V> V lastValue(List<V> list, V defaultValue)
    {
        if(list.size() > 0)
        {
            return list.get(list.size() - 1);
        }
        return defaultValue;
    }

    /**
     * Reflection to get Forge's range of a value
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void loadRange()
    {
        if(this.range == null)
        {
            try
            {
                // Forge Config API Port: use own reflection helper class
                Optional<Object> range = ReflectionHelper.get(ReflectionHelper.getDeclaredField(ForgeConfigSpec.ValueSpec.class, "range"), this.valueSpec);
                if(range.isPresent())
                {
                    Class rangeClass = Class.forName("net.minecraftforge.common.ForgeConfigSpec$Range");
                    Object min = ReflectionHelper.get(ReflectionHelper.getDeclaredField(rangeClass, "min"), range.get()).get();
                    Object max = ReflectionHelper.get(ReflectionHelper.getDeclaredField(rangeClass, "max"), range.get()).get();
                    this.range = Pair.of((T) min, (T) max);
                    return;
                }
            }
            catch(ClassNotFoundException ignored) {}
            this.range = Pair.of(null, null);
        }
    }
}
