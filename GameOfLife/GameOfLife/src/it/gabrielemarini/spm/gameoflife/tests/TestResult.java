package it.gabrielemarini.spm.gameoflife.tests;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author Gabryxx7
 * Class for storing the results of each test
 */
public class TestResult {
	public enum TestType {
		SEQUENTIAL("Sequential"), SKANDIUM("Skandium"), JAVATHREADS("Java Threads"), JAVA8("Java8");
		
		private String title;
		
		public String getTitle(){
			return this.title;
		}

		private TestType(String title) {
			this.title = title;
		}
	}		
	
	private LinkedHashMap<String, ArrayList<Long>> times;
	private LinkedHashMap<String, Long> averages;
	public TestType type;
	
	public TestResult(TestType type){
		this.type = type;
		times = new LinkedHashMap<String, ArrayList<Long>>();
		averages = new LinkedHashMap<String, Long>();
	}
		
	/**
	 * Insert a new service time in the array of times, this is needed because of the amount of tests
	 * that need to be done in order to get a precise average
	 * 
	 * @param boardSize
	 * @param numThreads
	 * @param time
	 */
	public void insertTime(String boardSize, int numThreads, long time){
		if(!this.times.containsKey(boardSize+numThreads)){
			this.times.put(boardSize+numThreads, new ArrayList<Long>());	
		}
		this.times.get(boardSize+numThreads).add(time);
	}
	
	/**
	 * Return the times array
	 * 
	 * @param boardSize
	 * @param numThreads
	 * @return
	 */
	public ArrayList<Long> getTimes(String boardSize, int numThreads){
		return this.times.get(boardSize+numThreads);
	}
	
	/**
	 * If the average time has not been computed, it computes by first removing
	 * the highest and lowest time from the array of times. If the average has already been computed
	 * it will simply return its value
	 * 
	 * @param boardSize
	 * @param numThreads
	 * @return
	 */
	public long getAverageTime(String boardSize, int numThreads){
		if(!averages.containsKey(boardSize+numThreads)){
			ArrayList<Long> timesList = this.times.get(boardSize+numThreads);
			if(timesList.size() > 3){
				long max = timesList.get(0);
				int maxIdx = 0;
				
				for(int j = 0; j < timesList.size(); j++){
					if(timesList.get(j) >= max){
						max = timesList.get(j);
						maxIdx = j;
					}
				}
				timesList.remove(maxIdx);
		
				long min = timesList.get(0);
				int minIdx = 0;
				for(int j = 0; j < timesList.size(); j++){			
					if(timesList.get(j) <= min){
						min = timesList.get(j);
						minIdx = j;
					}
				}
				timesList.remove(minIdx);
			}
			
			long sum = 0;
			for(int i = 0; i < timesList.size(); i++){
				sum = sum + timesList.get(i);
			}
			averages.put(boardSize+numThreads, sum/timesList.size());
		}
		
		return averages.get(boardSize+numThreads);
	}
}
