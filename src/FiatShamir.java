import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

public class FiatShamir {

    public static class ProtocolRun {
        public final BigInteger R;
        public final int c;
        public final BigInteger s;

        public ProtocolRun(BigInteger R, int c, BigInteger s) {
            this.R = R;
            this.c = c;
            this.s = s;
        }
    }

    public static void main(String[] args) {
        String filename = "inputFS.txt";
        BigInteger N = BigInteger.ZERO;
        BigInteger X = BigInteger.ZERO;
        ProtocolRun[] runs = new ProtocolRun[10];
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            N = new BigInteger(br.readLine().split("=")[1]);
            X = new BigInteger(br.readLine().split("=")[1]);
            for (int i = 0; i < 10; i++) {
                String line = br.readLine();
                String[] elem = line.split(",");
                runs[i] = new ProtocolRun(
                        new BigInteger(elem[0].split("=")[1]),
                        Integer.parseInt(elem[1].split("=")[1]),
                        new BigInteger(elem[2].split("=")[1]));
            }
            br.close();
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
            System.exit(1);
        }
        BigInteger m = recoverSecret(N, X, runs);
        System.out.println("Recovered message: " + m);
        System.out.println("Decoded text: " + decodeMessage(m));
    }

    public static String decodeMessage(BigInteger m) {
        return new String(m.toByteArray());
    }

    /**
     * Recovers the secret used in this collection of Fiat-Shamir protocol runs.
     *
     * @param N
     *            The modulus
     * @param X
     *            The public component
     * @param runs
     *            Ten runs of the protocol.
     * @return
     */
    private static BigInteger recoverSecret(BigInteger N, BigInteger X,
                                            ProtocolRun[] runs) {
        BigInteger firstR = BigInteger.ZERO;
        BigInteger secondR = BigInteger.ZERO;
        BigInteger firstS = BigInteger.ZERO;
        BigInteger secondS = BigInteger.ZERO;
        int firstC=0;
        int secondC=0;

        BigInteger xSmall = BigInteger.ZERO;

        /**
         * find the two inputs with the same R, the nonce.
         * Save these S:es. S=r^2*x^c
         * Save these C:s
         */

        outerloop:
        for (int i = 0; i < runs.length; i++) {
            firstR = runs[i].R;
            for (int j = 0; j <runs.length; j++) {
                if(j!=i && firstR.subtract(runs[j].R ).equals(BigInteger.ZERO) ){
                    firstS = runs[i].s;
                    secondS = runs[j].s;
                    firstC = runs[i].c;
                    secondC = runs[j].c;

                    break outerloop;
                }


            }

        }

        /**
         * Since one C is 1 and the other 0, we get S1= r^2 * x^1 and S2= r^2*x^0.
         * By dividing these, we cancel the r^2 and retain x.
         *
         * Division is done by calculating the modular inverse of the S with C=0, and multiplying S1 with this inverse.
         */

        if(firstC == 1 && secondC == 0){
            BigInteger inverseOfDivisor = secondS.modInverse(N);
            xSmall = firstS.multiply(inverseOfDivisor);

        }

        if(firstC == 0 && secondC == 1){
            BigInteger inverseOfDivisor = firstS.modInverse(N);
            xSmall = secondS.multiply(inverseOfDivisor);
        }


        return xSmall.mod(N) ;
    }
}