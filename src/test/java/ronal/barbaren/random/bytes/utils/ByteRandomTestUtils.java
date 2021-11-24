package ronal.barbaren.random.bytes.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ByteRandomTestUtils {

    @RequiredArgsConstructor
    @Getter
    public static class RandomByteDistribution {
        final Map<Byte, Long> grouped;
        final Map<Byte, Double> interest;

        public double getAvgPercent() {
            return interest.values().stream().mapToDouble(a -> a).average().getAsDouble();
        }

        public double getAvgCount() {
            return grouped.values().stream().mapToDouble(Long::doubleValue).average().getAsDouble();
        }

        public boolean isValidInterest(Predicate<Double> validator) {
            return interest.values().stream().allMatch(validator);
        }

        public boolean isValidByte(Predicate<Byte> validator) {
            return interest.keySet().stream().allMatch(validator);
        }

        public boolean isNormal(double deltaPercent) {
            double avgPercent = getAvgPercent();
            return isValidInterest(a -> Math.abs(a - avgPercent) <= deltaPercent);
        }

        public void print(double deltaPercent) {
            double avgPercent = getAvgPercent();
            for (var a : interest.entrySet()) {
                double avgAndRealDelta = Math.abs(a.getValue() - avgPercent);
                if (avgAndRealDelta <= deltaPercent)
                    log.info("byte: {}, percent: {}", a.getKey(), a.getValue());
                else
                    log.warn("byte: {}, percent: {}, avg: {}, delta: {}", a.getKey(), a.getValue(), avgPercent, avgAndRealDelta);
            }
        }
    }

    public static RandomByteDistribution getDistribution(byte[] bytes) {
        Map<Byte, Long> grouped = getGrouped(bytes);
        Map<Byte, Double> interest = getInterest(grouped);
        return new RandomByteDistribution(grouped, interest);
    }

    public static boolean isNormalBytesDistribution(byte[] bytes, double percentDelta) {
        Map<Byte, Long> grouped = getGrouped(bytes);
        Map<Byte, Double> interest = getInterest(grouped);
        log.info("interest size: {}", interest.size());
        return isValidByDelta(interest, percentDelta);
    }

    static void printInterest(Map<Byte, Double> map, double avgPercent, double percentDelta) {
        for (var a : map.entrySet()) {
            double avgAndRealDelta = Math.abs(a.getValue() - avgPercent);
            if (avgAndRealDelta <= percentDelta)
                log.info("byte: {}, percent: {}", a.getKey(), a.getValue());
            else
                log.warn("byte: {}, percent: {}, avg: {}, delta: {}", a.getKey(), a.getValue(), avgPercent, avgAndRealDelta);
        }
    }

    static boolean isValidByDelta(Map<Byte, Double> interest, double percentDelta) {
        double avgPercent = getAvgPercent(interest);
        printInterest(interest, avgPercent, percentDelta);
        return interest.values().stream().allMatch(a -> Math.abs(a - avgPercent) <= percentDelta);
    }

    static double getAvgPercent(Map<Byte, Double> interest) {
        return interest.values().stream().mapToDouble(a -> a).average().getAsDouble();
    }

    static Map<Byte, Double> getInterest(Map<Byte, Long> grouped) {
        double count = grouped.values().stream().mapToLong(a -> a).sum();
        return grouped.entrySet().stream()
                .map(a -> Map.entry(a.getKey(), 100 * a.getValue() / count))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, TreeMap::new));
    }

    static Map<Byte, Long> getGrouped(byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(a -> bytes[a])
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));
    }

    static int countUnique(byte[] bytes) {
        Set<Byte> set = new HashSet<>();
        for (byte b : bytes)
            set.add(b);
        return set.size();
    }
}
