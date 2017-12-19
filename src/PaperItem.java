

import java.util.ArrayList;

public class PaperItem {
	
	private int program_id;
	private String id = "";
	private String title = "";
	private String paper_abstract = "";
	private ArrayList<Integer> cites = null;
	private int hidden_cite = -1; 
	
	public void removeOneCitation() {
		this.hidden_cite = this.cites.remove(this.cites.size() - 1);
	}
	
	public int getHidden_cite() {
		return this.hidden_cite;
	}
	
	public String toString() { 
	    return "Int ID: " + program_id + " Title: " + this.title;
	} 
	
	public PaperItem(int program_id, String id, String title, String paper_abstract)
	{
		this.setProgramId(program_id);
		this.setId(id);
		this.setTitle(title);
		this.setPaper_abstract(paper_abstract);
		this.cites = new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> getCites(){
		return this.cites;
	}
	
	public void addCitation(int cited_paper_id) {
		this.cites.add(cited_paper_id);
	}

	public int getProgramId()
	{
		return program_id;
	}
	
	public void setProgramId(int program_id)
	{
		this.program_id = program_id;
	}

	private String getId() {
		return id;
	}


	private void setId(String id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	private void setTitle(String title) {
		this.title = title;
	}


	private String getPaper_abstract() {
		return paper_abstract;
	}


	private void setPaper_abstract(String paper_abstract) {
		this.paper_abstract = paper_abstract;
	}
	
	public int getCitationNumber()
	{
		return this.cites.size();
	}
}
