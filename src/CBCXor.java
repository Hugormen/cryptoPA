import java.io.BufferedReader;
import java.io.FileReader;

import javax.xml.bind.DatatypeConverter;

public class CBCXor {

    public static void main(String[] args) {
        String filename = "inputCBC.txt";
        byte[] first_block = null;
        byte[] encrypted = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            first_block = br.readLine().getBytes();
            encrypted = DatatypeConverter.parseHexBinary(br.readLine());
            br.close();
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
            System.exit(1);
        }
        String m = recoverMessage(first_block, encrypted);
        System.out.println("Recovered message: " + m);
    }

    /**
     * Recover the encrypted message (CBC encrypted with XOR, block size = 12).
     *
     * @param first_block
     *            We know that this is the value of the first block of plain
     *            text.
     * @param encrypted
     *            The encrypted text, of the form IV | C0 | C1 | ... where each
     *            block is 12 bytes long.
     */
    private static String recoverMessage(byte[] first_block, byte[] encrypted) {

        byte[] key = new byte[first_block.length];

        /**
         * E: C_i = K + (M_i + C_(i-1))
         * E_0: C_0 = K + (M_0 + IV)
         * K  = C_0 + (M_0 + IV)
         *
         * D: M_0 = (K+C_i)+C_(i-1)
         */
        for (int i = 0; i < first_block.length ; i++) {
            key[i] = (byte)(encrypted[12+i] ^ (first_block[i] ^ encrypted[i]));
        }

        byte[] decrypted = new byte[encrypted.length];

        for (int i = decrypted.length-12; i > 12 ; i = i-12) {

            for (int j = 0; j < 12; j++) {
                decrypted[i+j] = (byte)((key[j]^ encrypted[i+j])^ encrypted[i+j-12]);

            }

        }





        return new String(decrypted);
    }
}