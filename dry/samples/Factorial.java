/* Should have a dryness score close to 0 */

public class Factorial {

	public static void main(String[] args) {
		factorial(10);
	}
	public static int factorial(int n) {
		int fact = 1;
		for(int i = 1; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}
}