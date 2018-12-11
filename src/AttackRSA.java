import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

public class AttackRSA {

    public static void main(String[] args) {

        String filename = "inputRSA.txt";
        BigInteger[] N = new BigInteger[3];
        BigInteger[] e = new BigInteger[3];
        BigInteger[] c = new BigInteger[3];
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            for (int i = 0; i < 3; i++) {
                String line = br.readLine();
                String[] elem = line.split(",");
                N[i] = new BigInteger(elem[0].split("=")[1]);
                e[i] = new BigInteger(elem[1].split("=")[1]);
                c[i] = new BigInteger(elem[2].split("=")[1]);
            }
            br.close();
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
        }
        BigInteger m = recoverMessage(N, e, c);
        System.out.println("Recovered message: " + m);
        System.out.println("Decoded text: " + decodeMessage(m));
    }

    public static String decodeMessage(BigInteger m) {
        return new String(m.toByteArray());
    }

    /**
     * Tries to recover the message based on the three intercepted cipher texts.
     * In each array the same index refers to same receiver. I.e. receiver 0 has
     * modulus N[0], public key d[0] and received message c[0], etc.
     *
     * @param N
     *            The modulus of each receiver.
     * @param e
     *            The public key of each receiver (should all be 3).
     * @param c
     *            The cipher text received by each receiver.
     * @return The same message that was sent to each receiver.
     */
    private static BigInteger recoverMessage(BigInteger[] N, BigInteger[] e,
                                             BigInteger[] c) {

        BigInteger newModulus = N[0].multiply(N[1].multiply(N[2]));
        /**
         * Creating terms which will be used in the Chinese Remainder Theorem
         * Test is the value which we will divide each term[i] with to make it congruent with 1 mod n[i].
         */
        BigInteger[] terms = new BigInteger[3];
        BigInteger[] test = new BigInteger[3];

        for (int i = 0; i < 3 ; i++) {
            terms[i] = BigInteger.ONE;
        }

        /**
         *
         *
         * If Chinese Remainder theorem is like this: (= means Congruent in this example)
         * x = c0 mod (N[0]
         * x = c1 mod (N[1]
         * x = c2 mod (N[2]
         *
         * first step of Chinese Remainder Theorem. (= means equals again)
         *
         * x= N[1]* N[2]      +    N[0]*N[2]     + N[0]*N[1]
         *
         * This means that the the only term which will matter mod N[i] is the i:th term,
         * since the other two disappear since N[i] is a factor in those terms.
         *
         *
         */
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j <3 ; j++) {
                if(i!=j){
                    terms[i]= terms[i].multiply(N[j]);
                }
            }
        }

        /**
         * The second step begins here.
         * Thus, to make x = c0 mod(N[0]) we need to make the first term (mod N[0]) = c0.
         * This means we have to make N[1]*N[2] (mod N[0]) = c0.
         * This is done by making the first term equal to (N[1]*N[2]) * (N[1]*N[2])^-1 (mod N[0])  * c0(mod N)
         *                                                 ^^^^^^^^This here above is one^^^^^^^
         */

        test[0] = terms[0].mod(N[0]);
        test[1] = terms[1].mod(N[1]);
        test[2] = terms[2].mod(N[2]);

        for (int i = 0; i < 3; i++) {
            terms[i] = terms[i].multiply(test[i].modInverse(N[i]));
            terms[i] = terms[i].multiply(c[i]).mod(newModulus);
        }

        BigInteger sum = BigInteger.ZERO;

        for (int i = 0; i < terms.length ; i++) {
            sum = sum.add(terms[i]);
        }

        /**
         * Now that we have all the terms, and the sum fit the idea: (= is congruent, again)
         *  x = c0 mod (N[0])
         *  x = c1 mod (N[1])
         *  x = c2 mod (N[2])
         *
         * We simply take the cube root from x in mod N (N[0]*N[1]*N[2]), which is equal to our decoded message.
         */


        BigInteger m = CubeRoot.cbrt(sum.mod(newModulus));

        return m;
    }

}