package sha3;

/**
 * Keccak[c]
 */
public class Keccak extends Sponge {
    /**
     * Keccak[c] with c bits of capacity
     */
    public Keccak(int c) {
        super(new KeccakF(), new Pad10a1(), KeccakF.B - c);
    }
}
