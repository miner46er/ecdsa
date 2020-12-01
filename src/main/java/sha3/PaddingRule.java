package sha3;

public interface PaddingRule {
    /**
     * Generate padding to pad m bits of input to a multiple of x bits.
     *
     * x and m must be multiples of 8.
     */
    public byte[] pad(int x, int m);
}
