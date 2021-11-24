package ronal.barbaren.random.bytes;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ronal.barbaren.random.bytes.utils.ByteRandomTestUtils;

@Slf4j
public class ByteRandomTest {

    BaseByteRandom random = new BaseByteRandom();

    @Test
    void nextNegativeByteExcludeZero() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteExcludeZero();

        log.info("is valid: {}", ByteRandomTestUtils.isNormalBytesDistribution(bytes, 1));
    }

    @Test
    void nextNegativeByteIncludeZeroMin() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteIncludeZero((byte) -20);

        log.info("is valid: {}", ByteRandomTestUtils.isNormalBytesDistribution(bytes, 1));
    }

    @Test
    void nextNegativeByteExcludeZeroMin() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteExcludeZero((byte) -20);

        log.info("is valid: {}", ByteRandomTestUtils.isNormalBytesDistribution(bytes, 1));
    }

}
