

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

/*
 * 
   8
   40
    CV: 1 Item Cover Rate: 0.6028416779431665
	CV: 1 Item Average Rank: 11.214365881032547
	CV: 1 User Cover Rate: 0.5669824086603519
	CV: 1 User Average Rank: 11.890811455847256
	
   20
	CV: 1 Item Cover Rate: 0.4820703653585927
	CV: 1 Item Average Rank: 6.811929824561403
	CV: 1 User Cover Rate: 0.43031123139377536
	CV: 1 User Average Rank: 7.151729559748428
   10
    CV: 1 Item Cover Rate: 0.35791610284167796
	CV: 1 Item Average Rank: 4.050094517958412
	CV: 1 User Cover Rate: 0.3135994587280108
    CV: 1 User Average Rank: 4.308522114347357
    
   1
   CV: 1 Item Cover Rate: 0.08457374830852503
   CV: 1 Item Average Rank: 1.0
   CV: 1 User Cover Rate: 0.07070365358592692
   CV: 1 User Average Rank: 1.0
 * 
 */
public class OfflineEval {

	private static final int cv = 2;
	private static final double train_ratio = 0.9;
	private static final int threshold = 8;
	private static final int top_N = 1;
	private static final int[] top = new int[] { 1, 10, 20, 40 };

	public static void writeCitationGraph(String output_path, ArrayList<PaperItem> train, ArrayList<PaperItem> test)
			throws Exception {
		PrintWriter writer = new PrintWriter(output_path, "UTF-8");

		try {
			for (PaperItem paper : train) {
				int paper_integer_id = paper.getProgramId();

				int from_program_id = paper_integer_id;
				for (int to_program_id : paper.getCites()) {
					writer.println(from_program_id + "," + to_program_id);
				}

			}

			for (PaperItem paper : test) {
				int paper_integer_id = paper.getProgramId();

				int from_program_id = paper_integer_id;
				for (int to_program_id : paper.getCites()) {
					writer.println(from_program_id + "," + to_program_id);
				}

			}

		} finally {
			writer.close();
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ProcessData pd = new ProcessData("raw_data/", "data/");
		try {
			pd.readPaperDetail();
		} catch (Exception e) {
			System.err.println("Catch Exception : " + e.getMessage());
			return;
		}

		Collection<PaperItem> paper_collection = pd.getPaperItemCollection();
		ArrayList<PaperItem> papers = new ArrayList<PaperItem>(
				Arrays.asList(paper_collection.toArray(new PaperItem[paper_collection.size()])));

		int[][] item_rank_per_cv = new int[cv + 1][(int) ((train_ratio * papers.size() + 10))];

		int[][] user_rank_per_cv = new int[cv + 1][(int) ((train_ratio * papers.size() + 10))];

		for (int current_cv = 1; current_cv <= cv; current_cv++) {
			//ProgressBar bar = new ProgressBar();
			
			// Output Directory
			String citation_graph_path = "data/eval_cv_" + current_cv + "_citation_data.csv";

			// Train Teest Split
			System.out.println("Creating Train/Test Sets For CV: " + current_cv);
			TrainTestSplit tts = new TrainTestSplit(papers, 0.9, threshold);
			ArrayList<PaperItem> train_papers = tts.getTrain();
			ArrayList<PaperItem> test_papers = tts.getTest();
			// System.out.println(papers.get(0).toString());
			// System.out.println(train_papers.get(0).toString());
			// System.out.println(test_papers.get(0).toString());

			// Remove one citation from test papers and create artificial test cases
			for (PaperItem paper : test_papers) {
				paper.removeOneCitation();
			}

			// Write Citation Graph For Recommend Engine
			System.out.println("Writing Citation Graph For CV: " + current_cv);
			writeCitationGraph(citation_graph_path, train_papers, test_papers);

			// Build User Model
			UserRecommenderModel user_based;
			ItemRecommenderModel item_based;
			try {
				System.out.println("Building Recommender Enginer for CV: " + current_cv);
				user_based = new UserRecommenderModel(citation_graph_path, "pearson");
				item_based = new ItemRecommenderModel(citation_graph_path, "pearson");

				System.out.println("Evaluating Test Sets For CV: " + current_cv);
				
				int test_paper_size = test_papers.size();
				//bar.update(0, test_paper_size);
				for (int idx = 1; idx < test_paper_size; idx++) {
					if (idx % (int)(test_paper_size/100) == 0)
					{
						System.out.printf("%.1f%%\n",idx * 1.0/ test_paper_size * 100 );
					}
					
					PaperItem paper = test_papers.get(idx - 1);

					final int test_paper_program_id = paper.getProgramId();
					final int hidden_cite_program_id = paper.getHidden_cite();

					List<RecommendedItem> item_results = item_based.recommendForItem(test_paper_program_id, top_N);
					List<RecommendedItem> user_results = user_based.recommendForUser(test_paper_program_id, top_N);

					// Check item hits
					for (int j = 1; j <= item_results.size(); j++) {
						if (item_results.get(j - 1).getItemID() == hidden_cite_program_id) {
							// System.out.println("Item Hit At Rank: " + j);
							item_rank_per_cv[current_cv][idx] = j;
							break;
						}
					}

					// Check user hits
					for (int j = 1; j <= user_results.size(); j++) {
						if (user_results.get(j - 1).getItemID() == hidden_cite_program_id) {
							// System.out.println("User Hit At Rank: " + j);
							user_rank_per_cv[current_cv][idx] = j;
							break;
						}
					}

				}

				int item_cover_count = 0;
				int item_cover_sum = 0;
				for (int val : item_rank_per_cv[current_cv]) {
					if (val > 0) {
						item_cover_count++;
						item_cover_sum += val;
					}
				}
				System.out.println("CV: " + current_cv + " Item Cover Rate: "
						+ (double) (item_cover_count * 1.0 / test_papers.size()));
				
				System.out.println("CV: " + current_cv + " Item Average Rank: "
						+ (double) (item_cover_sum * 1.0 / item_cover_count));

				int user_cover_count = 0;
				int user_cover_sum = 0;
				for (int val : user_rank_per_cv[current_cv]) {
					if (val > 0) {
						user_cover_count++;
						user_cover_sum += val;
					}
				}
				System.out.println("CV: " + current_cv + " User Cover Rate: "
						+ (double) (user_cover_count * 1.0 / test_papers.size()));
				
				System.out.println("CV: " + current_cv + " User Average Rank: "
						+ (double) (user_cover_sum * 1.0 / user_cover_count));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Error: " + e.getMessage());
			}

		}

	}

}
