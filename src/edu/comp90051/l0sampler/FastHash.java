package edu.comp90051.l0sampler;

import edu.comp90051.utils.MapUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class FastHash extends TWiseHash {
  public static long MERSENNE_PRIME17 = 0x1FFFF;        // Mersenne prime 2^17 - 1
  public static long MERSENNE_PRIME31 = 0x7FFFFFFF;     // Mersenne prime 2^31 - 1

  // Mersenne prime 2^61 - 1
  public static long MERSENNE_PRIME61 = new BigInteger("1FFFFFFFFFFFFFFF", 16).longValue();

  private int shift;  // bits to shift when modding

  /**
   * Constructing a hash function drawn from t-wise independent hash family with fast modding strategy
   * The adoption of fast modding rather than regular modulus operation is for acceleration reason.
   * <p>
   * The only allowed modding prime is 2^17-1, 2^31-1, 2^61-1.
   * Any other will raise an <i>IllegalArgumentException</i>
   *
   * @param t     the coordinate of hash family's independence
   * @param p     the modding prime
   * @param range the value range (modded range) of hash function
   */
  public FastHash(int t, long p, long range) {
    super(t, p, range);
    pValidationCheck(p);
  }

  private void pValidationCheck(long p) {
    if (p == MERSENNE_PRIME17)
      shift = 17;
    else if (p == MERSENNE_PRIME31)
      shift = 31;
    else if (p == MERSENNE_PRIME61)
      shift = 61;
    else
      throw new IllegalArgumentException("Invalid Prime Number p");
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
      prod += fastMod(a[i] * x_k);
      prod = fastMod(prod);
      x_k = fastMod(x_k * x);
    }
    return prod % range;
  }

  /**
   * Fast Modding strategy
   *
   * @param x long decimal to be modded
   * @return x mod p
   */
  private long fastMod(long x) {
    x = (x >> shift) + (x & p);
    x = (x >> shift) + (x & p);
    return x;
  }

  /*******************************************************************
   *  Utility methods.
   *******************************************************************/

  public static long mod17(long x) {
    x = (x >> 17) + (x & MERSENNE_PRIME17);
    x = (x >> 17) + (x & MERSENNE_PRIME17);
    return x;
  }

  public static long mod31(long x) {
    x = (x >> 31) + (x & MERSENNE_PRIME31);
    x = (x >> 31) + (x & MERSENNE_PRIME31);
    return x;
  }

  public static long mod61(long x) {
    x = (x >> 61) + (x & MERSENNE_PRIME61);
    x = (x >> 61) + (x & MERSENNE_PRIME61);
    return x;
  }

  public static void main(String[] args) {

    Map<String, Integer> counter = new HashMap<>();
    for (int i = 0; i < 1000000; i++) {
      FastHash tHash = new FastHash(4, FastHash.MERSENNE_PRIME31, 10);
      String key = Long.toString(tHash.hash(17283));
      counter.put(key, counter.getOrDefault(key, 0) + 1);
    }
    StdOut.println(MapUtil.sortByValue(counter));
  }
}
