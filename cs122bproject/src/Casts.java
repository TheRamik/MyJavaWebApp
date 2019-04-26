import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Casts
{
	private String id;
	private String title;
	private String director;
	private List<String> stars;
	
	public Casts()
	{
		stars = new ArrayList<String>();
	}
	
	public Casts(String newId, String newTitle, int newYear, String newDirector, List<String> newStars) {
		this.id = newId;
		this.title = newTitle;
		this.director = newDirector;
		this.stars = newStars;
		
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

	public String getDirector() {
		return director;
	}

	public void setDirector(String dir) {
		if (dir == null || dir.isEmpty()) {
			dir = "NULL";
		}
		this.director = dir;
	}
	
	public List<String> getStars() {
		return stars;
	}

	public void setGenres(List<String> stars) {
			if (stars.isEmpty()) {
				stars.add("NULL");
			}
			this.stars = stars;
		}	
	
	public String starsToString() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = stars.iterator();
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
		sb.append("director: " + getDirector());
		sb.append(", ");
		sb.append("stars: " + starsToString());
		sb.append(".");
		return sb.toString();
	}
}