

import java.util.ArrayList;
import java.util.Arrays;

public class Table {

	public String[] columnNames;
	public ArrayList<int[]> rows;
	public ArrayList<Integer> pkLocations = new ArrayList<Integer>(); // made this into a list to support multiple pks
	// and kcs
	public ArrayList<Integer> kcLocations = new ArrayList<Integer>();
	public int tcLocation;
	public String tableName;

	public Table(String[] colNames, ArrayList<int[]> rows, int kcLocation, int tcLocation, String tableName) {
		this.columnNames = colNames;
		this.rows = rows;
		this.kcLocations.add(kcLocation);
		this.pkLocations.add(0);
		this.tcLocation = tcLocation;
		this.tableName = tableName;
	}

	public Table(String[] colNames, ArrayList<int[]> rows, ArrayList<Integer> pkLocations,
			ArrayList<Integer> kcLocations, int tcLocation, String tableName) {
		this.columnNames = colNames;
		this.rows = rows;
		this.pkLocations = pkLocations;
		this.kcLocations = kcLocations;
		this.tcLocation = tcLocation;
		this.tableName = tableName;
	}

	// Cartesian product operation
	public Table(Table t1, Table t2) {

		int t1Width = t1.getColCount();
		int t2Width = t2.getColCount();
		int rowWidth = t1Width + t2Width - 1; // subtract one for tc

		columnNames = new String[rowWidth];
		rows = new ArrayList<int[]>();

		// column combine
		for (int i = 0; i < t1Width - 1; i++) {
			columnNames[i] = t1.columnNames[i];
			if (columnNames[i].equals("KC")) { // this used to be based on the index of this column. maintain the
				// indices of the kcs.
				kcLocations.add(i);
				columnNames[i] = t1.tableName + "." + columnNames[i];
			} else if (columnNames[i].contains("KC")) { // possible bug: what if more than 2 tables being merged? this
				// might fix that. if it doesn't equal kc but it contains kc,
				// then we don't need to rename but we do need to maintain index
				// reference.
				kcLocations.add(i);
			} else if (t1.isPrimaryKey(columnNames[i])) {
				// if its a primary key in t1, update our metadata
				pkLocations.add(i);
			}
		}
		for (int i = t1Width - 1; i < rowWidth; i++) {
			columnNames[i] = t2.columnNames[i - t1Width + 1];
			if (columnNames[i].equals("KC")) {
				kcLocations.add(i);
				columnNames[i] = t2.tableName + "." + columnNames[i];
			} else if (columnNames[i].contains("KC")) {
				kcLocations.add(i);
			} else if (t2.isPrimaryKey(columnNames[i])) {
				// if its a primary key in t2, update our metadata
				pkLocations.add(i);
			}
		}

		for (int rowT1 = 0; rowT1 < t1.getRowCount(); rowT1++) {
			for (int rowT2 = 0; rowT2 < t2.getRowCount(); rowT2++) {

				int[] row = new int[rowWidth];
				System.arraycopy(t1.rows.get(rowT1), 0, row, 0, t1Width);
				System.arraycopy(t2.rows.get(rowT2), 0, row, t1Width - 1, t2Width - 1);

				// maintain max tc
				int t1Tc = t1.rows.get(rowT1)[t1Width - 1];
				int t2Tc = t2.rows.get(rowT2)[t2Width - 1];
				if (t1Tc > t2Tc) {
					row[rowWidth - 1] = t1Tc;
				} else {
					row[rowWidth - 1] = t2Tc;
				}

				rows.add(row);
			}
		}
	}

	// inner join operation
	public Table(Table t1, Table t2, String joinClause) {

		int t1Width = t1.getColCount();
		int t2Width = t2.getColCount();
		int rowWidth = t1Width + t2Width - 1; // subtract one for tc

		columnNames = new String[rowWidth];
		rows = new ArrayList<int[]>();
		String[] splitJoin = joinClause.split("=");

		// column combine
		for (int i = 0; i < t1Width - 1; i++) {
			columnNames[i] = t1.columnNames[i];
			if (columnNames[i].equals("KC")) {
				columnNames[i] = t1.tableName + "." + columnNames[i];
				kcLocations.add(i);
			} else if (columnNames[i].contains("KC")) { // possible bug: what if more than 2 tables being merged? this
				// might fix that. if it doesn't equal kc but it contains kc,
				// then we don't need to rename but we do need to maintain index
				// reference.
				kcLocations.add(i);
			} else if (t1.isPrimaryKey(columnNames[i])) {
				// if its a primary key in t1, update our metadata
				pkLocations.add(i);
			}
		}
		for (int i = t1Width - 1; i < rowWidth; i++) {
			columnNames[i] = t2.columnNames[i - t1Width + 1];
			if (columnNames[i].equals("KC")) {
				columnNames[i] = t2.tableName + "." + columnNames[i];
				kcLocations.add(i);
			} else if (columnNames[i].contains("KC")) {
				kcLocations.add(i);
			} else if (t2.isPrimaryKey(columnNames[i])) {
				// if its a primary key in t2, update our metadata
				pkLocations.add(i);
			}
		}

		for (int rowT1 = 0; rowT1 < t1.getRowCount(); rowT1++) {
			for (int rowT2 = 0; rowT2 < t2.getRowCount(); rowT2++) {
				if (t1.rows.get(rowT1)[t1.getColIndexByName(splitJoin[0])] == t2.rows.get(rowT2)[t2.getColIndexByName(splitJoin[1])]
						&& t1.rows.get(rowT1)[t1.kcLocations.get(0)] == t2.rows.get(rowT2)[t2.kcLocations.get(0)]) {

					int[] row = new int[rowWidth];
					System.arraycopy(t1.rows.get(rowT1), 0, row, 0, t1Width);
					System.arraycopy(t2.rows.get(rowT2), 0, row, t1Width - 1, t2Width - 1);

					// maintain max tc
					int t1Tc = t1.rows.get(rowT1)[t1Width - 1];
					int t2Tc = t2.rows.get(rowT2)[t2Width - 1];
					if (t1Tc > t2Tc) {
						row[rowWidth - 1] = t1Tc;
					} else {
						row[rowWidth - 1] = t2Tc;
					}

					rows.add(row);
				}
			}
		}
	}

	/**
	 * 
	 * Eliminates rows on the given table based on the column and value passed and
	 * returns the result as a new table
	 * 
	 * @param col
	 * @param value
	 * @return
	 */
	public Table select(String col, int value) {

		ArrayList<int[]> rows = new ArrayList<int[]>();

		int rowCount = getRowCount();
		int colIndex = getColIndexByName(col);

		// given a faulty column, don't do any selection and instead return the original
		// table.
		if (colIndex == -1)
			return this;

		for (int i = 0; i < rowCount; i++) {
			if (this.rows.get(i)[colIndex] == value) {
				rows.add(this.rows.get(i));
			}
		}

		return new Table(this.columnNames, rows, this.pkLocations, this.kcLocations, this.tcLocation, this.tableName);
	}

	/**
	 * Prints the table with the passed projection columns.
	 * 
	 * @param columns The columns to project.
	 */
	public void project(String[] columns) {
		// projecting all columns, print all with no limit.
		if (columns[0].equals("*")) {
			print(false);
			return;
		}

		boolean[] projectMask = new boolean[getColCount()];
		int colCount = 1;

		for (int i = 0; i < getColCount(); i++) {
			if (Arrays.asList(columns).contains(columnNames[i])) {
				projectMask[i] = true;
				colCount++;
			} else {
				projectMask[i] = false;
			}
		}

		// make sure that the kcs are included when projecting a primary key.
		for (int i = 0; i < getColCount(); i++) {
			if (projectMask[i] && isPrimaryKey(columnNames[i])) {
				projectMask[i + 1] = true;
				colCount++;
			}
		}

		projectMask[getColCount() - 1] = true;
		TableFormatter.PrintProjection(this, false, projectMask, colCount);
	}

	public int getRowCount() {
		return rows.size();
	}

	public int getColCount() {
		return columnNames.length;
	}

	// returns -1 if not found.
	public int getColIndexByName(String col) {
		for (int i = 0; i < getColCount(); i++) {
			if (columnNames[i].equals(col)) {
				return i;
			}
		}
		return -1;
	}

	public boolean isPrimaryKey(String col) {
		int i = getColIndexByName(col);
		for (int loc : pkLocations) {
			if (i == loc) {
				return true;
			}
		}

		return false;
	}

	// the following assumes the property: the index of the kc will always be the
	// index of the pk plus 1.
	public int getKCIndexByPK(String pk) {
		return getColIndexByName(pk) + 1;
	}

	public void print(boolean limit) {
		TableFormatter.Print(this, false);
	}

	public void print() {
		TableFormatter.Print(this, true);
	}

}
