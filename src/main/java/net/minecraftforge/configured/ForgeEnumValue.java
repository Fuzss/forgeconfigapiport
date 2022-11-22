/*
 * Copyright (c) MrCrayfish
 * SPDX-License-Identifier: GPLv3
 */

package net.minecraftforge.configured;

import com.mrcrayfish.configured.api.IAllowedEnums;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class ForgeEnumValue<T extends Enum<T>> extends ForgeValue<T> implements IAllowedEnums<T>
{
    public ForgeEnumValue(ForgeConfigSpec.EnumValue<T> configValue, ForgeConfigSpec.ValueSpec valueSpec)
    {
        super(configValue, valueSpec);
    }

    @Override
    public Set<T> getAllowedValues()
    {
        Set<T> allowedValues = new HashSet<>();
        T[] enums = this.initialValue.getDeclaringClass().getEnumConstants();
        for(T e : enums)
        {
            if(this.valueSpec.test(e))
            {
                allowedValues.add(e);
            }
        }
        return allowedValues;
    }
}
