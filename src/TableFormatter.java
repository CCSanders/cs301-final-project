

public class TableFormatter {
	
	public static void Print(Table t, boolean limit) {
		int rowCount = t.rows.size();
		if (rowCount > 100 && limit)
			rowCount = 100;
		
		for(int i = 0; i < t.getColCount(); i++) {
			System.out.format("%8s", t.columnNames[i]);
		}
		System.out.println();
		for(int i = 0; i < t.getColCount(); i++) {
			System.out.print("--------");
		}
		System.out.println();
		for (int i = 0; i < rowCount; i++) {
			//only print the rows allowed by the tc
			if(t.rows.get(i)[t.getColCount() - 1] > DatabaseManager.securityLevel) {
				continue;
			}
			for(int j = 0; j < t.getColCount(); j++) {
				System.out.format("%8d", t.rows.get(i)[j]);
			}
			System.out.println();
		}
	}
	
	public static void PrintProjection(Table t, boolean limit, boolean[] projectionMask, int projectedColCount) {
		int rowCount = t.rows.size();
		if (rowCount > 100 && limit)
			rowCount = 100;
		
		for(int i = 0; i < t.getColCount(); i++) {
			if(projectionMask[i]) {
				System.out.format("%8s", t.columnNames[i]);
			}
		}
		System.out.println();
		for(int i = 0; i < t.getColCount(); i++) {
			if(projectionMask[i]) {
				System.out.print("--------");
			}
		}
		System.out.println();
		for (int i = 0; i < rowCount; i++) {
			//only print the rows allowed by the tc
			if(t.rows.get(i)[t.getColCount() - 1] > DatabaseManager.securityLevel) {
				continue;
			}
			for(int j = 0; j < t.getColCount(); j++) {
				if(projectionMask[j]) {
					System.out.format("%8d", t.rows.get(i)[j]);
				}
			}
			System.out.println();
		}
	}
	
	/* FIXED IT
	//could not think of the solution to dynamically printing until i starting working on the projection. this works for now, might fix later. 
	public static void Print(Table t, boolean limit) {
		int colCount = t.columnNames.length;
		int rowCount = t.rows.size();
		if (rowCount > 100 && limit)
			rowCount = 100;

		switch (colCount) {
		case 4:
			System.out.format("%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2], t.columnNames[3]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2], t.rows.get(i)[3]);
			}
			break;
		case 5:
			System.out.format("%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2], t.columnNames[3],
					t.columnNames[4]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2], t.rows.get(i)[3],
						t.rows.get(i)[4]);
			}
			break;
		case 6:
			System.out.format("%8s%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2], t.columnNames[3],
					t.columnNames[4], t.columnNames[5]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2],
						t.rows.get(i)[3], t.rows.get(i)[4], t.rows.get(i)[5]);
			}
			break;
		case 7:
			System.out.format("%8s%8s%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2], t.columnNames[3],
					t.columnNames[4], t.columnNames[5], t.columnNames[6]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2],
						t.rows.get(i)[3], t.rows.get(i)[4], t.rows.get(i)[5], t.rows.get(i)[6]);
			}
			break;
		case 8:
			System.out.format("%8s%8s%8s%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2],
					t.columnNames[3], t.columnNames[4], t.columnNames[5], t.columnNames[6], t.columnNames[7]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2],
						t.rows.get(i)[3], t.rows.get(i)[4], t.rows.get(i)[5], t.rows.get(i)[6], t.rows.get(i)[7]);
			}
			break;
		case 9:
			System.out.format("%8s%8s%8s%8s%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2],
					t.columnNames[3], t.columnNames[4], t.columnNames[5], t.columnNames[6], t.columnNames[7], t.columnNames[8]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2],
						t.rows.get(i)[3], t.rows.get(i)[4], t.rows.get(i)[5], t.rows.get(i)[6], t.rows.get(i)[7], t.rows.get(i)[8]);
			}
			break;
		case 10:
			System.out.format("%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2],
					t.columnNames[3], t.columnNames[4], t.columnNames[5], t.columnNames[6], t.columnNames[7], t.columnNames[8], t.columnNames[9]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2],
						t.rows.get(i)[3], t.rows.get(i)[4], t.rows.get(i)[5], t.rows.get(i)[6], t.rows.get(i)[7], t.rows.get(i)[8], t.rows.get(i)[9]);
			}
			break;
		case 11:
			System.out.format("%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2],
					t.columnNames[3], t.columnNames[4], t.columnNames[5], t.columnNames[6], t.columnNames[7], t.columnNames[8], t.columnNames[9], t.columnNames[10]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2],
						t.rows.get(i)[3], t.rows.get(i)[4], t.rows.get(i)[5], t.rows.get(i)[6], t.rows.get(i)[7], t.rows.get(i)[8], t.rows.get(i)[9], t.rows.get(i)[10]);
			}
			break;
		case 12:
			System.out.format("%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2],
					t.columnNames[3], t.columnNames[4], t.columnNames[5], t.columnNames[6], t.columnNames[7], t.columnNames[8], t.columnNames[9], t.columnNames[10], t.columnNames[11]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2],
						t.rows.get(i)[3], t.rows.get(i)[4], t.rows.get(i)[5], t.rows.get(i)[6], t.rows.get(i)[7], t.rows.get(i)[8], t.rows.get(i)[9], t.rows.get(i)[10], t.rows.get(i)[11]);
			}
			break;
		case 13:
			System.out.format("%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%8s%n", t.columnNames[0], t.columnNames[1], t.columnNames[2],
					t.columnNames[3], t.columnNames[4], t.columnNames[5], t.columnNames[6], t.columnNames[7], t.columnNames[8], t.columnNames[9], t.columnNames[10], t.columnNames[11], t.columnNames[12]);
			for (int i = 0; i < rowCount; i++) {
				System.out.format("%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%8d%n", t.rows.get(i)[0], t.rows.get(i)[1], t.rows.get(i)[2],
						t.rows.get(i)[3], t.rows.get(i)[4], t.rows.get(i)[5], t.rows.get(i)[6], t.rows.get(i)[7], t.rows.get(i)[8], t.rows.get(i)[9], t.rows.get(i)[10], t.rows.get(i)[11], t.rows.get(i)[12]);
			}
			break;
		}
	}*/
}
