package edu.comp90051.l0sampler;

import edu.comp90051.utils.MapUtil;
import edu.princeton.cs.algs4.StdOut;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class L0Insertion implements L0Sampler {
  private TWiseHash h;  // t-wise independent hash function
  private long m;
  private Integer a;      // Sampled item

  /**
   * Initializes a basic l0 Sampler only working with insertion-only (Positive frequency items only) input streams
   *
   * @param n item universe size
   * @param t dependency cardinality of hash function
   */

  public L0Insertion(int n, int t) {
    h = new FastHash(t, FastHash.MERSENNE_PRIME31, n);
    this.m = n + 1;
    this.a = null;
  }

  public L0Insertion(int n) {
    this(n, 2);
  }

  /**
   * Update the internal sampling information when a new stream item comes in
   *
   * @param item  item
   * @param delta item frequency increment
   */
  @Override
  public void update(int item, int delta) {
    long hashVal = h.hash(item);
    if (hashVal < m) {
      this.a = item;
      m = hashVal;
    }
  }

  /**
   * Output the sampled item in the l0 distribution
   *
   * @return Sampled item (Or its ID)
   */
  @Override
  public Integer output() throws IllegalStateException {
    if (a == null)
      throw new IllegalStateException("Empty Input Stream!");

    return this.a;
  }

  /**
   * Reads one command-line integers n: Execute Experiment Epoch for n times,
   * each epoch includes (100 * Support Vector Size) sampling repetitions;
   * print time elapsed and sampling distribution
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) throws InterruptedException {

    final Path data = (args.length > 1) ? Path.of(args[1]) : Path.of("dataset/Insertion-only_stream.csv");

    try (
        BufferedReader reader = new BufferedReader(new FileReader(data.toFile()))
    ) {
      // Data Properties
      String[] dataDescriptions = reader.readLine().split(" ");
      final int UNIVERSE_SIZE = Integer.parseInt(dataDescriptions[0]);
      final int SUPP_VECTOR_SIZE = Integer.parseInt(dataDescriptions[1]);

      // Experiment Environment
      final int BATCH_SIZE = Integer.parseInt(args[0]);
      final int SAMPLING_SIZE = 20 * SUPP_VECTOR_SIZE;

      // Result Evaluation Metrics
      double t1 = System.currentTimeMillis();
      Map<Integer, Integer> distroMap = new HashMap<>();

      for (int i = 1; i <= BATCH_SIZE; i++) {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int j = 0; j < SAMPLING_SIZE; j++) {
          exec.execute(() -> {
            L0Insertion insertionSampler = new L0Insertion(UNIVERSE_SIZE, 2);
            Integer a;
            try {
              a = SamplingTest.runExperiment(insertionSampler, data);
              synchronized (distroMap) {
                distroMap.put(a, distroMap.getOrDefault(a, 0) + 1);
              }
            } catch (IllegalStateException | IOException | FailToRetrieveException e) {
              e.printStackTrace();
            }
          });
        }
        exec.shutdown();
        while (!exec.isTerminated()) {
          Thread.sleep(500);
        }
        StdOut.println("Batch " + i + " Finished");
      }

      System.out.println(System.currentTimeMillis() - t1);
      StdOut.println(MapUtil.sortByValue(distroMap));

//      SamplingTest.printTestResult(distroMap, Path.of("output/l0_insertion"));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
