/* Should have a dryness score close to 0 */

public class DoubleFor {
	public static void main(String[] args) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				System.out.println("here " + i);
			}
		}
	}
}