package ronal.barbaren.random.bytes;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ronal.barbaren.random.bytes.utils.ByteRandomTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class NextByteTest {

    ByteRandom random = new BaseByteRandom();

    @Test
    void nextByte() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextByte();

        ByteRandomTestUtils.RandomByteDistribution distribution = ByteRandomTestUtils.getDistribution(bytes);
        double deviation = 0.02;
        boolean isNormal = distribution.isNormal(deviation);
        if (!isNormal)
            distribution.print(deviation);

        Assertions.assertTrue(isNormal);
        Assertions.assertEquals(distribution.getInterest().size(), 256);
    }

    @ParameterizedTest
    @CsvSource({"-20,20", "10,70", "-57,12", "-121,123"})
    void nextByteMinMax(String strMin, String strMax) {
        byte min = Byte.parseByte(strMin);
        byte max = Byte.parseByte(strMax);

        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextByte(min, max);

        ByteRandomTestUtils.RandomByteDistribution distribution = ByteRandomTestUtils.getDistribution(bytes);
        double deviation = 0.02;
        boolean isNormal = distribution.isNormal(deviation);
        if (!isNormal)
            distribution.print(deviation);

        Assertions.assertTrue(isNormal);
        Assertions.assertEquals(distribution.getInterest().size(), max - min);
    }

    @Test
    void randomImage() throws IOException {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 1000; i++)
            for (int j = 0; j < 1000; j++) {
                int r = random.nextByte() + 128;
                int g = random.nextByte() + 128;
                int b = random.nextByte() + 128;
                int color = (r << 16) | (g << 8) | b;
                img.setRGB(i, j, color);
            }
        File tmp = Files.createTempDir();
        ImageIO.write(img, "PNG", new File(tmp + "/image.png"));
    }

}
