// Compilation (CryptoLibTest contains the main-method):
//   javac CryptoLibTest.java
// Running:
//   java CryptoLibTest

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigInteger.*;

public class CryptoLib {

	/**
	 * Returns an array "result" with the values "result[0] = gcd",
	 * "result[1] = s" and "result[2] = t" such that "gcd" is the greatest
	 * common divisor of "a" and "b", and "gcd = a * s + b * t".
	 **/
	public static int[] EEA(int a, int b) {
		int[] result = new int[3];

		int previous_s = 1;
		int previous_t = 0;
		int previous_r = a;
		int s = 0;
		int t = 1;
		int r = b;

		if(a % b != 0 || a == 1 || b == 1) {
			while(r != 0) {
				int q = previous_r / r;

				int temp_prev_r = previous_r;
				previous_r = r;
				r = temp_prev_r - q * r;

				int temp_prev_s = previous_s;
				previous_s = s;
				s = temp_prev_s - q * s;

				int temp_prev_t = previous_t;
				previous_t = t;
				t = temp_prev_t - q * t;
			}
		}

		result[0] = previous_r;
		result[1] = previous_s;
		result[2] = previous_t;
		return result;
	}

	/**
	 * Returns Euler's Totient for value "n".
	 **/
	public static int EulerPhi(int n) {
		if(n<1) {
			return 0;
		}

		int amountOfCoPrimes = 0;

		// Check for every number smaller than n if they are relatively prime.
		for (int i = 1; i < n; i++) {
			if ( gcd(n, i) == 1)
				amountOfCoPrimes++;
		}

		return amountOfCoPrimes;
	}


	/**
	 * Returns the value "v" such that "n*v = 1 (mod m)". Returns 0 if the
	 * modular inverse does not exist.
	 **/
	public static int ModInv(int n, int m) {
        n = n % m;
        for (int x = 1; x < m; x++)
            if ( ((n * x) % m) == 1 || ((n * x) % m) == -(m-1))
                return x;
        return 1;
    }

	/**
	 * Returns 0 if "n" is a Fermat Prime, otherwise it returns the lowest
	 * Fermat Witness. Tests values from 2 (inclusive) to "n/3" (exclusive).
	 **/
	public static int FermatPT(int n) {

        for (int i = 2; i < (n/3); i++) {

            if((longPow(i,n-1,n))!=1){
             return i;
            }

        }

		return 0;
	}

	/**
	 * Returns the probability that calling a perfect hash function with
	 * "n_samples" (uniformly distributed) will give one collision (i.e. that
	 * two samples result in the same hash) -- where "size" is the number of
	 * different output values the hash function can produce.
	 **/
	public static double HashCP(double n_samples, double size) {
        double probAllUnique = 1.0;
        for (int i = 1; i < n_samples+1; i++) {
            probAllUnique= probAllUnique*((size-(i-1))/size);
            
        }

		return 1-probAllUnique;
	}

	// Help methods below

    public static List<Integer> distinctPrimeFactors(int number){

        List<Integer> primeFactors = new ArrayList<>();
        int current = 0;
        for(int i = 2; i< number; i++) {
            while(number%i == 0) {
                if(i!=current){
                    current=i;
                    primeFactors.add(i);
                }
                number = number/i;
            }
        }
        if(number >2) {
            primeFactors.add(number);
        }

        return primeFactors;
    }

    private static int gcd(int a, int b) {
        if(a == b)
            return a;

        if(b == 0){
            return a;
        } else {
            return gcd(b, a%b);
        }
    }

    public static long longPow(int base, int pow, int n){
        long product = 1;

        for (int i = 0; i < pow; i++) {
            product = ((product%(n)) * base)%(n);

        }

        return product;
    }

}
