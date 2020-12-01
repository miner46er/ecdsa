package ecdsa;

import java.math.BigInteger;

/**
 * @author bipin khatiwada
 * github.com/bipinkh
 * @reference https://github.com/Kalyanranjan/ecdsa
 */

public class KeyPair {
    public BigInteger privateKey;
    public BigInteger[] publicKey;


    public KeyPair(BigInteger[] point, BigInteger p, BigInteger a) {
//        privateKey = BigIntUtils.randomNumberLessThan(p);
        privateKey = new BigInteger("7891686032FD8057F636B44B1F47CCE564D2509923A7465B", 16);
        publicKey = EcOperations.pointMultiply(point, p, a, privateKey);
    }

    public BigInteger[] getPublicKey() {
        return this.publicKey;
    }

    public BigInteger getPrivateKey() {
        return this.privateKey;
    }
}