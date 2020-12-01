package sha3;

/**
 * Sponge construction
 */
public class Sponge {
    protected SpongeFunction f;
    protected PaddingRule pad;
    protected int b, bBytes;
    protected int r, rBytes;
    protected int c, cBytes;

    public Sponge(SpongeFunction f, PaddingRule pad, int rate) {
        this.f = f;
        this.pad = pad;
        bBytes = f.getStateSize();
        b = 8 * bBytes;
        r = rate;
        rBytes = r / 8;
        c = b - r;
        cBytes = r / 8;
    }

    public void sponge(final byte[] n, int d, byte[] out) {
        byte[] padding = pad.pad(r, 8 * n.length);
        int nWholeBlocks = n.length / rBytes;
        int bWholeBlocks = nWholeBlocks * rBytes;
        int bPartial = n.length - bWholeBlocks;
        assert padding.length + bPartial == rBytes;
        byte[] block = new byte[rBytes];
        f.zeroiseState();
        for (int i = 0; i < nWholeBlocks; ++i) {
            System.arraycopy(n, rBytes * i, block, 0, rBytes);
            f.xorIn(block);
            f.f();
        }
        System.arraycopy(n, bWholeBlocks, block, 0, bPartial);
        System.arraycopy(padding, 0, block, bPartial, padding.length);
        f.xorIn(block);
        f.f();
        f.extract(d / 8, out);
        f.zeroiseState();
    }
}
