package fuzs.forgeconfigapiport.fabric.impl.client.commands.arguments;

import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.util.StringRepresentable;

public class EnumArgument<T extends Enum<T> & StringRepresentable> extends StringRepresentableArgument<T> {

    private EnumArgument(Class<? extends T> enumClazz) {
        super(StringRepresentable.fromEnum(enumClazz::getEnumConstants), enumClazz::getEnumConstants);
    }

    public static <T extends Enum<T> & StringRepresentable> StringRepresentableArgument<T> enumArgument(Class<? extends T> enumClazz) {
        return new EnumArgument<>(enumClazz);
    }
}
