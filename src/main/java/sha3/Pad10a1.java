package sha3;

/**
 * Padding rule pad10*1
 */
public class Pad10a1 implements PaddingRule {
    public Pad10a1() {}

    @Override
    public byte[] pad(int x, int m) {
        int j = -((-m - 2) % x);
        int outbits = 2 + j;
        assert outbits % 8 == 0;
        int outbytes = outbits / 8;
        assert outbytes > 0;
        byte[] output = new byte[outbytes];
        output[0] = (byte)0x80; // 0b1...
        output[outbytes - 1] |= 0x01; // ...0b1
        return output;
    }
}
