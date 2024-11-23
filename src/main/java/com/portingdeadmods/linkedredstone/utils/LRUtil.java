package com.portingdeadmods.linkedredstone.utils;

import net.minecraft.nbt.CompoundTag;

public final class LRUtil {
    public static CompoundTag readdLong(CompoundTag tag, String key, Object value) {
        if (tag.contains(key)) {
            tag.remove(key);
        }
        tag.putLong(key, (Long) value);
        return tag;
    }
}
