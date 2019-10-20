package edu.comp90051.a2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class L0Dynamic {
    private KSparse[] K;
    private FastHash h;
    private int n;

    public L0Dynamic(int n, double delta) {
        this.n = n;
        int k = (int) Math.round(12 * Math.log1p(1 / delta));
        int t = k / 2;
        h = new FastHash(t, FastHash.MERSENNE_PRIME61, n * n * n);

        int L = (int) Math.ceil(Math.log1p(n));
        K = new KSparse[L];
        for (int i = 0; i < L; i++)
            K[i] = new KSparse(k, 5);
    }

    public void update(int item, int freqDelta) {
        int l = 0;
        long hashVal = h.hash(item);
        long scope = n * n * n;
        while (l < K.length && scope >= hashVal) {
            K[l].update(item, freqDelta);
            l++;
            scope /= 2;
        }
    }

    public int output() throws FailToRetrieveException {
        Integer[][] output = null;
        for (KSparse kSparse : K) {
            try {
                output = kSparse.output();
                break;
            } catch (WrongRetrieveException | NotSparseException ignored) {
            }
        }

        if (output == null)
            throw new FailToRetrieveException();

        int minItem = 0;
        long minHashVal = Long.MAX_VALUE;
        for (Integer[] record : output) {
            long hashVal = h.hash(record[0]);
            if (hashVal < minHashVal) {
                minItem = record[0];
                minHashVal = hashVal;
            }
        }

        return minItem;
    }

    public static void main(String[] args) throws InterruptedException {
        final int N = 0xFFFF;
        final int NON_ZERO_ITEM_SIZE = 10 * 10 * 10;
        final int SAMPLING_SIZE = 1 * NON_ZERO_ITEM_SIZE;
        final int BATCH_SIZE = 100;

        Map<Object, Integer> counter = new HashMap<>();
        int[] FailCounter = new int[1];

        for (int j = 0; j < BATCH_SIZE; j++) {
            ExecutorService exec = Executors.newCachedThreadPool();
            for (int i = 0; i < SAMPLING_SIZE; i++) {
                exec.execute(() -> {
                    L0Dynamic l0Dynamic = new L0Dynamic(N, 0.05);
                    Path data = Path.of("dataset/L0Dynamic_Mini.csv");
                    try {
                        Files.lines(data)
                                .forEach(line -> {
                                    String[] record = line.split(",");
                                    l0Dynamic.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        int sampledItem = l0Dynamic.output();
                        synchronized (counter) {
                            counter.put(sampledItem, counter.getOrDefault(sampledItem, 0) + 1);
                        }
                    } catch (FailToRetrieveException e) {
                        synchronized (FailCounter) {
                            FailCounter[0]++;
                        }
                    }
                });
            }
            exec.shutdown();
            while (!exec.isTerminated()) {
                Thread.sleep(500);
            }
            StdOut.println("Batch " + (j + 1) + " Finished.");
        }

        StdOut.println(MapUtil.sortByValue(counter));
        StdOut.println("FailCounter " + FailCounter[0]);
    }
}
