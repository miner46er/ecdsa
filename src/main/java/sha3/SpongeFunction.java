package sha3;

/**
 * Interface between a permutation and the Sponge
 */
public interface SpongeFunction {
    /**
     * Get the number of bytes in the state
     */
    public int getStateSize();

    /**
     * Set state to all-zeroes
     */
    public void zeroiseState();

    /**
     * Serialise the entire state to bytes
     *
     * out *must* be sized appropriately.
     */
    public void serialise(byte[] out);

    /**
     * Deserialise a state from bytes
     *
     * There must be enough bytes to fill the state.
     */
    public void deserialise(final byte[] in);

    /**
     * XOR the bytes p (as a bit string) into the first bitlen(p) bits
     * of state.
     */
    public void xorIn(final byte[] p);

    /**
     * Extract the first 8*nbytes bits of state into the array out.
     */
    public void extract(int nbytes, byte[] out);

    /**
     * Permute the state
     */
    public void f();
}
