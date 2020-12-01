package sha3;

/**
 * SHA-3 padding rule: append 01 then pad10*1
 */
public class PadSha3 implements PaddingRule {
    public PadSha3() {}

    @Override
    public byte[] pad(int x, int m) {
        int j = ((-m - 4) % x) + x;
        int outbits = 4 + j;
        assert outbits % 8 == 0;
        int outbytes = outbits / 8;
        assert outbytes > 0;
        byte[] output = new byte[outbytes];
        /*
        System.err.println
            (String.format("x=%d m=%d j=%d obits=%d obytes=%d",
                           x, m, j, outbits, outbytes));
        */
        // Note endianness: leftmost bit is LSB!
        output[0] = (byte)0x06; // 0b011...
        output[outbytes - 1] |= 0x80; // ...0b1
        return output;
    }
}
