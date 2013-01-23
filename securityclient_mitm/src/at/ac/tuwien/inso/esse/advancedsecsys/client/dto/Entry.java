package at.ac.tuwien.inso.esse.advancedsecsys.client.dto;

public class Entry {
	private String name;
	private String telnr;

	public Entry(String paramString1, String paramString2) {
		this.name = paramString1;
		this.telnr = paramString2;
	}

	public String getName() {
		return this.name;
	}

	public String getTelnr() {
		return this.telnr;
	}

	public void setName(String paramString) {
		this.name = paramString;
	}

	public void setTelnr(String paramString) {
		this.telnr = paramString;
	}

	@Override
	public String toString() {
		return "Entry [name=" + name + ", telnr=" + telnr + "]";
	}

	public String asString() {
		return name + "++" + telnr;
	}

	public static Entry fromString(String entrstr) {
		String splitted[] = entrstr.split("##");
		Entry entry = new Entry(splitted[0], splitted[1]);
		return entry;
	}

	public static void main(String[] args) {
		System.out.println(fromString("name##telnr").asString());
	}
}
