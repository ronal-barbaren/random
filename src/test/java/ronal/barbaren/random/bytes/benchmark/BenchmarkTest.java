package ronal.barbaren.random.bytes.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ronal.barbaren.random.bytes.BaseByteRandom;
import ronal.barbaren.random.bytes.ByteRandom;

@State(Scope.Benchmark)
public class BenchmarkTest {

    ByteRandom random = new BaseByteRandom();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void nextByte() {
        random.nextByte();
    }
}
