import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DatabaseManager {

	static Table table1;
	static Table table2;
	static Table table3;
	
	public static int securityLevel;

	public static Table GetTableByName(String tableName) {
		switch(tableName) {
		case "T1":
			return table1;
		case "T2":
			return table2;
		case "T3":
			return table3;
		default:
			return null;
		}
	}
	
	public static void LoadTables() {
		LoadLocalT1();
		LoadLocalT2();
		LoadLocalT3();

		//DBTests();
	}
	
	public static void LoadTables(String t1Path, String t2Path, String t3Path) {
		table1 = LoadTable(t1Path, 1, 3, "T1");
		table2 = LoadTable(t2Path, 1, 4, "T2");
		table3 = LoadTable(t3Path, 1, 5, "T3");
	}
	
	private static Table LoadTable(String path, int kcLoc, int tcLoc, String name) {
		Table t = null;
		try {
			File f = new File(path);
			InputStream resourceStream = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));

			String[] columns = reader.readLine().split("\\s");
			ArrayList<int[]> rows = new ArrayList<int[]>();

			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				String[] rowDataRaw = nextLine.split("\\s");
				int[] rowData = new int[rowDataRaw.length];
				for (int i = 0; i < rowDataRaw.length; i++) {
					rowData[i] = Integer.parseInt(rowDataRaw[i]);
				}
				rows.add(rowData);
			}

			t = new Table(columns, rows, kcLoc, tcLoc, name);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}


	private static void LoadLocalT1() {
		try {

			InputStream resourceStream = DatabaseManager.class.getResourceAsStream("/T1.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));

			String[] columns = reader.readLine().split("\\s");
			ArrayList<int[]> rows = new ArrayList<int[]>();

			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				String[] rowDataRaw = nextLine.split("\\s");
				int[] rowData = new int[rowDataRaw.length];
				for (int i = 0; i < rowDataRaw.length; i++) {
					rowData[i] = Integer.parseInt(rowDataRaw[i]);
				}
				rows.add(rowData);
			}

			table1 = new Table(columns, rows, 1, 3, "T1");

			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void LoadLocalT2() {
		try {

			InputStream resourceStream = DatabaseManager.class.getResourceAsStream("/T2.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));

			String[] columns = reader.readLine().split("\\s");
			ArrayList<int[]> rows = new ArrayList<int[]>();

			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				String[] rowDataRaw = nextLine.split("\\s");
				int[] rowData = new int[rowDataRaw.length];
				for (int i = 0; i < rowDataRaw.length; i++) {
					rowData[i] = Integer.parseInt(rowDataRaw[i]);
				}
				rows.add(rowData);
			}

			table2 = new Table(columns, rows, 1, 4, "T2");
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void LoadLocalT3() {
		try {

			InputStream resourceStream = DatabaseManager.class.getResourceAsStream("/T3.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));

			String[] columns = reader.readLine().split("\\s");
			ArrayList<int[]> rows = new ArrayList<int[]>();

			String nextLine;
			while ((nextLine = reader.readLine()) != null) {
				String[] rowDataRaw = nextLine.split("\\s");
				int[] rowData = new int[rowDataRaw.length];
				for (int i = 0; i < rowDataRaw.length; i++) {
					rowData[i] = Integer.parseInt(rowDataRaw[i]);
				}
				rows.add(rowData);
			}

			table3 = new Table(columns, rows, 1, 5, "T3");
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static void DBTests() {
		
		//inner join and cartesian product test
		Table temp1 = new Table(table1, table2, "A1=B1");
		Table temp2 = new Table(table1, table2);
		temp1.print(false);
		temp2.print();
		System.out.println("inner join row count: " + temp1.getRowCount() + ", cartesian join row count: " + temp2.getRowCount());
	}
}
