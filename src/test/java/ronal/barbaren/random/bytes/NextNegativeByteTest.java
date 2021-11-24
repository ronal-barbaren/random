package ronal.barbaren.random.bytes;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ronal.barbaren.random.bytes.utils.ByteRandomTestUtils;

@Slf4j
public class NextNegativeByteTest {

    BaseByteRandom random = new BaseByteRandom();

    @Test
    void nextNegativeByteExcludeZero() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteExcludeZero();

        ByteRandomTestUtils.RandomByteDistribution distribution = ByteRandomTestUtils.getDistribution(bytes);
        double deviation = 0.02;
        boolean isNormal = distribution.isNormal(deviation);
        if (!isNormal)
            distribution.print(deviation);

        Assertions.assertTrue(isNormal);
        Assertions.assertTrue(distribution.isValidByte(a -> a < 0));
        Assertions.assertEquals(distribution.getInterest().size(), 128);
    }

    @ParameterizedTest
    @ValueSource(ints = {-20, -45, -57, -123})
    void nextNegativeByteIncludeZeroMin(int min) {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteIncludeZero((byte) min);

        ByteRandomTestUtils.RandomByteDistribution distribution = ByteRandomTestUtils.getDistribution(bytes);
        double deviation = 0.02;
        boolean isNormal = distribution.isNormal(deviation);
        if (!isNormal)
            distribution.print(deviation);

        Assertions.assertTrue(isNormal);
        Assertions.assertTrue(distribution.isValidByte(a -> a <= 0));
        Assertions.assertEquals(distribution.getInterest().size(), -min);
    }

    @ParameterizedTest
    @ValueSource(ints = {-4, -35, -47, -119})
    void nextNegativeByteExcludeZeroMin(int min) {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteExcludeZero((byte) min);

        ByteRandomTestUtils.RandomByteDistribution distribution = ByteRandomTestUtils.getDistribution(bytes);
        double deviation = 0.02;
        boolean isNormal = distribution.isNormal(deviation);
        if (!isNormal)
            distribution.print(deviation);

        Assertions.assertTrue(isNormal);
        Assertions.assertTrue(distribution.isValidByte(a -> a < 0));
        Assertions.assertEquals(distribution.getInterest().size(), -min -1);
    }

}
