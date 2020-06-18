

public class Util {

	public static boolean IsNumeric(String s) {
		if (s == null)
			return false;
		
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}
	
	public static void PrintDebug(String s) {
		if(Main.debugMode) {
			System.out.println(s);
		}
	}
}
