package data_structures;

import java.util.*;

import genome.Gene;

public class RandomHashSet<T> {
	
	//Both set and data contain the same information, we just use each one for different purposes
	HashSet<T> set; //This is for checking if an element exists O(1) search
	ArrayList<T> data; //We will use this to actually access the data since it has indices
	
	public RandomHashSet() {
		set = new HashSet<>();
		data = new ArrayList<>();
		
	}

	public boolean contains(T object) {
		return set.contains(object);
	}
	
	public T randomElement() {
		if(set.size()>0) {
			return data.get((int)(data.size()*Math.random()));
		}
		return null;
	}
	
	public int size() {
		return data.size();
	}
	
	public void add(T object) {
		if(!set.contains(object)) {
			set.add(object);
			data.add(object);
		}
	}
	
	//Adds a gene in a sorted fashion
	public void addSorted(Gene object) {
		for(int i =0 ;i< this.size(); i++) {
			int innovationNumber = ((Gene)data.get(i)).getInnovationNumber();
			if(object.getInnovationNumber() < innovationNumber) {
				data.add(i, (T)object);
				set.add((T)object);
				return;
			}
		}
		//if the object's innovation number is larger than all the previous ones
		data.add((T)object);
		set.add((T)object);
		return;
		
	}
	
	public void clear() {
		set.clear();
		data.clear();
	}
	
	public T get(int index) {
		if(index < 0 || index >= size()) return null;
		return data.get(index);
	}
	
	public void remove(int index) {
		if(index < 0 || index >= size()) return;
		set.remove(data.get(index));
		data.remove(index);
	}
	
	public void remove(T object) {
		set.remove(object);
		data.remove(object);
	}

	public HashSet<T> getSet() {
		return set;
	}

	public void setSet(HashSet<T> set) {
		this.set = set;
	}

	public ArrayList<T> getData() {
		return data;
	}

	public void setData(ArrayList<T> data) {
		this.data = data;
	}
	
	
	
}
