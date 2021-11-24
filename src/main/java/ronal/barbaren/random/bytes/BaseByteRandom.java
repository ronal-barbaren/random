package ronal.barbaren.random.bytes;

import java.util.concurrent.atomic.AtomicInteger;

public class BaseByteRandom implements ByteRandom {
    private static final int multiplier = 252149037;
    private static final int addend = 11;

    private final AtomicInteger seed;
    private int offset;

    public BaseByteRandom() {
        seed = new AtomicInteger((int) System.currentTimeMillis());
        offset = (int) System.nanoTime();
    }

    @Override
    public int nextInt() {
        AtomicInteger seed = this.seed;
        int oldSeed;
        int nextSeed;
        do {
            oldSeed = seed.get();
            offset = offsetNanoTime(offset);
            nextSeed = javaUtilRandom(oldSeed) ^ offset;
        } while (!seed.compareAndSet(oldSeed, nextSeed));
        return nextSeed;
    }

    private int offsetNanoTime(int offset) {
        if (offset % 31 == 0)
            offset ^= (int) System.nanoTime();
        return javaUtilRandom(offset);
    }

    private int javaUtilRandom(int value) {
        return (value * multiplier + addend);
    }
}
