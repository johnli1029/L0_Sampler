package edu.comp90051.l0sampler;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KSparse {
    private int k;
    private long[][] w1;
    private long[][] w2;
    private long[][] w3;
    private long[][] q;
    private long p;
    private FastHash[] h;

    public KSparse(int k, int d) {
        this.k = k;
//        StdOut.println("d " + d);
        this.h = new FastHash[d];
        this.p = (2 * k < FastHash.MERSENNE_PRIME17) ? FastHash.MERSENNE_PRIME17 : FastHash.MERSENNE_PRIME31;
        for (int i = 0; i < d; i++) {
            h[i] = new FastHash(2, p, 2 * k);
        }

        w1 = new long[d][2 * k];
        w2 = new long[d][2 * k];
        w3 = new long[d][2 * k];
        q = new long[d][2 * k];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < 2 * k; j++)
                q[i][j] = StdRandom.uniform(p - 1) + 1;
        }
    }

    public void update(int item, int freqDelta) {
        for (int i = 0; i < h.length; i++) {
            int hashVal = (int) h[i].hash(item);
            long[] updatedVal = OneSparseUtil.update(item, freqDelta, w1[i][hashVal], w2[i][hashVal], w3[i][hashVal], q[i][hashVal], p);
            w1[i][hashVal] = updatedVal[0];
            w2[i][hashVal] = updatedVal[1];
            w3[i][hashVal] = updatedVal[2];
        }
    }

    public Integer[][] output() throws NotSparseException, WrongRetrieveException {
        Map<Integer, Integer> freqVec = new HashMap<>();
        for (int i = 0, d = h.length; i < d; i++) {
            for (int j = 0; j < 2 * k; j++) {
                if (OneSparseUtil.fingerprintTest(w1[i][j], w2[i][j], w3[i][j], q[i][j], p)) {
                    freqVec.put((int) (w2[i][j] / w1[i][j]), (int) w1[i][j]);
                }
                if (freqVec.size() > k) {
                    throw new NotSparseException();
                }
            }
        }

        for (Map.Entry<Integer, Integer> pair : freqVec.entrySet()) {
            this.update(pair.getKey(), -pair.getValue());
        }

        for (int i = 0, d = h.length; i < d; i++) {
            for (int j = 0; j < 2 * k; j++) {
                if (w1[i][j] != 0)
                    throw new WrongRetrieveException();
            }
        }

        Integer[][] result = new Integer[freqVec.size()][2];
        int i = 0;
        for (Map.Entry<Integer, Integer> pair : freqVec.entrySet()) {
            result[i][0] = pair.getKey();
            result[i][1] = pair.getValue();
            i++;
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        final int SUPP_VECTOR_SIZE = 1000;
        final int SAMPLE_SIZE = 10;
        final int BATCH_SIZE = 10;

        int[] eval_metrics = new int[3];     // [0] Successfully Retrieved   [1] Not Sparse  [2] Wrong Retrieve
        long t1 = System.currentTimeMillis();
//        for (int i= 0; i < 10; i++) {
//            KSparse kSparse = new KSparse(SUPP_VECTOR_SIZE, 3);
//            Path data = Path.of("dataset/Dynamic_stream.csv");
//            try {
//                Files.lines(data)
//                        .forEach(line -> {
//                            String[] record = line.split(",");
//                            kSparse.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
//                        });
//                try {
//                    Integer[][] output = kSparse.output();
//                    synchronized (eval_metrics) {
//                        eval_metrics[0]++;
//                    }
//                } catch (NotSparseException e) {
//                    synchronized (eval_metrics) {
//                        eval_metrics[1]++;
//                    }
//                } catch (WrongRetrieveException e) {
//                    synchronized (eval_metrics) {
//                        eval_metrics[2]++;
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        for (int j = 0; j < BATCH_SIZE; j++) {
            ExecutorService exec = Executors.newCachedThreadPool();
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                exec.execute(() -> {
                    KSparse kSparse = new KSparse(3*SUPP_VECTOR_SIZE, 5 );
                    Path data = Path.of("dataset/Dynamic_stream.csv");
                    try {
                        Files.lines(data)
                                .forEach(line -> {
                                    String[] record = line.split(",");
                                    kSparse.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
                                });
                        try {
                            Integer[][] output = kSparse.output();
                            synchronized (eval_metrics) {
                                eval_metrics[0]++;
                            }
                        } catch (NotSparseException e) {
                            synchronized (eval_metrics) {
                                eval_metrics[1]++;
                            }
                        } catch (WrongRetrieveException e) {
                            synchronized (eval_metrics) {
                                eval_metrics[2]++;
                            }
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

            StdOut.println(Arrays.toString(eval_metrics));
        }
        System.out.println(System.currentTimeMillis() - t1);
    }
}
