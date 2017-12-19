

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.util.FastMath;


//Reference: https://github.com/tgsmith61591/clust4j/blob/master/src/main/java/com/clust4j/data/TrainTestSplit.java
public class TrainTestSplit {
	final private ArrayList<PaperItem> train;
	final private ArrayList<PaperItem> test = new ArrayList<PaperItem>();

	/**
	 * Split a dataset into a train-test split. Leverages {@link DataSet#shuffle()}
	 * to ensure the most random split possible
	 * 
	 * @param data
	 * @param train_ratio
	 */
	public TrainTestSplit(ArrayList<PaperItem> data, double train_ratio, int threshold) {
		final int m = data.size();
		ArrayList<PaperItem> current_data = new ArrayList<PaperItem>(data);

		// validate the ratio...
		if (train_ratio <= 0.0 || train_ratio >= 1.0) 
		{
			throw new IllegalArgumentException("train ratio must be a positive value between 0.0 and 1.0");
		} 
		else if (m < 2) 
		{
			throw new IllegalArgumentException("too few elements to split");
		}

		final int train_rows = FastMath.max((int) FastMath.floor((double) m * train_ratio), 1); // want to make sure at
																								// least 1...
		final int test_rows = m - train_rows;

		Collections.shuffle(current_data); // Shuffle the list

		// build the split...
		int i = 1;
		for (PaperItem paper : new ArrayList<PaperItem>(current_data)) 
		{
			
			if (paper.getCitationNumber() >= threshold) 
			{
				this.test.add(paper);
				current_data.remove(paper);
				i = i + 1;
			}
			if (i > test_rows)
			{
				break;
			}
		}
		
		this.train =  current_data;

	}

	/**
	 * Return a copy of the training set
	 * 
	 * @return
	 */
	public ArrayList<PaperItem> getTrain() {
		return train;
	}

	/**
	 * Return a copy of the test set
	 * 
	 * @return
	 */
	public ArrayList<PaperItem> getTest() {
		return test;
	}
}
