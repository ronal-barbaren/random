package ronal.barbaren.random.bytes;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@State(Scope.Benchmark)
public class ByteRandomTest {

    BaseByteRandom random = new BaseByteRandom();
    Random rnd = new Random();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ByteRandomTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void myRandom() {
        random.nextByte();
    }

    @Test
    void nextPositiveByteIncludeZero() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextPositiveByteIncludeZero();
        log.info("is valid: {}", isNormalBytesDistribution(bytes, 1));
    }

    @Test
    void nextNegativeByteExcludeZero() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteExcludeZero();

        log.info("is valid: {}", isNormalBytesDistribution(bytes, 1));
    }

    @Test
    void nextPositiveByteMax() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextPositiveByteIncludeZero((byte) 20);

        log.info("is valid: {}", isNormalBytesDistribution(bytes, 1));
    }

    @Test
    void nextByteMinMax() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextByte((byte) -20, (byte) 20);

        log.info("is valid: {}", isNormalBytesDistribution(bytes, 1));
    }

    @Test
    void nextNegativeByteIncludeZeroMin() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteIncludeZero((byte) -20);

        log.info("is valid: {}", isNormalBytesDistribution(bytes, 1));
    }

    @Test
    void nextNegativeByteExcludeZeroMin() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextNegativeByteExcludeZero((byte) -20);

        log.info("is valid: {}", isNormalBytesDistribution(bytes, 1));
    }

    @Test
    void randomImage() throws IOException {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 1000; i++)
            for (int j = 0; j < 1000; j++) {
                int r = rnd.nextInt(256);
                int g = rnd.nextInt(256);
                int b = rnd.nextInt(256);
                int color = (r << 16) | (g << 8) | b;
                img.setRGB(i, j, color);
            }
        File tmp = Files.createTempDir();
        ImageIO.write(img, "PNG", new File(tmp + "/image.png"));
    }

    @Test
    void testIsRandomMethod() {
        byte[] bytes = new byte[10_000_000];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = random.nextByte();
        log.info("is valid: {}", isNormalBytesDistribution(bytes, 1));
    }

    boolean isNormalBytesDistribution(byte[] bytes, double percentDelta) {
        Map<Byte, Long> grouped = getGrouped(bytes);
        Map<Byte, Double> interest = getInterest(grouped);
        log.info("interest size: {}", interest.size());
        return isValidByDelta(interest, percentDelta);
    }

    void printInterest(Map<Byte, Double> map, double avgPercent, double percentDelta) {
        for (var a : map.entrySet()) {
            if (Math.abs(a.getValue() - avgPercent) <= percentDelta)
                log.info("byte: {}, percent: {}", a.getKey(), a.getValue());
            else
                log.warn("byte: {}, percent: {}, avg: {}", a.getKey(), a.getValue(), avgPercent);
        }
    }

    boolean isValidByDelta(Map<Byte, Double> interest, double percentDelta) {
        double avgPercent = getAvgPercent(interest);
        printInterest(interest, avgPercent, percentDelta);
        return interest.values().stream().allMatch(a -> Math.abs(a - avgPercent) <= percentDelta);
    }

    double getAvgPercent(Map<Byte, Double> interest) {
        return interest.values().stream().mapToDouble(a -> a).average().getAsDouble();
    }

    double getAvgCount(Map<Byte, Long> grouped) {
        return grouped.values().stream().mapToDouble(Long::doubleValue).average().getAsDouble();
    }

    Map<Byte, Double> getInterest(Map<Byte, Long> grouped) {
        double count = grouped.values().stream().mapToLong(a -> a).sum();
        return grouped.entrySet().stream()
                .map(a -> Map.entry(a.getKey(), 100 * a.getValue() / count))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new));
    }

    Map<Byte, Long> getGrouped(byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(a -> bytes[a])
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));
    }

    int countUnique(byte[] bytes) {
        Set<Byte> set = new HashSet<>();
        for (byte b : bytes)
            set.add(b);
        return set.size();
    }
}
