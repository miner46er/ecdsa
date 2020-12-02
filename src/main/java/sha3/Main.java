package sha3;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        byte[] m;
        Sha3[] sha3 = new Sha3[4];
        sha3[0] = new Sha3(224);
        sha3[1] = new Sha3(256);
        sha3[2] = new Sha3(384);
        sha3[3] = new Sha3(512);
        m = new byte[0];
        printDigests(sha3, m);
        m = new byte[200];
        for (int i = 0; i < m.length; ++i)
            m[i] = (byte)0xa3;
        printDigests(sha3, m);
        m = "abc".getBytes(StandardCharsets.US_ASCII);
        printDigests(sha3, m);
    }

    private static void printHex(byte[] bb) {
        for (byte b : bb)
            System.out.print(String.format("%02x", (int)(b & 0xff)));
    }

    private static void printDigest(Sha3 md, byte[] message) {
        int s = md.getOutputSize();
        byte[] digest = md.digest(message);
        System.out.print(String.format(" SHA3-%d = ", s));
        printHex(digest);
        System.out.println();
    }

    private static void printDigests(Sha3[] mds, byte[] m) {
        System.out.print("Message: ");
        printHex(m);
        System.out.println("\n");
        for (Sha3 md : mds)
            printDigest(md, m);
        System.out.println();
    }
}
