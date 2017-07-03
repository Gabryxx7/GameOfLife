package it.gabrielemarini.spm.gameoflife.tests;

import java.awt.BorderLayout;

import java.awt.Graphics;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.JFrame;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import it.gabrielemarini.spm.gameoflife.Board;
import it.gabrielemarini.spm.gameoflife.GraphicBoard;
import it.gabrielemarini.spm.gameoflife.java8.GoLJava8;
import it.gabrielemarini.spm.gameoflife.javathreads.GoLJavaThreads;
import it.gabrielemarini.spm.gameoflife.sequential.GoLSequential;
import it.gabrielemarini.spm.gameoflife.skandium.GoLSkandium;
import it.gabrielemarini.spm.gameoflife.tests.TestResult.TestType;

/**
 * @author Gabryxx7
 * Class for executing the tests and evaluating performance
 */
public class Tests {
	/** Versions of the game **/
	private static byte SEQUENTIAL = 0;
	private static byte JAVATHREADS = 1;
	private static byte SKANDIUM = 2;
	private static byte JAVA8 = 3;
	private static byte ALLVERSIONS = 4;

	private static boolean DEBUGGING = false; //Used for printing message for debugging purposes

	private static HelpFormatter formatter = new HelpFormatter();
	private static CommandLineParser parser = new DefaultParser();
	private static Options options = new Options();
	private static final String headerMsg =   "\n\t _______  _______  __   __  _______    _______  _______    ___      ___   _______  _______\n"+ 
			"\t|       ||   _   ||  |_|  ||       |  |       ||       |  |   |    |   | |       ||       |\n"+
			"\t|    ___||  |_|  ||       ||    ___|  |   _   ||    ___|  |   |    |   | |    ___||    ___|\n"+
			"\t|   | __ |       ||       ||   |___   |  | |  ||   |___   |   |    |   | |   |___ |   |___ \n"+
			"\t|   ||  ||       ||       ||    ___|  |  |_|  ||    ___|  |   |___ |   | |    ___||    ___|\n"+
			"\t|   |_| ||   _   || ||_|| ||   |___   |       ||   |      |       ||   | |   |    |   |___ \n"+
			"\t|_______||__| |__||_|   |_||_______|  |_______||___|      |_______||___| |___|    |_______|\n";



	/**
	 * Initialize the command line for receiving the possible arguments
	 */
	public static void commandLineInit() {    
		options.addOption(Option.builder("s").argName("10x10,50x50,800x800...").longOpt("size")
				.desc("Boards sizes, in the form HxW").hasArgs().valueSeparator(',').required().build());
		options.addOption(Option.builder("t").argName("4").longOpt("threads")
				.desc("Maximum number of threads. If not specified, uses all available threads").hasArg().build());
		options.addOption(Option.builder("i").argName("1000").longOpt("iterations")
				.desc("Number of iterations for every board").hasArg().required().build());
		options.addOption(Option.builder("n").argName("30").longOpt("number")
				.desc("Number of times to execute the test").hasArg().required().build());
		options.addOption(Option.builder("v").argName("seq, jt, sk, j8").longOpt("version")
				.desc("Version of the game to execute. If not specified executes all possible versions (test) and print a file with all the results").hasArg().build());
		options.addOption("g", "graphics", false, "Display graphics, disabled by default");
		options.addOption("h", "help", false, "Print help message");
	}


	/**
	 * @param args
	 * @throws Exception Possible exceptions during the execution of one of the game versions
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(headerMsg);
		commandLineInit();		//Initialize the command line

		try{
			/*** Command Line Parsing and arguments initialization **/
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("h")) {
				formatter.printHelp("GameOfLife", options, true);	
			}
			else if(line.hasOption("s") && line.hasOption("i") && line.hasOption("n")){

				String[] boardsSizes = line.getOptionValues("s"); //Boards sizes strings
				int[] boardsHeights = new int[boardsSizes.length]; //Boards heights
				int[] boardsWidths = new int[boardsSizes.length]; //Boards widths
				int iterations = 0; //Number of iterations to execute
				int nThreads = Runtime.getRuntime().availableProcessors(); //Maximum number of threads, by default is equal to the maximum available threads
				int version = ALLVERSIONS; //Version of the game to execute
				int nTests = 10;
				boolean graphics = false; //Boolean for showing the graphical board
				long execTime = 0;

				for(int i = 0; i < boardsSizes.length; i++){
					if(!boardsSizes[i].contains("x")){
						System.err.println("Every board size must be in the form \"ROWSxCOLS\", e.g \"1000x1000\"");
						System.exit(1);
					}

					String[] parts = boardsSizes[i].split("x");
					try{
						boardsHeights[i] = Integer.parseInt(parts[0]);
						boardsWidths[i] = Integer.parseInt(parts[1]);	
					}catch(NumberFormatException e){
						System.err.println("Boards sizes must be in the fom HxW where H and W are numbers");
						System.exit(1);
					}
				}

				try{
					iterations = Integer.parseInt(line.getOptionValue("i"));
				}catch(NumberFormatException e){
					System.err.println("Iterations number must be a number (that was unexpected)");
					System.exit(1);
				}

				if(line.hasOption("t")){
					try{
						nThreads = Integer.parseInt(line.getOptionValue("t"));
					}catch(NumberFormatException e){
						System.err.println("Threads number must be a number (that was unexpected)");
						System.exit(1);
					}
				}

				if(line.hasOption("n")){
					try{
						nTests = Integer.parseInt(line.getOptionValue("n"));
					}catch(NumberFormatException e){
						System.err.println("The number of tests must be a number (that was unexpected)");
						System.exit(1);
					}
				}

				if(line.hasOption("v")){
					switch(line.getOptionValue("v")){
					case "seq":
						version = SEQUENTIAL;
						break;

					case "jt":
						version = JAVATHREADS;
						break;							

					case "sk":
						version = SKANDIUM;
						break;

					case "j8":
						version = JAVA8;
						break;

					default:
						System.err.println("Wrong code only the followings are allowed: seq, jt, sk, j8");
						System.exit(1); 
					}
				}

				if(line.hasOption("g"))					
					graphics = true;

				/*** All the arguments and settings have now been set ***/
				/*** Boards declaration and initialization of the object for storing the results **/		
				Board backupBoard = new Board();	//Used to store the original board so that every version will execute the SAME IDENTICAL board
				Board testingBoard;	//The board actually used by every version of the game
				Board solutionBoard = null;	//Used for debugging purposes to check the correctness of the computation for every version
				JFrame frame = null;				

				HashMap<TestType, TestResult> results = new HashMap<TestType, TestResult>();
				TestResult seqResults = new TestResult(TestType.SEQUENTIAL);
				TestResult jtResults = new TestResult(TestType.JAVATHREADS);
				TestResult skResults = new TestResult(TestType.SKANDIUM);
				TestResult j8Results = new TestResult(TestType.JAVA8);				

				for(int i = 0; i < nTests; i++){
					for(int n = 0; n < boardsSizes.length; n++){
						backupBoard.initializeRandomBoard(boardsHeights[n], boardsWidths[n]);	//The backup Board will be used for reinitializing the testingBoard
						testingBoard = new Board(boardsHeights[n], boardsWidths[n]);	//Board passed to each different version of the game					
						testingBoard.copyFromBoard(backupBoard);	//the testingBoard must be identical for every version of the game

						if(DEBUGGING)
							solutionBoard = new Board(boardsHeights[n], boardsWidths[n]);	//Board used to test the correctness of the results

						System.out.println("\n\n**** Run " +(i+1) +"/" +nTests +" - Testing " +boardsSizes[n] +" Board - " +iterations+ " iterations ****");


						/** Initializing the graphical board if the option has been selected **/
						if(graphics){
							if(frame == null)
								frame = new JFrame("Game of Life");	
							Graphics g = frame.getGraphics();
							frame.pack();
							Insets insets = frame.getInsets();
							frame.getContentPane().add(new GraphicBoard(testingBoard), BorderLayout.CENTER);
							frame.paint(g);
							frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							frame.setSize(insets.left + insets.right + testingBoard.getWidth(), insets.top + insets.bottom + testingBoard.getHeight());
							frame.setVisible(true);								
						}	

						/** SEQUENTIAL version of the game **/
						if(version == SEQUENTIAL || version == ALLVERSIONS){	
							System.out.print("Sequential: \t" +"Executing...");

							execTime = GoLSequential.execute(testingBoard, iterations);
							System.out.println("\t" +execTime + " ms");

							seqResults.insertTime(boardsSizes[n], 0, execTime);

							if(DEBUGGING)
								solutionBoard.copyFromBoard(testingBoard);						
						}

						/** Each parallel implementation of the game will be executed using an amount of threads, starting from 1 to the
						 * maximum number of threads selected or available on the machine */
						for(int t = 1; t <= nThreads; t++){		
							System.out.println("------------  " +t +" Thread(s)  ------------");

							/*** JAVA THREADS version of the game ***/
							testingBoard.copyFromBoard(backupBoard); //Restoring the initial state of the board
							if(version == JAVATHREADS || version == ALLVERSIONS){

								System.out.print("Java Threads: \t" +"Executing...");
								execTime = GoLJavaThreads.execute(testingBoard, iterations, t); 
								System.out.println("\t" +execTime + " ms");	

								jtResults.insertTime(boardsSizes[n], t, execTime);							

								//If the sequential version has been executed and debugging is enabled
								if(version == ALLVERSIONS && DEBUGGING){
									if(testingBoard.compareTo(solutionBoard))
										System.out.println("Risultato:\t" + "Corretto");	
									else System.out.println("Risultato:\t" + "NON CORRETTO");
								}
							}					

							/*** SKANDIUM version of the game ***/
							testingBoard.copyFromBoard(backupBoard);
							if(version == SKANDIUM || version == ALLVERSIONS){

								System.out.print("Skandium: \t" +"Executing...");

								/** Skandium version that loops the map skeleton getting each time the results by calling
								 * future.get()
								 */
								execTime = GoLSkandium.execute(testingBoard, iterations, t);	
								System.out.println("\t" + execTime + " ms");								

								skResults.insertTime(boardsSizes[n], t, execTime);

								//If the sequential version has been executed and debugging is enabled
								if(version == ALLVERSIONS && DEBUGGING){
									if(testingBoard.compareTo(solutionBoard))
										System.out.println("Risultato:\t" + "Corretto");	
									else System.out.println("Risultato:\t" + "NON CORRETTO");
								}
							}	

							/*** JAVA8 version of the game ***/
							testingBoard.copyFromBoard(backupBoard); //Restoring the board initial value
							if(version == JAVA8 || version == ALLVERSIONS){

								System.out.print("Java8: \t\t" +"Executing...");
								execTime = GoLJava8.execute(testingBoard, iterations-1, t);
								System.out.println("\t" + execTime + " ms");									

								j8Results.insertTime(boardsSizes[n], t, execTime);							

								//If the sequential version has been executed and debugging is enabled
								if(version == ALLVERSIONS && DEBUGGING){
									if(testingBoard.compareTo(solutionBoard))
										System.out.println("Risultato:\t" + "Corretto");	
									else System.out.println("Risultato:\t" + "NON CORRETTO");
								}
							}	
						}
					}
				}

				if(version == ALLVERSIONS){
					results.put(TestType.SEQUENTIAL, seqResults);
					results.put(TestType.JAVATHREADS, jtResults);
					results.put(TestType.SKANDIUM, skResults);
					results.put(TestType.JAVA8, j8Results);

					StatsSheetGenerator.generate(results, boardsSizes, nThreads, iterations, nTests);
					System.out.println("\n*** Statistics table file Generated ***");
				}				
				System.out.println("\n*** ALL TESTS HAVE BEEN COMPLETED ***\n");
			}			
			else{
				System.err.println("The arguments \"size\", \"iterations\" and \"number\" are required");
			}
		} catch (ParseException exp) {
			formatter.printHelp("GameOfLife", options, true);
			System.err.println("The arguments \"size\", \"iterations\" and \"number\" are required");
		}
		System.exit(0);
	}
}
