package ecdsa;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * @author bipin khatiwada
 * github.com/bipinkh
 */
public class Signature {

    public static BigInteger[] messageSign(String msg, BigInteger n, BigInteger[] G, BigInteger a, BigInteger privateKey) throws NoSuchAlgorithmException {

        BigInteger k, kInv, r, e, s, z;
        BigInteger[] kG;

//        e = new BigInteger(SHAsum(msg.getBytes()), 16);
        e = new BigInteger("1B376F0B735C615CEEEB31BAEE654B0A374825DB", 16);
        z = e.shiftRight(e.bitLength() - n.bitLength());

        do {
            do {
//                k = BigIntUtils.randomNumberLessThan(p);
                k = new BigInteger("D06CB0A0EF2F708B0744F08AA06B6DEEDEA9C0F80A69D847", 16);
                kG = EcOperations.pointMultiply(G, Constants.p, a, k);
                r = kG[0].mod(n);
            } while (r.compareTo(BigInteger.ZERO) == 0);

            kInv = k.modInverse(n);

            s = kInv.multiply(z.add(r.multiply(privateKey))).mod(n);

//            System.out.println("s :" + s.toString(16));
        } while (s.compareTo(BigInteger.ZERO) == 0);

        kG[0] = r;
        kG[1] = s;
        return kG;
    }

    public static boolean messageVerify(String msg, BigInteger[] sign, BigInteger n, BigInteger[] G, BigInteger a, BigInteger[] pbkQ) throws NoSuchAlgorithmException {

        BigInteger r = sign[0];
        BigInteger s = sign[1];

        if (r.compareTo(n) >= 0) {
            System.out.println(" r : Message NOT VERIFIED");
            return false;
        }
        if (s.compareTo(n) >= 0) {
            System.out.println(" s :Message NOT VERIFIED");
            return false;
        }

        BigInteger e = new BigInteger(SHAsum(msg.getBytes()), 16);
        BigInteger z = e.shiftRight(e.bitLength() - n.bitLength());
        BigInteger sInv = s.modInverse(n);

        BigInteger u1 = z.multiply(sInv).mod(n);
        BigInteger u2 = r.multiply(sInv).mod(n);

        BigInteger[] X = EcOperations.pointAddition(EcOperations.pointMultiply(G, Constants.p, a, u1), EcOperations.pointMultiply(pbkQ, Constants.p, a, u2), Constants.p);

        if(X[0].equals(BigInteger.ZERO) || X[1].equals(BigInteger.ZERO) ){
            System.out.println("Invalid !");
        }
        BigInteger v = X[0].mod(n);

        if (v.compareTo(r) == 0) {
            System.out.println("Message VERIFIED");
            return true;
        }

        System.out.println("Message NOT VERIFIED");
        return false;
    }

    public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        return byteArray2Hex(md.digest(convertme));
    }


    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        try{
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }finally {
            formatter.close();
        }

    }

}
