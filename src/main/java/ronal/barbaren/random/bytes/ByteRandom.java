package ronal.barbaren.random.bytes;

@FunctionalInterface
public interface ByteRandom {
    int nextInt();

    default byte nextByte() {
        return (byte) nextInt();
    }

    default byte nextByte(byte min, byte max) {
        int range = max - min;
        double rndPositiveInt = nextInt() & Integer.MAX_VALUE;
        int rnd = (int) (range * rndPositiveInt / Integer.MAX_VALUE);
        return (byte) (rnd + min);
    }

    default byte nextPositiveByteIncludeZero(byte max) {
        double rndPositiveInt = nextInt() & Integer.MAX_VALUE;
        return (byte) (max * rndPositiveInt / Integer.MAX_VALUE);
    }

    /**
     * min must be negative
     *
     * @param min <= -1 & >= -128
     * @return negative random number excluding min
     */
    default byte nextNegativeByteIncludeZero(byte min) {
        double rndNegativeInt = nextInt() | Integer.MIN_VALUE;
        return (byte) (min * rndNegativeInt / Integer.MIN_VALUE);
    }

    /**
     * min must be negative
     *
     * @param min <= -1 & >= -128
     * @return negative random number excluding min and zero
     */
    default byte nextNegativeByteExcludeZero(byte min) {
        double rndNegativeInt = nextInt() | Integer.MIN_VALUE;
        byte rnd = (byte) ((min + 1) * rndNegativeInt / Integer.MIN_VALUE);
        return (byte) (rnd - 1);
    }

    default byte nextPositiveByteIncludeZero() {
        return (byte) (nextByte() & Byte.MAX_VALUE);
    }

    default byte nextNegativeByteExcludeZero() {
        return (byte) (nextByte() | Byte.MIN_VALUE);
    }
}
