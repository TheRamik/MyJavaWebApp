public class Star
{
	private String id;
	private String name;
	private int birthYear;
	
	public Star()
	{
	}
	
	public Star(String newId, String newName, int newBirthYear) {
		this.id = newId;
		this.name = newName;
		this.birthYear  = newBirthYear;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int year) {
		this.birthYear = year;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("STAR Details - ");
		sb.append("id: " + getId());
		sb.append(", ");
		sb.append("name: " + getName());
		sb.append(", ");
		sb.append("birthyear: " + getBirthYear());
		sb.append(".");
		return sb.toString();
	}
}