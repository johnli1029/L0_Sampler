import java.math.BigInteger;

public class Tst {

    public static int mod17(int x) {
        int prime17 = 0x1FFFF;
        x = (x >> 17) + (x & prime17);
        x = (x >> 17) + (x & prime17);
        return x;
    }

    public static long mod61(long x) {
        long prime61 = new BigInteger("FFFFFFFFFFFFFFF", 16).longValue();
        x = (x >> 61) + (x & prime61);
        x = (x >> 61) + (x & prime61);
        return x;
    }

    public static void main(String[] args) {
        long prime61 = new BigInteger("FFFFFFFFFFFFFFF", 16).longValue();
        System.out.println(prime61);
        System.out.println(0xFFFFF % prime61);
        System.out.println(mod61(0xFFFFF));
    }
}
