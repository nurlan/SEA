package kz.edu.sdu.sea.apps.ejb.client.dto;

public class SearchResultDTO {
	private String title;
	private String link;
	private String linkText;
	private String description;
	private String content;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = (title.length()<=65)?title:title.substring(0, 60)+" ... ";
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getLinkText() {
		return linkText;
	}
	public void setLinkText(String linkText) {
		this.linkText = (linkText.length()<=65)?linkText:linkText.substring(0, 60) + " ... ";
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		if( description != null )
			this.description = (content.length()<=300)?content:content.substring(0, 295) + " ... ";
		else
			this.description = null;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = (content.length()<=300)?content:content.substring(0, 295) + " ... ";
	}
}
