package edu.comp90051.l0sampler;

import edu.comp90051.utils.MapUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class L0Turnstile implements L0Sampler {
  private KSparseTurnstile[] K;
  private FastHash h;
  private int n;

  public L0Turnstile(int n, double delta) {
    this.n = n;
    int k = (int) Math.round(12 * Math.log1p(1 / delta));
    int t = k / 2;
    h = new FastHash(t, FastHash.MERSENNE_PRIME61, (long) n * n * n);

    int L = (int) Math.ceil(Math.log1p(n));
    K = new KSparseTurnstile[L];
    for (int i = 0; i < L; i++)
      K[i] = new KSparseTurnstile(k, (int) Math.round(Math.log(k / 0.05)));
  }

  public void update(int item, int freqDelta) {
    int l = 0;
    long hashVal = h.hash(item);
    long scope = (long) n * n * n;
    while (l < K.length && scope >= hashVal) {
      K[l].update(item, freqDelta);
      l++;
      scope /= 2;
    }
  }

  public Integer output() throws FailToRetrieveException {
    Integer[][] output = null;

    // Take the first successful KSparse recovery as output
    for (KSparseTurnstile kSparseTurnstile : K) {
      try {
        output = kSparseTurnstile.output();
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
    final int UNIVERSE_SIZE = 0x100000;
    final int NON_ZERO_ITEM_SIZE = 1000;
    final int SAMPLING_SIZE = 100 * NON_ZERO_ITEM_SIZE;
    final int BATCH_SIZE = 10;

    double t1 = System.currentTimeMillis();
    Map<Object, Integer> counter = new HashMap<>();
    int[] FailCounter = new int[1];
    for (int i = 0; i < SAMPLING_SIZE; i++) {
      L0Turnstile l0Turnstile = new L0Turnstile(UNIVERSE_SIZE, 0.05);
      Path data = Path.of("dataset/Insertion-only_stream.csv");
      try {
        Files.lines(data)
            .skip(1)
            .forEach(line -> {
              String[] record = line.split(",");
              l0Turnstile.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
            });
      } catch (IOException e) {
        e.printStackTrace();
      }

      try {
        int sampledItem = l0Turnstile.output();
        synchronized (counter) {
          counter.put(sampledItem, counter.getOrDefault(sampledItem, 0) + 1);
        }
      } catch (FailToRetrieveException e) {
        synchronized (FailCounter) {
          FailCounter[0]++;
        }
      }
    }

    System.out.println(System.currentTimeMillis() - t1);
    StdOut.println(MapUtil.sortByValue(counter));
    StdOut.println(FailCounter[0]);

//        double t1 = System.currentTimeMillis();
//        Map<Integer, Integer> counter = new HashMap<>();
//        int[] FailCounter = new int[1];
//        for (int j = 0; j < BATCH_SIZE; j++) {
//            ExecutorService exec = Executors.newCachedThreadPool();
//            for (int i = 0; i < 20; i++) {
//                exec.execute(() -> {
//                    L0Dynamic l0Dynamic = new L0Dynamic(UNIVERSE_SIZE, 0.05);
//                    Path data = Path.of("dataset/Insertion-only_stream.csv");
//                    try {
//                        Files.lines(data)
//                                .skip(1)
//                                .forEach(line -> {
//                                    String[] record = line.split(",");
//                                    l0Dynamic.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
//                                });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    try {
//                        int sampledItem = l0Dynamic.output();
//                        synchronized (counter) {
//                            counter.put(sampledItem, counter.getOrDefault(sampledItem, 0) + 1);
//                        }
//                    } catch (FailToRetrieveException e) {
//                        synchronized (FailCounter) {
//                            FailCounter[0]++;
//                        }
//                    }
//                });
//            }
//            exec.shutdown();
//            while (!exec.isTerminated()) {
//                Thread.sleep(500);
//            }
//            StdOut.println("Batch " + (j + 1) + " Finished.");
//        }
//
//        StdOut.println("FailCounter " + FailCounter[0]);
//        StdOut.println(MapUtil.sortByValue(counter));
//
//        System.out.println(System.currentTimeMillis() - t1);
  }
}
