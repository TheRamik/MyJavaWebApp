import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Film
{
	private String id;
	private String title;
	private int year;
	private String director;
	private List<String> genres;
	
	public Film()
	{
		genres = new ArrayList<String>();
	}
	
	public Film(String newId, String newTitle, int newYear, String newDirector, List<String> newGenres) {
		this.id = newId;
		this.title = newTitle;
		this.year  = newYear;
		this.director = newDirector;
		this.genres = newGenres;
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}


	public String getDirector() {
		return director;
	}

	public void setDirector(String dir) {
		if (dir == null || dir.isEmpty()) {
			dir = "NULL";
		}
		this.director = dir;
	}
	
	public List<String> getGenres() {
		return genres;
	}
	
	public String checkGenre(String genre) {
		String tempGenre = genre; 
		switch (genre) {
			case "Act"  :
			case "Axtn" :
			case "Actn" :
			case "actn" : tempGenre = "Action"; break;
			case "Adct" :
			case "Adctx":
			case "Advt" : tempGenre = "Adventure"; break;
			case "anti-Dram" : tempGenre = "Anti-Drama"; break;
			case "BioB" :
			case "BioG" :
			case "BioP" : 
			case "BiopP":
			case "Biopx":
			case "BioPx":
			case "Biop" : tempGenre = "Biography"; break;
			case "Cart" : tempGenre = "Cartoon"; break;
			case "Comd" :
			case "Cond" :
			case "Comdx": tempGenre = "Comedy"; break;
			case "CnRb" :
			case "CnRbb":
			case "CnR"  :
			case "Crim" : tempGenre = "Crime"; break;
			case "Docu" :
			case "Ducu" : 
			case "Dicu" : tempGenre = "Documentary"; break;
			case "Docu Dram" : 
			case "Dram Docu" : tempGenre = "Drama Documentary"; break; 
			case "Dram" :
			case "Draam":
			case "ram"  :
			case "DRam" :
			case "Dramd":
			case "Dram>":
			case "DraM" : tempGenre = "Drama"; break;
			case "txx"  :
			case "Ctxxx":
			case "Ctxx" :
			case "Expm" : tempGenre = "Experimental"; break;
			case "Faml" : tempGenre = "Family"; break;
			case "Fant" :
			case "fant" :
			case "FantH*": tempGenre = "Fantasy"; break;
			case "Hist" : tempGenre = "History"; break;
			case "Hor"  :
			case "Horr" : tempGenre = "Horror"; break;
			case "Musc" :
			case "musc" :
			case "Muusc":
			case "Muscl": tempGenre = "Musical"; break;
			case "Mystp":
			case "Myst" : tempGenre = "Mystery"; break;
			case "Porb" : tempGenre = "Porn"; break;
			case "Psyc" : tempGenre = "Psycology"; break;
			case "Romtx": 
			case "Romt" : tempGenre = "Romance"; break;
			case "Romt Dram" : tempGenre = "Romantic Drama"; break;
			case "Romt Actn": tempGenre = "Romantic Action"; break;
			case "RomtAdvt" : tempGenre = "Romantic Adventure"; break;
			case "Romt Comd":
			case "Romt. Comd": tempGenre = "Romantic Comedy"; break;
			case "ScFi" :
			case "Scfi" :
			case "S.F." :
			case "SxFi" :
			case "SciF" : tempGenre = "Sci-Fi"; break;
			case "sports" : tempGenre = "Sport"; break;
			case "stage musical" : tempGenre = "Stage Musical"; break;
			case "Surl" :
			case "Surr" :
			case "surreal" : tempGenre = "Surreal"; break;
			case "Susp" :
			case "susp" : tempGenre = "Suspense"; break;
			case "Disa" : tempGenre = "Thriller"; break;
			case "West" :
			case "West1": tempGenre = "Western"; break;
		}
		return tempGenre;
	}

	public void setGenres(List<String> genres) {
			if (genres.isEmpty()) {
				genres.add("NULL");
			}
			this.genres = genres;
		}	
	
	public String genreToString() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = genres.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Movie Details - ");
		sb.append("id: " + getId());
		sb.append(", ");
		sb.append("title: " + getTitle());
		sb.append(", ");
		sb.append("year: " + getYear());
		sb.append(", ");
		sb.append("director: " + getDirector());
		sb.append(", ");
		sb.append("genre: " + genreToString());
		sb.append(".");
		return sb.toString();
	}
}