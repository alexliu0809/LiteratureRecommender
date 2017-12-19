

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class ItemRecommenderModel {
	
	private DataModel model = null;
	private ItemSimilarity similarity = null;
	private GenericBooleanPrefItemBasedRecommender recommender = null;
			
			
	public ItemRecommenderModel(String input_data_path, String similarity_type) throws Exception
	{
		this.model = new FileDataModel(new File(input_data_path));
		this.similarity = new TanimotoCoefficientSimilarity(model);
		//Tricky
		this.recommender = new GenericBooleanPrefItemBasedRecommender(model, similarity);
	}
	
	public List<RecommendedItem> recommendForItem( int program_id, int N) throws Exception
	{
		
		
		List<RecommendedItem> recommendations = this.recommender.recommend(program_id, N);
		//List<RecommendedItem> recommendations = this.recommender.mostSimilarItems(program_id, N);
		//It also has .mostSimilarItems.
		
		/*
		for (RecommendedItem recommendation : recommendations) {
		  System.out.println(recommendation);
			//recommendation.getItemID();
			
		}
		*/
		return recommendations;
	}

}
