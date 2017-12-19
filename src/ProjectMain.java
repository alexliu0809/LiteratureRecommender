import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
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

import com.jaunt.Element;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

//Reference: http://jaunt-api.com/jaunt-tutorial.htm

public class ProjectMain {
	
	//Do a cross-validation on model performance by removing one
	//Possibly Use Google As Baseline -- Hard (Possibly try baidu / others, it works now. -- see test1.py)
	//Add simple GUI for recommending
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		ProcessData pd = new ProcessData("raw_data/", "data/");
		try
		{
			pd.readPaperDetail();
			pd.printFullCitationGraph();
			//pd.readCitationGraph();
		}
		catch (Exception e)
		{
			System.err.println("Catch Exception : " + e.getMessage());
		}
		
		
		UserRecommenderModel user_based;
		try {
			
			user_based = new UserRecommenderModel("data/citation_data.csv", "pearson");
			
			int program_id = pd.convertStringId2ProgramId("0001001");
			
			System.out.println("Recommending For:" + pd.from_id_to_title(program_id));
			
			List<RecommendedItem> recommendations = user_based.recommendForUser(program_id, 5);
			
			System.out.println("\nUser-Based Recommend Results:");
			for (RecommendedItem item:recommendations)
			{
				System.out.println(pd.from_id_to_title(item.getItemID()));
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error: " + e.getMessage());
		}
		
		ItemRecommenderModel item_based;
		try {
			
			item_based = new ItemRecommenderModel("data/citation_data.csv", "pearson");
			
			int program_id = pd.convertStringId2ProgramId("0001001");
			
			System.out.println("\nRecommending For " + pd.from_id_to_title(program_id));
			
			List<RecommendedItem> recommendations = item_based.recommendForItem(program_id, 5);
			
			System.out.println("\nItem-Based Recommend Results:");
			for (RecommendedItem item:recommendations)
			{
				System.out.println(pd.from_id_to_title(item.getItemID()));
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error: " + e.getMessage());
		}
		
		//List<String> words = pd.get_all_names();
		//Demo d = new Demo(words);
		
		
		 

	}
	
	
	/*
	public static void main(String[] args) throws Exception {
		try{
	         //Creating data model
	         DataModel datamodel = new FileDataModel(new File("data/dataset.csv")); //data
	      
	         //Creating UserSimilarity object.
	         UserSimilarity usersimilarity = new TanimotoCoefficientSimilarity(datamodel);
	      
	         //Creating UserNeighbourHHood object.
	         UserNeighborhood userneighborhood = new NearestNUserNeighborhood(5, usersimilarity, datamodel);
	      
	         //Create UserRecomender
	         //https://www.slideshare.net/Cataldo/tutoria-mahout-recommendation
	         UserBasedRecommender recommender = new GenericBooleanPrefUserBasedRecommender(datamodel, userneighborhood, usersimilarity);
	        
	         List<RecommendedItem> recommendations = recommender.recommend(2, 3);
				
	         for (RecommendedItem recommendation : recommendations) {
	            System.out.println(recommendation);
	         }
	      
	      }catch(Exception e){
	    	  	System.err.println(e.getMessage());
	      }
	}
	*/

}
