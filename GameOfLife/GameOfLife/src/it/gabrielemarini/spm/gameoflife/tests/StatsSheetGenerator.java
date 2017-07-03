package it.gabrielemarini.spm.gameoflife.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import it.gabrielemarini.spm.gameoflife.tests.TestResult.TestType;

/**
 * @author Gabryxx7
 * Class that generates a worksheet excel file with the obtained results
 */
public class StatsSheetGenerator {
	
	private static Workbook wb;
	
	public static void init() {
		wb = new XSSFWorkbook();
	}

	public static void writeFile() throws Exception {
		String dateString = new SimpleDateFormat("ddMMyy_HHmmss").format(new Date());
		String file = "stats/Statistics" +dateString+".xls";
		File statsFolder = new File("stats");
		boolean success = statsFolder.mkdirs();
		if (!success && !statsFolder.exists()) {
		    System.out.println("Couldn't create the \"stats\" folder, storing results on the project root folder");
		    file = "Statistics" +dateString+".xls";
		}
		
		if (wb instanceof XSSFWorkbook)
			file += "x";
		
		FileOutputStream out = new FileOutputStream(file);
		wb.write(out);
		out.close();
	}
	
	
	public static void generate(HashMap<TestType, TestResult> results, String[] boardsSizes, int nThreads, int iterations, int nTests) throws Exception {
		init();
		int boardsNum = boardsSizes.length;
		
		for (HashMap.Entry<TestType, TestResult> entry : results.entrySet()){			
			if(entry.getKey() != TestType.SEQUENTIAL){
				Sheet sheet = wb.createSheet(entry.getKey().getTitle());
				Row headerRow = sheet.createRow(0);
				int emptyColumns = 3;
				int execTimeCol = 0 * (boardsNum+emptyColumns);
				int speedupCol = 1 * (boardsNum+emptyColumns);
				int scalabilityCol = 2 * (boardsNum+emptyColumns);
				int efficiencyCol = 3 * (boardsNum+emptyColumns);
				
				headerRow.createCell(execTimeCol).setCellValue("Execution Time");
				headerRow.createCell(speedupCol).setCellValue("Speedup");
				headerRow.createCell(scalabilityCol).setCellValue("Scalability");
				headerRow.createCell(efficiencyCol).setCellValue("Efficiency");			

				Row boardSizeRow = sheet.createRow(1);
				Row seqTimeRow = sheet.createRow(2);				

				boardSizeRow.createCell(speedupCol+boardsNum+1).setCellValue("Ideal");
				boardSizeRow.createCell(scalabilityCol+boardsNum+1).setCellValue("Ideal");
				boardSizeRow.createCell(efficiencyCol+boardsNum+1).setCellValue("Ideal");	
				
				for(int i = 0; i < boardsNum; i++){
					//Execution Time board sizes and iterations
					headerRow.createCell(execTimeCol+1+i).setCellValue(nTests +"*" +iterations);
					boardSizeRow.createCell(execTimeCol+1+i).setCellValue(boardsSizes[i]);

					//Speedup board sizes and iterations
					headerRow.createCell(speedupCol +1+i).setCellValue(nTests +"*" +iterations);
					boardSizeRow.createCell(speedupCol +1+i).setCellValue(boardsSizes[i]);

					//Scalability board sizes and iterations
					headerRow.createCell(scalabilityCol +1+i).setCellValue(nTests +"*" +iterations);
					boardSizeRow.createCell(scalabilityCol +1+i).setCellValue(boardsSizes[i]);

					//Efficiency board sizes and iterations
					headerRow.createCell(efficiencyCol +1+i).setCellValue(nTests +"*" +iterations);	
					boardSizeRow.createCell(efficiencyCol +1+i).setCellValue(boardsSizes[i]);	
					
					long seqTime = results.get(TestType.SEQUENTIAL).getAverageTime(boardsSizes[i], 0);
					long singleThreadTime = 0;
					seqTimeRow.createCell(execTimeCol).setCellValue("SeqTime");
//					seqTimeRow.createCell(speedupCol).setCellValue("SeqTime");
//					seqTimeRow.createCell(scalabilityCol).setCellValue("SeqTime");
//					seqTimeRow.createCell(efficiencyCol).setCellValue("SeqTime");					
					seqTimeRow.createCell(execTimeCol+1+i).setCellValue(seqTime);
					
					int rowPos = 3;
					Row printRow = null;
					for(int t = 1; t <= nThreads; t++){			
						if(i == 0)
							printRow = sheet.createRow(rowPos);
						else printRow = sheet.getRow(rowPos);
						
						long timeToPrint = entry.getValue().getAverageTime(boardsSizes[i], t);
//						System.out.println("Printing: " +timeToPrint +" in cell(" +rowPos +"," + (1+i) +") of sheet " +sheet.getSheetName());

						if(t == 1)
							singleThreadTime = timeToPrint;

						printRow.createCell(execTimeCol).setCellValue(t);
						printRow.createCell(speedupCol).setCellValue(t);
						printRow.createCell(scalabilityCol).setCellValue(t);
						printRow.createCell(efficiencyCol).setCellValue(t);
						
						printRow.createCell(execTimeCol+1+i).setCellValue(timeToPrint);
						
						printRow.createCell(speedupCol+1+i).setCellValue((double) seqTime / timeToPrint);
						printRow.createCell(speedupCol+boardsNum+1).setCellValue(seqTime / (seqTime/t));	//Ideal speedup
						
						if(t > 1){
							printRow.createCell(scalabilityCol+1+i).setCellValue((double) singleThreadTime / timeToPrint);
							printRow.createCell(scalabilityCol+boardsNum+1).setCellValue(singleThreadTime / (singleThreadTime/t));	//Ideal scalability
							
							printRow.createCell(efficiencyCol+1+i).setCellValue((double) seqTime/ (t * timeToPrint));
							printRow.createCell(efficiencyCol+boardsNum+1).setCellValue(seqTime/ (t * (seqTime/t)));		//Ideal efficiency	
						}
						
						rowPos++;
					}
				}
				sheet.autoSizeColumn(0);
			}
		}		
		writeFile();
	}


}
