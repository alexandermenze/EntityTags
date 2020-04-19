package de.lx.entitytags.util;

public final class BitFlags {

    public static byte set(byte bits, byte value, boolean set){
        if(set)
            return set(bits, value);
        else 
            return unset(bits, value);
    }

    public static byte set(byte bits, byte value){
        return (byte)(bits | value);
    }

    public static byte unset(byte bits, byte value){
        return (byte)(bits & ~value);
    }

}