

import java.io.File;

import java.util.List;


import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Hello world!
 *
 */
public class UserRecommenderModel 
{
	private DataModel model = null;
	private UserSimilarity similarity = null;
	private UserNeighborhood neighborhood = null;
	private UserBasedRecommender recommender = null;
			
			
	public UserRecommenderModel(String input_data_path, String similarity_type) throws Exception
	{
		this.model = new FileDataModel(new File(input_data_path));
		this.similarity = new TanimotoCoefficientSimilarity(model);
		this.neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model); 
		//Tricky
		this.recommender = new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
	}
	
	public List<RecommendedItem> recommendForUser( int program_id, int N) throws Exception
	{
		
		
		List<RecommendedItem> recommendations = this.recommender.recommend(program_id, N);
		
		/*
		for (RecommendedItem recommendation : recommendations) {
		  System.out.println(recommendation);
			//recommendation.getItemID();
			
		}
		*/
		return recommendations;
	}
	
	/*
    public static void main( String[] args ) throws Exception
    {
    		UserRecommenderModel user_based = new UserRecommenderModel("data/dataset.csv", "pearson");
    		user_based.recommendForUser(2, 3);
    }
    */
}
