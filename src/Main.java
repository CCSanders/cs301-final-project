import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	static int classLevel;
	static int inputType = 0; // 0 : sysin, 1 : stream from file, 2 : test cases
	
	public static boolean debugMode = false;

	public static void main(String[] args) {
		
		if(args.length == 0) {
			DatabaseManager.LoadTables();
		}else if(args.length < 3){
			System.out.println("Please use either 0 or 3 arguments for the three tables to load.");
			System.exit(-1);
		}else {
			DatabaseManager.LoadTables(args[0], args[1], args[2]);
			
			if(args.length == 4) {
				inputType = 1;
			}
		}

		
		switch(inputType) {
		case 0:
			System.out.println("Please enter your security level: ");
			Scanner in = new Scanner(System.in);
			
			try {
				classLevel = Integer.parseInt(in.nextLine());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			DatabaseManager.securityLevel = classLevel;
			
			System.out.println("Please enter your query: ");
			String query = in.nextLine();
			System.out.println();
			
			QueryProcessor.ProcessQuery(query);
			
			in.close();
			break;
		case 1:
			try {
				//open up the input file for the fourth argument, stream thru all the queries.
				File f = new File(args[3]);
				Scanner inFile = new Scanner(f);
				
				while(inFile.hasNextLine()) {
					//get the classLevel
					try {
						classLevel = Integer.parseInt(inFile.nextLine());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}

					DatabaseManager.securityLevel = classLevel;
					
					String fileQuery = inFile.nextLine();
					
					System.out.println(classLevel);
					System.out.println(fileQuery);
					System.out.println();
					
					QueryProcessor.ProcessQuery(fileQuery);
					
					System.out.println();
				}

				inFile.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 2:
			RunTestCases();
			break;
		}

	}
	
	private static void RunTestCases() {
		DocTests();
		//CustomTests();
	}
	
	private static void DocTests() {
		TestQuery(4, "SELECT C2 FROM T3 WHERE C3=50;");
		TestQuery(3, "SELECT C2 FROM T3 WHERE C3=50;");
		TestQuery(2, "SELECT C2 FROM T3 WHERE C3=50;");
		TestQuery(3, "SELECT C2 FROM T3 WHERE C3=50 and TC=2;");
		TestQuery(3, "SELECT C2 FROM T3 WHERE C3=50 and TC=4;");
		TestQuery(3, "SELECT * FROM T2 WHERE B2=54;");
		TestQuery(4, "SELECT C1, C3 FROM T1, T3 WHERE A1=C4 and A2=425;");
		TestQuery(4, "SELECT * FROM T1, T3 WHERE A1=C4 and A2=425;");
	}
	
	@SuppressWarnings("unused")
	private static void CustomTests() {
		TimedTestQuery(4, "SELECT * FROM T1, T3");
		TimedTestQuery(3, "SELECT A1, A2, B3, C3 FROM T1, T2, T3 WHERE A1=C1 AND A1=B1;");
	}
	
	private static void TestQuery(int security, String query) {
		System.out.println(security);
		System.out.println(query);
		DatabaseManager.securityLevel = security;
		QueryProcessor.ProcessQuery(query);
		System.out.println();
		System.out.println();
	}
	
	private static void TimedTestQuery(int security, String query) {
		
		long startTime = System.currentTimeMillis();
		
		System.out.println(security);
		System.out.println(query);
		
		DatabaseManager.securityLevel = security;
		QueryProcessor.ProcessQuery(query);
		
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		
		System.out.println();
		System.out.println("Query Duration: " + duration + "ms");
		System.out.println();
		System.out.println();
	}
}