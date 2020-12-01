package sha3;

/**
 * Keccak-f[1600] = Keccak-p[1600, 24]
 */
public class KeccakF implements SpongeFunction {
    /**
     * State as an array of 64-bit lanes [y][x]
     *
     * Note that z=0 denotes the MSB, while z=63 the LSB.
     *
     * With regards to (de)serialsation, the MSB of byte 0 is loaded into
     * the MSB of [0][0], while the LSB of byte 8 is loaded into the LSB of
     * [0][0].
     */
    protected long[][] lanes;

    /**
     * Lane size in bits w (fixed)
     */
    public static final int W = 64;

    /**
     * Log2 of lane size l (fixed)
     */
    public static final int L = 6;

    /**
     * State size in bits b (fixed)
     */
    public static final int B = 5 * 5 * W;

    /**
     * Number of rounds (fixed)
     */
    public static final int NR = 24;

    /**
     * Parity planes (C and D) for theta
     */
    private long[] cPlane, dPlane;

    /**
     * Scratch state array for pi and chi
     */
    private long[][] lanesPrime;

    /**
     * LFSR seed
     */
    private static byte rSeed = (byte)0x80;

    /**
     * LFSR state R for iota
     */
    private byte r;

    /**
     * Initialise a zeroed state
     */
    public KeccakF() {
        lanes = new long[5][5];
        cPlane = new long[5];
        dPlane = new long[5];
        lanesPrime = new long[5][5];
        r = rSeed;
    }

    /**
     * Get the number of bytes in the state
     */
    @Override
    public int getStateSize() {
        return B / 8;
    }

    /**
     * Set state to all-zeroes
     */
    @Override
    public void zeroiseState() {
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                lanes[y][x] = 0;
                lanesPrime[y][x] = 0;
            }
            cPlane[y] = 0;
            dPlane[y] = 0;
        }
        r = rSeed;
    }

    /**
     * Serialise state to bytes
     */
    @Override
    public void serialise(byte[] out) {
        int i = 0;
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                long lane = lanes[y][x];
                out[i++] = (byte)((lane >> 56) & 0xff);
                out[i++] = (byte)((lane >> 48) & 0xff);
                out[i++] = (byte)((lane >> 40) & 0xff);
                out[i++] = (byte)((lane >> 32) & 0xff);
                out[i++] = (byte)((lane >> 24) & 0xff);
                out[i++] = (byte)((lane >> 16) & 0xff);
                out[i++] = (byte)((lane >>  8) & 0xff);
                out[i++] = (byte)((lane      ) & 0xff);
            }
        }
    }

    /**
     * Deserialise bytes to state
     */
    @Override
    public void deserialise(final byte[] in) {
        int i = 0;
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                long lane;
                lane =               (in[i++] & 0xff);
                lane = (lane << 8) | (in[i++] & 0xff);
                lane = (lane << 8) | (in[i++] & 0xff);
                lane = (lane << 8) | (in[i++] & 0xff);
                lane = (lane << 8) | (in[i++] & 0xff);
                lane = (lane << 8) | (in[i++] & 0xff);
                lane = (lane << 8) | (in[i++] & 0xff);
                lane = (lane << 8) | (in[i++] & 0xff);
            }
        }
    }

    /**
     * XOR the bytes p (as a bit string) into the first bitlen(p) bits
     * of state.
     */
    @Override
    public void xorIn(final byte[] p) {
        int len = p.length;
        int x = 0, y = 0;
        int nz = W - 8; // bit position
        long lane = 0;
        for (int i = 0; i < len; ++i) {
            lane |= ((long)p[i] & 0xff) << nz;
            if ((nz -= 8) < 0) {
                lanes[y][x] ^= lane;
                nz = W - 8;
                lane = 0;
                if ((x += 1) == 5) {
                    x = 0;
                    y += 1;
                }
            }
        }
        if (nz < W-8) {
            lanes[y][x] ^= lane;
        }
    }

    /**
     * Extract the first 8*nbytes bits of state into the array out.
     */
    @Override
    public void extract(int nbytes, byte[] out) {
        int x = 0, y = 0;
        int nz = W - 8; // bit position
        long lane = lanes[y][x];
        for (int i = 0; i < nbytes; ++i) {
            out[i] = (byte)((lane >> nz) & 0xff);
            if ((nz -= 8) < 0) {
                nz = W - 8;
                if ((x += 1) == 5) {
                    x = 0;
                    y += 1;
                }
                lane = lanes[y][x];
            }
        }
    }

    /**
     * Theta step function
     */
    private void theta() {
        // Calculate C
        for (int x = 0; x < 5; ++x)
            cPlane[x] = 0;
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x)
                cPlane[x] ^= lanes[y][x];
        }
        // Calculate D
        for (int x = 0; x < 5; ++x) {
            long prev = cPlane[(x+4) % 5];
            long next = cPlane[(x+1) % 5];
            next = (next >>> 1) | (next << (W - 1));
            dPlane[x] = prev ^ next;
        }
        // Apply
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x)
                lanes[y][x] ^= dPlane[x];
        }
    }

    /**
     * Rotate lane (x, y) by amount
     */
    private void rot(int x, int y, int amount) {
        long l = lanes[y][x];
        l = (l >>> amount) | (l << (W - amount));
        lanes[y][x] = l;
    }

    /**
     * Rho step function
     */
    private void rho() {
        rot(0, 0,   0 % W);
        rot(1, 0,   1 % W);
        rot(2, 0, 190 % W);
        rot(3, 0,  28 % W);
        rot(4, 0,  91 % W);

        rot(0, 1,  36 % W);
        rot(1, 1, 300 % W);
        rot(2, 1,   6 % W);
        rot(3, 1,  55 % W);
        rot(4, 1, 276 % W);

        rot(0, 2,   3 % W);
        rot(1, 2,  10 % W);
        rot(2, 2, 171 % W);
        rot(3, 2, 153 % W);
        rot(4, 2, 231 % W);

        rot(0, 3, 105 % W);
        rot(1, 3,  45 % W);
        rot(2, 3,  15 % W);
        rot(3, 3,  21 % W);
        rot(4, 3, 136 % W);

        rot(0, 4, 210 % W);
        rot(1, 4,  66 % W);
        rot(2, 4, 253 % W);
        rot(3, 4, 120 % W);
        rot(4, 4,  78 % W);
    }

    /**
     * Pi step function
     */
    private void pi() {
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x)
                lanesPrime[y][x] = lanes[x][(x + 3*y) % 5];
        }
        // Swap buffers
        long[][] temp = lanes;
        lanes = lanesPrime;
        lanesPrime = lanes;
    }

    /**
     * Chi step function
     */
    private void chi() {
        for (int y = 0; y < 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                long xorlane = (~lanes[y][(x+1) % 5]) & lanes[y][(x+2) % 5];
                lanesPrime[y][x] = lanes[y][x] ^ xorlane;
            }
        }
        // Swap buffers
        long[][] temp = lanes;
        lanes = lanesPrime;
        lanesPrime = lanes;
    }

    /**
     * LFSR calculation
     */
    private long nextRc() {
        int c = r & 1;
        r >>>= 1;
        r ^= -c & 0x8e;
        return c;
    }

    /**
     * Iota step function
     */
    private void iota() {
        long rc;
        rc  = nextRc() << 63;
        rc |= nextRc() << 62;
        rc |= nextRc() << 60;
        rc |= nextRc() << 56;
        rc |= nextRc() << 48;
        rc |= nextRc() << 32;
        rc |= nextRc();
        lanes[0][0] ^= rc;
    }

    /**
     * Round function
     *
     * Round number is implicit in how many times iota() has been called.
     */
    private void rnd() {
        theta();
        rho();
        pi();
        chi();
        iota();
    }

    /**
     * Run the Keccak permutation over our current state.
     */
    @Override
    public void f() {
        r = rSeed; // Start from round 0
        for (int i = 0; i < NR; ++i)
            rnd();
    }
}
