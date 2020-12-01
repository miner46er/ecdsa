package sha3;

/**
 * SHA3-n
 */
public class Sha3 {
    /**
     * Fixed width b of Keccak-f (1600 bits)
     */
    protected static final int b = KeccakF.B;

    /**
     * Capacity in bits
     */
    protected int c;

    /**
     * Rate in bits
     */
    protected int r;

    /**
     * Output length in bits
     */
    protected int d;

    /**
     * Sponge instance
     */
    protected Sponge s;

    /**
     * SHA3-n with n bits of output
     */
    public Sha3(int n) {
        switch (n) {
        case 224:
            c = 448;
            d = 224;
            break;
        case 256:
            c = 512;
            d = 256;
            break;
        case 384:
            c = 768;
            d = 384;
            break;
        case 512:
            c = 1024;
            d = 512;
            break;
        default:
            throw new IllegalArgumentException("Invalid SHA-3 output size");
        }
        r = b - c;
        s = new Sponge(new KeccakF(), new PadSha3(), r);
    }

    /**
     * Digest a message and return its hash value
     */
    public byte[] digest(final byte[] message) {
        byte[] out = new byte[d / 8];
        s.sponge(message, d, out);
        return out;
    }

    public int getOutputSize() {
        return d;
    }
}
