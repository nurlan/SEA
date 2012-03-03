package kz.edu.sdu.sea.apps.ejb.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;


@Entity
@Indexed
@Table(name="pages")
public class Page {
	private Long pageId;
	private String content;
	private String title;
	private String description;
	private Link link;
	
	@Id
	@Column(name="page_id")
	public Long getPageId() {
		return pageId;
	}
	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}
	
	@Column(columnDefinition="text")
	@Field(index=Index.TOKENIZED, store=Store.NO)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Field(index=Index.TOKENIZED, store=Store.NO)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(columnDefinition="text")
	@Field(index=Index.TOKENIZED, store=Store.NO)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@OneToOne(mappedBy="page")
	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}
	
}
