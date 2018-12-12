import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;


public class ElGamal {

    public static String decodeMessage(BigInteger m) {
        return new String(m.toByteArray());
    }

    public static void main(String[] arg) {
        String filename = "inputEG.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            BigInteger p = new BigInteger(br.readLine().split("=")[1]);
            BigInteger g = new BigInteger(br.readLine().split("=")[1]);
            BigInteger y = new BigInteger(br.readLine().split("=")[1]);
            String line = br.readLine().split("=")[1];
            String date = line.split(" ")[0];
            String time = line.split(" ")[1];
            int year  = Integer.parseInt(date.split("-")[0]);
            int month = Integer.parseInt(date.split("-")[1]);
            int day   = Integer.parseInt(date.split("-")[2]);
            int hour   = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            int second = Integer.parseInt(time.split(":")[2]);
            BigInteger c1 = new BigInteger(br.readLine().split("=")[1]);
            BigInteger c2 = new BigInteger(br.readLine().split("=")[1]);
            br.close();
            BigInteger m = recoverSecret(p, g, y, year, month, day, hour, minute,
                    second, c1, c2);
            System.out.println("Recovered message: " + m);
            System.out.println("Decoded text: " + decodeMessage(m));
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
            System.exit(1);
        }
    }

    public static BigInteger recoverSecret(BigInteger p, BigInteger g,
                                           BigInteger y, int year, int month, int day, int hour, int minute,
                                           int second, BigInteger c1, BigInteger c2) {

        /**
         * Find her random R by trying all different values of milliseconds.
         */

        BigInteger bigKey = BigInteger.ZERO;
        for (int i = 0; i < 1000; i++) {
            long key = recoverRandomKey(year, month, day, hour, minute, second,i);
            bigKey = new BigInteger(Long.toString(key));

            if (g.modPow(bigKey,p).compareTo(c1)==0){
                System.out.println(i);
                System.out.println(bigKey);
                break;
            }
        }
        /**
         * Since we have h (which is y here), the size of the group (which is p)
         * , aswell as r and c2,
         * we can use the second part of ElGamal-encryption to
         * get out the m.
         *
         * c2 = m * h^r (mod p)
         *
         * This gives us
         *
         * m = (c2 * (h^r)^-1) (mod p)
         */

        BigInteger hr = y.modPow(bigKey,p);
        BigInteger hrInv = hr.modInverse(p);

        BigInteger m = c2.multiply(hrInv);

        return m.mod(p);
    }

    private static long recoverRandomKey(int year, int month, int day, int hour, int minute, int second,int mili){
        return (long)(year*(Math.pow(10,10))+month*(Math.pow(10,8))+day*(Math.pow(10,6))+hour*(Math.pow(10,4))+
                minute*(Math.pow(10,2))+second + mili);
    }
}