package data_structures;

import java.util.*;
/*
 * Select randomly, but with a higher preference for those with a higher score.
 */
public class RandomSelector <T> {
	
	private ArrayList <T> objects =  new ArrayList<>(); //Stores the objects
	private ArrayList <Double> scores = new ArrayList<Double>(); //The scores for each object
	
	private double total_score = 0; 
	
	public void add(T element, double score) {
		objects.add(element);
		scores.add(score);
		total_score += score;
		
	}
	
	//Imagine the total_score as a number line segmented by the scores of each object.
	//The larger an object's score is, the more likely the threshold will land in the object's range
	//We pick the object whose score the threshold lands in.
	public T random() {
		double threshold = Math.random() * total_score;
		
		double sum = 0;
		for(int i = 0; i< objects.size();i++) {
			sum += scores.get(i);
			if(sum > threshold) {
				return objects.get(i);
			}
		}
		return null;
		
	}
	
	
	
}
