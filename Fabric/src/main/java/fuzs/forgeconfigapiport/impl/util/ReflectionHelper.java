package fuzs.forgeconfigapiport.impl.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * helper class for reflection operations, similar to the Forge thing, but we want to use this on Fabric as well
 */
@SuppressWarnings("unchecked")
public class ReflectionHelper {
    /**
     * @param clazz clazz to get field from
     * @param name field name
     * @return the field
     */
    public static Field getDeclaredField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ignored) {
        }
        return null;
    }

    /**
     * @param clazz clazz to get method from
     * @param name method name
     * @param parameterTypes method arguments
     * @return the method
     */
    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException ignored) {
        }
        return null;
    }

    /**
     * @param clazz clazz to get constructor from
     * @param parameterTypes constructor arguments
     * @param <T> class object type
     * @return the constructor
     */
    public static <T> Constructor<T> getDeclaredConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            Constructor<T> constructor = (Constructor<T>) clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException ignored) {
        }
        return null;
    }

    /**
     * @param field field instance, don't have to null check it before, it'll be done here
     * @param instance the object instance
     * @param <T> field type for auto-casting
     * @return the field value
     */
    public static <T> Optional<T> get(@Nullable Field field, Object instance) {
        if (field != null) {
            try {
                return Optional.of((T) field.get(instance));
            } catch (IllegalAccessException ignored) {
            }
        }
        return Optional.empty();
    }

    /**
     * @param method method instance, don't have to null check it before, it'll be done here
     * @param instance the object instance
     * @param args required method args
     * @param <T> return type for auto-casting
     * @return method result or null when void
     */
    public static <T> Optional<T> invoke(@Nullable Method method, Object instance, Object... args) {
        if (method != null) {
            try {
                return Optional.ofNullable((T) method.invoke(instance, args));
            } catch (InvocationTargetException | IllegalAccessException ignored) {
            }
        }
        return Optional.empty();
    }

    /**
     * @param constructor constructor instance, don't have to null check it before, it'll be done here
     * @param args constructor arguments
     * @param <T> instance object type
     * @return new instance
     */
    public static <T> Optional<T> newInstance(@Nullable Constructor<T> constructor, Object... args) {
        if (constructor != null) {
            try {
                return Optional.of(constructor.newInstance(args));
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException ignored) {
            }
        }
        return Optional.empty();
    }
}
