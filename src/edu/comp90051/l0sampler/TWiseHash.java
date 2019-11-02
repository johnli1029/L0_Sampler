package edu.comp90051.l0sampler;

import edu.comp90051.utils.MapUtil;

import java.util.HashMap;
import java.util.Map;

public class TWiseHash {
  protected long p;     // A large prime larger than input universe size as well as value range
  protected long[] a;   // Hashing parameters with size t
  protected long range; // hash value range

  /**
   * Constructing a hash function drawn from t-wise independent hash family
   *
   * @param t     the coordinate of hash family's independence
   * @param p     the modding prime
   * @param range the value range (modded range) of hash function
   */
  public TWiseHash(int t, long p, long range) {
    this.p = p;
    this.range = range;
    a = new long[t];
    a[0] = StdRandom.uniform(p); // Draw a_0 from range [0, p-1]
    for (int i = 1; i < a.length; i++)
      a[i] = 1 + StdRandom.uniform(p - 1);  // Draw other parameters from range [1, p-1]
  }

  /**
   * Return hash value of the input key
   *
   * @param key the input key
   * @return hash value, in range [0, range)
   */
  public long hash(Object key) {
    long x = Integer.toUnsignedLong(key.hashCode());
    long x_k = x;
    long prod = a[0];   // Sum of polynomial
    for (int i = 1; i < a.length; i++) {
      prod += (a[i] * x_k) % p;
      prod %= p;
      x_k = (x_k * x) % p;
    }
    return prod % range;
  }

  public static void main(String[] args) {
    Map<String, Integer> counter = new HashMap<>();
    for (int i = 0; i < 1000000; i++) {
      TWiseHash tHash = new TWiseHash(2, FastHash.MERSENNE_PRIME31, 10);
      String key = Long.toString(tHash.hash(10));
      counter.put(key, counter.getOrDefault(key, 0) + 1);
    }
    StdOut.println(MapUtil.sortByValue(counter));
  }


}
