package edu.comp90051.a2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KSparseTurnstile {
    private int k;
    private long[][] w1;
    private long[][] w2;
    private long[][] w3;
    private FastHash[] h;

    public KSparseTurnstile(int k, int d) {
        this.k = k;
//        System.out.println("k " + k);
//        StdOut.println("d " + d);
        this.h = new FastHash[d];
        for (int i = 0; i < d; i++) {
            h[i] = new FastHash(2, FastHash.MERSENNE_PRIME61, 2 * k);
        }

        w1 = new long[d][2 * k];
        w2 = new long[d][2 * k];
        w3 = new long[d][2 * k];
    }

    public void update(int item, int freqDelta) {
        for (int i = 0; i < h.length; i++) {
            int hashVal = (int) h[i].hash(item);
            long[] updatedVal = OneSparseUtil.turnstileUpdate(item, freqDelta, w1[i][hashVal], w2[i][hashVal], w3[i][hashVal]);
            w1[i][hashVal] = updatedVal[0];
            w2[i][hashVal] = updatedVal[1];
            w3[i][hashVal] = updatedVal[2];
        }
    }

    public Integer[][] output() throws NotSparseException, WrongRetrieveException {
        Map<Integer, Integer> freqVec = new HashMap<>();
        for (int i = 0, d = h.length; i < d; i++) {
            for (int j = 0; j < 2 * k; j++) {
                if (OneSparseUtil.ganuglyTest(w1[i][j], w2[i][j], w3[i][j])) {
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

    public static void main(String[] args) throws WrongRetrieveException, NotSparseException {
        final int SUPP_VECTOR_SIZE = 1000;

        int[] eval_metrics = new int[3];     // [0] Successfully Retrieved   [1] Not Sparse  [2] Wrong Retrieve
        for (int i = 0; i < 100; i++) {
            KSparseTurnstile kSparse = new KSparseTurnstile(SUPP_VECTOR_SIZE, (int) Math.round(Math.log(SUPP_VECTOR_SIZE / 0.05)));
            Path data = Path.of("dataset/insert_only_stream.csv");
            try {
                Files.lines(data)
                        .skip(1)
                        .forEach(line -> {
                            String[] record = line.split(",");
                            kSparse.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
                        });
                try {
                    Integer[][] output = kSparse.output();
                    System.out.println(Arrays.deepToString(output));
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
        }

        System.out.println(Arrays.toString(eval_metrics));
    }

}
