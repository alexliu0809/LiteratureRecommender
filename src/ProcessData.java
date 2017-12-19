
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProcessData {

	private String raw_data_path = "";
	private String processed_data_path = "";
	private HashMap<Integer, PaperItem> id_detail_map = new HashMap<Integer, PaperItem>(); //From int id to item
	
	private HashMap<Integer, String> programId_2_stringId_map = new HashMap<Integer, String>();; // for future use.
	private HashMap<String, Integer> stringId_2_programId_map = new HashMap<String, Integer>();; // for future use.

	public ProcessData(String raw_data_path, String processed_data_path)
	{
		this.raw_data_path = raw_data_path;
		this.processed_data_path = processed_data_path;
	}
	
	public int convertStringId2ProgramId(String id)
	{
		if (stringId_2_programId_map.containsKey(id))
		{
			return (int)stringId_2_programId_map.get(id);
		}
		else
		{
			return -1;
		}
		
	}
	
	public void readPaperDetail() throws Exception
	{
		int counter = 0;

		File[] files = new File(this.raw_data_path + "hep-th-abs/").listFiles(); //for abs in folder hep-th-abs
		for (File file : files) { 
			if (file.isDirectory()) //for folder
			{
				for (File single_item :file.listFiles()) //for each abs
				{
					//read from abs
					BufferedReader br = new BufferedReader(new FileReader(single_item.getAbsolutePath()));
					try 
					{
						int slash_count = 0;
						String title = "";
						String paper_abs = "";
						String id = single_item.getName().replaceAll(".abs", ""); // remove abs 
						int current = 0;
						String line = br.readLine();

						while (line != null) 
						{
							if (line.startsWith("Title:"))
							{
								current = 1;
								line = line.substring(6);
							}
							
							else if (line.startsWith("Authors:"))
							{
								current = 3;
							}
							
							else if (line.startsWith("\\\\"))
							{
								slash_count += 1;
								if (slash_count == 2)
								{
									current = 2;
									line = br.readLine();
									continue;
								}
								else if (slash_count == 3)
								{
									current = 3;
								}
							}
							else if (line.contains(":"))
							{
								current = 3;
							}
							
							
							
							if (current == 1)
							{
								title += line;
							}
							else if (current == 2)
							{
								paper_abs += line;
							}
							else if (current == 3)
							{
								
							}
							
							line = br.readLine();
						}
						//System.out.println(title.trim()+"\n");
						PaperItem one_paper_item = new PaperItem(counter, id.trim(), title.trim(), paper_abs.trim());
						getId_detail_map().put(counter, one_paper_item);
						programId_2_stringId_map.put(counter, id.trim());
						stringId_2_programId_map.put(id.trim(), counter);
						counter += 1;
						
					} finally 
					{
						br.close();
					}
				}
			}
		}
		
		
		this.readCitationGraph();

	}

	private void readCitationGraph() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(this.raw_data_path + "hep-th-citations"));
		
		try 
		{
			String line = br.readLine();
			while (line != null)
			{
				if (line == "" || line.startsWith("#"))
				{
					continue;
				}
				
				String from = line.split(" ")[0].trim();
				String to = line.split(" ")[1].trim();
				
				if (stringId_2_programId_map.containsKey(from) && stringId_2_programId_map.containsKey(to))
				{
					int from_program_id = stringId_2_programId_map.get(from);
					int to_program_id = stringId_2_programId_map.get(to);
					this.getId_detail_map().get(from_program_id).addCitation(to_program_id);
				}
				
				line = br.readLine();
			}
			
		}finally 
		{
			br.close();
		}
	}
	
	public void printArtificialCitationGraph() throws Exception
	{
		
	}
	
	public void printFullCitationGraph() throws Exception
	{
		
		PrintWriter writer = new PrintWriter(this.processed_data_path + "citation_data.csv", "UTF-8");
		
		try 
		{
			for (Map.Entry<Integer, PaperItem> entry : this.getId_detail_map().entrySet()) {
			    int paper_integer_id = entry.getKey();
			    PaperItem paper = entry.getValue();
			    
			    int from_program_id = paper_integer_id;
			    for (int to_program_id : paper.getCites())
			    {
			    	writer.println(from_program_id+","+to_program_id);
			    }

			}
			
		}finally 
		{
			writer.close();
		}
	}

	public String from_id_to_title( long id)
	{
		int int_id = (int) id;
		if (getId_detail_map().containsKey( int_id ) )
		{
			String title = getId_detail_map().get(int_id).getTitle();
			return title;
		}
		else
		{
			return "null";
		}
	}

	public List<String> get_all_names()
	{
		List<String> words = new ArrayList<String>();
		
		for (PaperItem item : getId_detail_map().values())
		{
			words.add(item.getTitle());
		}
		return words;
	}
	
	public Collection<PaperItem> getPaperItemCollection()
	{
		return this.id_detail_map.values();
	}

	public HashMap<Integer, PaperItem> getId_detail_map() {
		return id_detail_map;
	}




	
	/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ProcessData pd = new ProcessData("raw_data/", "data/");
		try
		{
			pd.readPaperDetail();
			pd.processCitationGraph();
		}
		catch (Exception e)
		{
			System.err.println("Catch Exception : " + e.getMessage());
		}
	}
	*/

}
