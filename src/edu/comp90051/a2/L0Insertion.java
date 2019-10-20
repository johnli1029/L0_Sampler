package edu.comp90051.a2;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class L0Insertion implements L0Sampler {
    private T_Wise_Hash h;
    private long m;
    private Object a;

    public L0Insertion(int n, int t) {
        h = new FastHash(t, FastHash.MERSENNE_PRIME31, n);
        this.m = n + 1;
        this.a = null;
    }

    @Override
    public void update(Object item, int delta) {
        long hashVal = h.hash(item);
        if (hashVal < m) {
            this.a = item;
            m = hashVal;
        }
    }

    @Override
    public Object output() {
        return this.a;
    }

    public static void main(String[] args) throws Exception {
        final int STREAM_SIZE = (int) Math.pow(2, 20);
        final int NON_ZERO_ITEM_SIZE = (int) Math.pow(2, 10);
        final int SAMPLING_SIZE = 20 * NON_ZERO_ITEM_SIZE;
//        final int SAMPLING_SIZE = 10;
        final int BATCH_SIZE = 10;
        double t1 = System.currentTimeMillis();

        Map<Object, Integer> counter = new HashMap<>();
//        for (int i = 0; i < 10; i++) {
//            L0Insertion insertionSampler = new L0Insertion(STREAM_SIZE, 2);
//            Path data = Path.of("dataset/insert_only_stream.csv");
//            try {
//                Files.lines(data)
//                        .skip(1)
//                        .forEach(line -> {
//                            String[] record = line.split(",");
//                            insertionSampler.update(record[0], Integer.parseInt(record[1]));
//                        });
//                Object a = insertionSampler.output();
//                synchronized (counter) {
//                    counter.put(a, counter.getOrDefault(a, 0) + 1);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }



        for (int j = 0; j < BATCH_SIZE; j++) {
            ExecutorService exec = Executors.newCachedThreadPool();
            for (int i = 0; i < SAMPLING_SIZE; i++) {
                exec.execute(() -> {
                    L0Insertion insertionSampler = new L0Insertion(STREAM_SIZE, 5);
                    Path data = Path.of("dataset/insert_only_stream.csv");
                    try {
                        Files.lines(data)
                                .skip(1)
                                .forEach(line -> {
                                    String[] record = line.split(",");
                                    insertionSampler.update(record[0], Integer.parseInt(record[1]));
                                });
                        Object a = insertionSampler.output();
                        synchronized (counter) {
                            counter.put(a, counter.getOrDefault(a, 0) + 1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            exec.shutdown();
            while (!exec.isTerminated()) {
                Thread.sleep(500);
            }
            StdOut.println("Batch " + (j + 1) + " Finished");
        }

        System.out.println(System.currentTimeMillis() - t1);
        try (
                PrintWriter output = new PrintWriter(Path.of("./output/l0_insertion_5.csv").toFile())) {
            for (Map.Entry<Object, Integer> entry : counter.entrySet())
                output.println(entry.getKey() + "," + entry.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        StdOut.println(MapUtil.sortByValue(counter));
    }
}
