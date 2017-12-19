import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;
import info.debatty.java.stringsimilarity.*;
import info.debatty.java.stringsimilarity.ShingleBased;

//Reference: http://jaunt-api.com/jaunt-tutorial.htm


public class OnlineEval {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ProcessData pd = new ProcessData("raw_data/", "data/");
		try 
		{
			pd.readPaperDetail();
		} 
		catch (Exception e) 
		{
			System.err.println("Catch Exception : " + e.getMessage());
			return;
		}

		Collection<PaperItem> paper_collection = pd.getPaperItemCollection();
		ArrayList<PaperItem> papers = new ArrayList<PaperItem>(
				Arrays.asList(paper_collection.toArray(new PaperItem[paper_collection.size()])));
		
		/*
		Jaccard jarcard_sim = new Jaccard(2);
		System.out.println(jarcard_sim.distance("abc def", "abc def "));
		System.out.println(jarcard_sim.distance("Abc Def", "abc Def "));
		System.out.println(jarcard_sim.distance("D", "abc def "));
		
		NormalizedLevenshtein l = new NormalizedLevenshtein();

        System.out.println(l.distance("abc def", "abc def  "));
        System.out.println(l.distance("Abc Def", "abc Def "));
        System.out.println(l.distance("D", "abc def "));
        
        System.out.println(l.distance("abc def".replaceAll("\\s{2,}", " ").trim(), "abc def  "));
        System.out.println(l.distance("Abc Def".replaceAll("\\s{2,}", " ").trim(), "abc Def ".replaceAll("\\s{2,}", " ").trim()));
        System.out.println(l.distance("D", "abc def "));
        
        * the less the better
        */
        
		NormalizedLevenshtein l = new NormalizedLevenshtein();
		try 
		{
			FileWriter fw = new FileWriter("meta_result/related_papers.txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw);
			for (PaperItem paper : papers) 
			{
				System.out.println("Searched Paper Title:"+paper.getTitle());
				out.println("Searched Paper Title:"+paper.getTitle());
				
				try 
				{
					
					// String q = "random word £500 bank $";
					String url = "https://scholar.google.com/scholar?&q=" + URLEncoder.encode(paper.getTitle(), "UTF-8");
					
					// Call useragent to visit
					UserAgent userAgent = new UserAgent(); // create new userAgent (headless browser).
					userAgent.visit(url); // visit a url
					
					// Get Google Scholar paper name
					Element returned_name = userAgent.doc.findFirst("<div class=gs_ri>").findFirst("h3 class=gs_rt").findFirst("<a href>");
					
					String returned_name_str = returned_name.getText().toLowerCase().replaceAll("\\s{2,}", " ").trim();
					String paper_title_str = paper.getTitle().toLowerCase().replaceAll("\\s{2,}", " ").trim();
					// Check it is the same paper
					//System.out.println(l.distance(returned_name_str, paper_title_str));
					if (l.distance(returned_name_str, paper_title_str) <= 0.3)
					{
						
						out.println("Return Search Paper Title:" + returned_name.getText());
						
						//Get related article url
						Element related_url = userAgent.doc.findFirst("<div class=gs_ri>").findFirst("<div class=gs_fl>")
								.findFirst("<a>Related articles");
						//System.out.println("returned url: " + related_url.getAt("href")); // print the anchor element
						
						//Visit URL
						UserAgent relateAgent = new UserAgent(); // create new userAgent (headless browser).
						Elements all_related_divs = relateAgent.visit(related_url.getAt("href")).findEach("<div class=gs_ri>"); // visit a url
						
						out.println("");
						out.println("Related:");
						for (Element article_div : all_related_divs)
						 {
							Element title = article_div.findFirst("<h3 class=gs_rt>").findFirst("<a>");
							Element year = article_div.findFirst("<div class=gs_a>");
									
							Pattern my_pattern = Pattern.compile(" [0-9][0-9][0-9][0-9] -");
							Matcher m_year = my_pattern.matcher(year.getText());
							if (m_year.find()) {
						         //System.out.println("Found value: " + m_year.group(0) );
						         
						         Pattern digit = Pattern.compile("[0-9][0-9][0-9][0-9]");
						         
								 Matcher m_digit = digit.matcher(m_year.group(0));
								 if (m_digit.find())
								 {
									 //System.out.println("Found value: " + m_digit.group(0) );
									 if (Integer.parseInt(m_digit.group(0)) >= 1992 && Integer.parseInt(m_digit.group(0)) <= 2004) 
									 {
										 out.println("Paper: " + title.getText());
										 out.println("Year:" + m_digit.group(0));
										 out.println();
									 }
								 }
						    }
						 }
					}
					
					System.out.println("Finished");
					
				} 
				catch (Exception e) 
				{ // if an HTTP/connection error occurs, handle JauntException.
					System.err.println(e);
				}
				finally {
					out.println("");
					out.flush();
					Thread.sleep( (int)(Math.random()*10000) + 60000);
				}
				/*
				try {
				UserAgent userAgent = new UserAgent();
				  userAgent.visit("http://jaunt-api.com/examples/food.htm");
				    
				  Elements elements = userAgent.doc.findEvery("<div>");             //find all divs in the document
				  System.out.println("Every div: " + elements.size() + " results"); //report number of search results.
				    
				  elements = userAgent.doc.findEach("<div>");                       //find all non-nested divs
				  System.out.println("Each div: " + elements.size() + " results");  //report number of search results.
				                                                                    //find non-nested divs within <p class='meat'>
				  for (Element e : elements)
				  {
					  System.out.println(e.getText());
				  }
				  
				  elements = userAgent.doc.findFirst("<p class=meat>").findEach("<div>");
				  System.out.println("Meat search: " + elements.size() + " results");//report number of search results.
				
				}
				catch (Exception e)
				{
					
				}
				*/
			}
			out.close();
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
		}
		
		
		

	}

}
