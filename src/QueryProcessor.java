

import java.util.ArrayList;
import java.util.Arrays;

public class QueryProcessor {

	private static int numTokens;
	private static int numFields;
	private static int numTables;
	private static int numWhereClauses;

	public static void ProcessQuery(String rawQuery) {

		// split query
		rawQuery = rawQuery.toUpperCase();
		String[] query = rawQuery.split("\\s");

		Util.PrintDebug("Starting query processing on: " + Arrays.toString(query));

		// start by getting the metadata of the query and removing commas, semicolons.
		query = GetQueryDataAndCleanup(query);

		// result table
		Table t = null;

		// secondary arrays for code simplicity
		String[] fields = new String[numFields];
		String[] tables = new String[numTables];
		ArrayList<String> whereClauses = new ArrayList<String>();
		ArrayList<String> joinClauses = new ArrayList<String>();

		if (numFields != 0)
			System.arraycopy(query, 1, fields, 0, numFields);
		if (numTables != 0)
			System.arraycopy(query, 2 + numFields, tables, 0, numTables);

		// was originally using array copy here but want to change the where clauses to
		// a list for simplicity.
		// also narrowed this down to this one loop so no deletion has to occur.
		//handle ANDS here instead of in the cleanup method.
		for (int i = 3 + numFields + numTables; i < (3 + numFields + numTables + numWhereClauses); i++) {
			if (query[i].equals("AND")) {
				continue;
			} else if (CheckIfJoinClause(query[i])) {
				joinClauses.add(query[i]);
			} else {
				whereClauses.add(query[i]);
			}
		}
		
		//prior to doing the joins, check the security level
		for(String clause : whereClauses) {
			String[] splitClause = clause.split("=");
			if(splitClause[0].equals("TC")) { //security selection
				int clauseVal = Integer.parseInt(splitClause[1]);
				if(clauseVal > DatabaseManager.securityLevel) {
					System.out.println("Error: Security Level Violation");
					return;
				}
			}
		}
		
		// join tables if necessary. order of joins can matter, specifically the ordering of the identifiers in the where clauses. 
		switch (numTables) {
		case 1: {
			t = DatabaseManager.GetTableByName(tables[0]);
			break;
		}
		case 2: {
			Table t1 = DatabaseManager.GetTableByName(tables[0]);
			Table t2 = DatabaseManager.GetTableByName(tables[1]);

			if (joinClauses.size() == 0) {
				// if there are no join clauses, we are going to do a cartesian product
				t = new Table(t1, t2);
			} else {
				t = new Table(t1, t2, joinClauses.get(0));
			}

			break;
		}
		case 3: {
			Table t1 = DatabaseManager.GetTableByName(tables[0]);
			Table t2 = DatabaseManager.GetTableByName(tables[1]);
			Table t3 = DatabaseManager.GetTableByName(tables[2]);

			if (joinClauses.size() == 0) {
				// if there are no join clauses, we are going to do a cartesian product
				t = new Table(t1, t2);
				t1 = t;
				t = new Table(t1, t3);
			} else if (joinClauses.size() == 1) {
				String[] joinClauseSplit = joinClauses.get(0).split("=");

				// figuring out the order of the join clauses
				if (t1.getColIndexByName(joinClauseSplit[0]) != -1) { // if the first part of the join condition regards
																		// t1
					if (t2.getColIndexByName(joinClauseSplit[1]) != -1) { // second part join = t2
						t = new Table(t1, t2, joinClauses.get(0));
						t1 = t;
						t = new Table(t1, t3);
					} else { // second part join = t3
						t = new Table(t1, t3, joinClauses.get(0));
						t1 = t;
						t = new Table(t1, t2);
					}
				} else if (t2.getColIndexByName(joinClauseSplit[0]) != -1) { // first part join = t2
					if (t2.getColIndexByName(joinClauseSplit[1]) != -1) { // second part join = t1
						t = new Table(t2, t1, joinClauses.get(0));
						t1 = t;
						t = new Table(t1, t3);
					} else { // second part join = t3
						t = new Table(t2, t3, joinClauses.get(0));
						t2 = t;
						t = new Table(t2, t1);
					}
				} else { // first part join = t3
					if (t1.getColIndexByName(joinClauseSplit[1]) != -1) { // second part join = t1
						t = new Table(t3, t1, joinClauses.get(0));
						t1 = t;
						t = new Table(t1, t2);
					} else { // second part join = t2
						t = new Table(t3, t2, joinClauses.get(0));
						t2 = t;
						t = new Table(t2, t1);
					}
				}

			} else if (joinClauses.size() == 2) {
				String[] firstJoinSplit = joinClauses.get(0).split("=");
				String[] secondJoinSplit = joinClauses.get(1).split("=");

				// figure out the order of the first join. set merged table references to null.
				if (t1.getColIndexByName(firstJoinSplit[0]) != -1) {
					if (t2.getColIndexByName(firstJoinSplit[1]) != -1) {
						t = new Table(t1, t2, joinClauses.get(0));
						t1 = null;
						t2 = null;
					} else {
						t = new Table(t1, t3, joinClauses.get(0));
						t1 = null;
						t3 = null;
					}
				} else if (t2.getColIndexByName(firstJoinSplit[0]) != -1) {
					if (t3.getColIndexByName(firstJoinSplit[1]) != -1) {
						t = new Table(t2, t3, joinClauses.get(0));
						t2 = null;
						t3 = null;
					} else {
						t = new Table(t2, t1, joinClauses.get(0));
						t2 = null;
						t1 = null;
					}
				} else if (t3.getColIndexByName(firstJoinSplit[0]) != -1) {
					if (t1.getColIndexByName(firstJoinSplit[1]) != -1) {
						t = new Table(t3, t1, joinClauses.get(0));
						t3 = null;
						t1 = null;
					} else {
						t = new Table(t3, t2, joinClauses.get(0));
						t3 = null;
						t1 = null;
					}
				}

				Table temp = t;
				if (temp.getColIndexByName(secondJoinSplit[0]) != -1) {
					if (t1 != null) {
						t = new Table(temp, t1, joinClauses.get(1));
					} else if (t2 != null) {
						t = new Table(temp, t2, joinClauses.get(1));
					} else {
						t = new Table(temp, t3, joinClauses.get(1));
					}
				} else if (t1 != null) {
					t = new Table(t1, temp, joinClauses.get(1));
				} else if (t2 != null) {
					t = new Table(t2, temp, joinClauses.get(1));
				} else if (t3 != null) {
					t = new Table(t3, temp, joinClauses.get(1));
				}
			}
			break;
		}
		}

		// now that we have done either our inner joins or our cartesian product, we can
		// do selection with the where clauses that aren't joins.
		// optimization - i could've potentially deleted the join clauses from the where
		// clauses array so i don't have to recheck..
		for (String clause : whereClauses) {
			String[] splitClause = clause.split("=");
			int clauseVal = Integer.parseInt(splitClause[1]);
			t = t.select(splitClause[0], clauseVal);
		}
		
		//change in architecture: project will print for us, we will never perform operations on a projected table so we don't need to return a new one.
		t.project(fields);
		Util.PrintDebug("Returned " + t.getRowCount() + " rows. Showing top " + Math.min(t.getRowCount(), 100) + ".");
	}

	private static String[] GetQueryDataAndCleanup(String[] query) {
		numTokens = query.length;
		numFields = 0;
		numTables = 0;
		numWhereClauses = 0;

		// find number of fields to select
		for (int i = 1; i < numTokens; i++) {
			if (query[i].equals("FROM")) {
				break;
			} else if (query[i].charAt(query[i].length() - 1) == ',') { // comma separated lists, get rid of
																		// commas
				numFields++;
				query[i] = query[i].substring(0, query[i].length() - 1);
			} else {
				numFields++;
			}
		}

		// find number of tables to select from
		for (int i = 2 + numFields; i < numTokens; i++) {
			if (query[i].equals("WHERE")) {
				break;
			} else if (query[i].charAt(query[i].length() - 1) == ',') { // comma separated lists, get rid
																		// of commas
				numTables++;
				query[i] = query[i].substring(0, query[i].length() - 1);
			} else {
				numTables++;
			}
		}

		// find number of where clauses (NOW INCLUDES AND TOKENS)
		for (int i = 3 + numFields + numTables; i < numTokens; i++) {
			numWhereClauses++;
		}

		//remove semi-colon
		if (query[numTokens - 1].charAt(query[numTokens - 1].length() - 1) == ';') {
			query[numTokens - 1] = query[numTokens - 1].substring(0, query[numTokens - 1].length() - 1);
		}
		
		Util.PrintDebug("Query processor found: " + numTokens + " tokens, " + numFields + " selection fields, "
				+ numTables + " tables, and " + numWhereClauses + " where clauses.");
		Util.PrintDebug("Cleaned up tokens: " + Arrays.toString(query));
		
		return query;
	}

	private static boolean CheckIfJoinClause(String whereClauseToken) {
		String[] splitClause = whereClauseToken.split("=");

		// we know whether or not a where clause is a join based off whether both values
		// in the clause are column names or if the second one is a number.
		if (Util.IsNumeric(splitClause[1])) {
			return false;
		}

		return true;
	}
}