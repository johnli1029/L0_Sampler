package edu.comp90051.l0sampler;

import edu.princeton.cs.algs4.StdRandom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OneSparseRecovery {
  private long w1;
  private long w2;
  private long w3;
  private long q;
  private long p;

  public OneSparseRecovery(long n) {
    if (Math.log(n) <= 17) {
      q = StdRandom.uniform(FastHash.MERSENNE_PRIME17 - 1) + 1;
      p = FastHash.MERSENNE_PRIME17;
    } else if (Math.log(n) <= 31) {
      q = StdRandom.uniform(FastHash.MERSENNE_PRIME31 - 1) + 1;
      p = FastHash.MERSENNE_PRIME31;
    } else {
      q = StdRandom.uniform(FastHash.MERSENNE_PRIME61 - 1) + 1;
      p = FastHash.MERSENNE_PRIME61;
    }
//        System.out.println(q);
  }

  public void update(int item, int delta) {
    w1 += delta;
    w2 += item * delta;
    w3 = largeMod(w3 + largeMod(largePower(q, item) * delta));
  }

  public Integer output() {
    if (fingerprintTest(w1, w2, w3))
      return (int) (w2 / w1);
    else
      return null;
  }

  public boolean fingerprintTest(long w1, long w2, long w3) {
//        StdOut.println("w1 " + w1);
//        StdOut.println("w2 " + w2);
//        StdOut.println("w3 " + w3);
    return w1 != 0 && w2 % w1 == 0 && largeMod(w1 * largePower(q, (int) (w2 / w1))) == w3;
  }

  private long largePower(long base, int exp) {
    long result = 1;
    while (exp-- > 0) {
      result = largeMod(result * base);
    }

    return result;
  }

  private long largeMod(long x) {
    if (p == FastHash.MERSENNE_PRIME17)
      return FastHash.mod17(x);
    else if (p == FastHash.MERSENNE_PRIME31)
      return FastHash.mod31(x);

    return FastHash.mod61(x);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    final int STREAM_SIZE = (int) Math.pow(2, 16);
    final int N_EXP = 10000;
    long t1 = System.currentTimeMillis();

    Path data = Path.of("dataset/1Sparse_stream_waterfall.csv");
    int dataLen = (int) Files.lines(data).count();
    int[] table = new int[]{0, 0}; // [0] FN: Fail to report sole item     [1] FP:report 1-sparse wrongly

    ExecutorService exec = Executors.newCachedThreadPool();
    for (int i = 0; i < N_EXP; i++) {
      exec.execute(() -> {
        int[] evalMetrics = new int[]{0, 0};
        try {
          OneSparseRecovery oneSparseRecovery = new OneSparseRecovery(STREAM_SIZE);
          int[] count = new int[]{0};
          Files.lines(data)
              .forEach(line -> {
                String[] record = line.split(",");
                oneSparseRecovery.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
                count[0]++;
                if (count[0] == 1) {
                  if (oneSparseRecovery.output() == null)
                    evalMetrics[0]++;
                } else if (count[0] == dataLen) {
                  if (oneSparseRecovery.output() == null)
                    evalMetrics[0]++;
                } else {
                  if (oneSparseRecovery.output() != null) {
                    System.out.println(count[0]);
                    evalMetrics[1]++;
                  }
                }
              });
          synchronized (table) {
            table[0] += evalMetrics[0];
            table[1] += evalMetrics[1];
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
    System.out.println("FN " + table[0]);
    System.out.println("FP " + table[1]);
    System.out.println(System.currentTimeMillis() - t1);


//        Path data = Path.of("dataset/1Sparse_stream_interleave.csv");
//        int dataLen = (int) Files.lines(data).count();
//        int[] table = new int[]{0, 0}; // [0] FN: Fail to report sole item     [1] FP:report 1-sparse wrongly
//
//        ExecutorService exec = Executors.newCachedThreadPool();
//
//        for (int i = 0; i < N_EXP; i++) {
//            exec.execute(() -> {
//                int[] evalMetrics = new int[]{0, 0}; // [0] FN: Fail to report sole item     [1] FP:report 1-sparse wrongly
//                try {
//                    OneSparseRecovery oneSparseRecovery = new OneSparseRecovery(0xFFFF);
//                    int[] count = new int[]{0};
//                    Files.lines(data)
//                            .forEach(line -> {
//                                String[] record = line.split(",");
//                                oneSparseRecovery.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
//                                count[0]++;
//                                if (count[0] == 1) {
//                                    if (oneSparseRecovery.output() == null)
//                                        evalMetrics[0]++;
//                                } else if ((count[0] & 0x1) == 1) {
//                                    if (oneSparseRecovery.output() == null)
//                                        evalMetrics[0]++;
//                                } else {
//                                    if (oneSparseRecovery.output() != null)
//                                        evalMetrics[1]++;
//                                }
//                            });
//                    synchronized (table) {
//                        table[0] += evalMetrics[0];
//                        table[1] += evalMetrics[1];
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//
//        exec.shutdown();
//        while (!exec.isTerminated()) {
//            Thread.sleep(500);
//        }
//        System.out.println("FN " + table[0]);
//        System.out.println("FP " + table[1]);
//        System.out.println(System.currentTimeMillis() - t1);

//
//        OneSparseRecovery oneSparseRecovery = new OneSparseRecovery(FastHash.MERSENNE_PRIME17);
//
//        oneSparseRecovery.update(1, -1);
//        StdOut.println(oneSparseRecovery.output());
//        oneSparseRecovery.update(2, 1);
//        StdOut.println(oneSparseRecovery.output());
//        oneSparseRecovery.update(2, -1);
//        StdOut.println(oneSparseRecovery.output());
  }
}
