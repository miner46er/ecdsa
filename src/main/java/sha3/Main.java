package sha3;

public class Main {
    public static void main(String[] args) {
        byte[] m;
        Sha3[] sha3 = new Sha3[4];
        sha3[0] = new Sha3(224);
        sha3[1] = new Sha3(256);
        sha3[2] = new Sha3(384);
        sha3[3] = new Sha3(512);
        m = new byte[0];
        printDigest(sha3[0], m);
        printDigest(sha3[1], m);
        printDigest(sha3[2], m);
        printDigest(sha3[3], m);
    }

    private static void printHex(byte[] bb) {
        for (byte b : bb)
            System.out.print(String.format("%02x", (int)(b & 0xff)));
    }

    private static void printDigest(Sha3 md, byte[] message) {
        int s = md.getOutputSize();
        byte[] digest = md.digest(message);
        System.out.print(String.format("SHA3-%d(0x", s));
        printHex(message);
        System.out.print(") = 0x");
        printHex(digest);
        System.out.println();
    }
}
