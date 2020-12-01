package ecdsa;

import java.math.BigInteger;
import java.lang.String;

/**
 * @author bipin khatiwada
 * github.com/bipinkh
 * @reference: https://github.com/Kalyanranjan/ecdsa
 * */

public class EcOperations {

    /*
     * negation: Function to get the negative of the point p in EC
     */
    public static BigInteger[] negation(BigInteger[] p) {
        BigInteger[] q = new BigInteger[2];
        q[0] = p[0];	//same x-coordinate
        q[1] = p[1].multiply(BigInteger.valueOf(-1)); //negating y- coordinate
        return q;
    }

    /*
     * pointAddition: function to adding Two points p1 and p2 in the EC
     * n: order of the field of EC
     */
    public static BigInteger[] pointAddition(BigInteger[] p1, BigInteger[] p2, BigInteger n) {
        BigInteger[] val = new BigInteger[2];

        BigInteger xp = p1[0];
        BigInteger yp = p1[1];

        BigInteger xq = p2[0];
        BigInteger yq = p2[1];

        BigInteger m = (yp.subtract(yq)).multiply(xp.subtract(xq).modInverse(n)).mod(n);
        BigInteger xr = m.pow(2).subtract(xp).subtract(xq).mod(n);
        BigInteger yr = m.multiply(xp.subtract(xr)).subtract(yp).mod(n);

        val[0] = xr;
        val[1] = yr;

        return val;
    }

    /*
     * pointDoubling: function to double a point p1 in the EC
     * n: order of the field EC
     * a: variable a of the EC equation
     */
    public static BigInteger[] pointDoubling(BigInteger[] p1, BigInteger n, BigInteger a) {
        BigInteger[] val = new BigInteger[2];

        BigInteger xp = p1[0];
        BigInteger yp = p1[1];

        BigInteger m = xp.pow(2).multiply(BigInteger.valueOf(3)).add(a).multiply(yp.multiply(BigInteger.valueOf(2)).modInverse(n)).mod(n);

        BigInteger xr = m.pow(2).subtract(xp).subtract(xp).mod(n);
        BigInteger yr = m.multiply(xp.subtract(xr)).subtract(yp).mod(n);

        val[0] = xr;
        val[1] = yr;

        return val;
    }

    /*
     * pointDoubling: function to multiply a point p1 in the EC by an integer mult
     * n: order of the field EC
     * a: variable a of the EC equation
     */
    public static BigInteger[] pointMultiply(BigInteger[] p1, BigInteger n, BigInteger a, BigInteger mult) {
        BigInteger[] val = new BigInteger[2];
        BigInteger[] doubledP = p1;

        boolean set = false;
        String binMult = mult.toString(2);
        int binMultLen = binMult.length();

        for (int c=binMultLen-1; c>= 0; c--) {
            // System.out.print("|"+c+"|");
            if (binMult.charAt(c) == '1') {
                if (set) {
                    val = pointAddition(val, doubledP, n);
                } else {
                    val = doubledP;
                    set = true;
                }
            }
            doubledP = pointDoubling(doubledP, n, a);
        }
        return val;
    }

    /*
     * printPoint: function to print the point in co-ordinate form
     */
    public static String printPoint(BigInteger[] p) {
        return "("+ p[0].toString() +","+p[1].toString()+")";
    }
}