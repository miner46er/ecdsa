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
        privateKey = BigIntUtils.randomNumberLessThan(p);
        publicKey = EcOperations.pointMultiply(point, p, a, privateKey);
    }

    public BigInteger[] getPublicKey() {
        return this.publicKey;
    }

    public BigInteger getPrivateKey() {
        return this.privateKey;
    }
}