package kz.edu.sdu.sea.apps.ejb.client;

import javax.ejb.Local;

@Local
public interface IContentFetcherLocal {
	void createPage(Long pageId, String content, String title,String description);
	void defineCharset(String link);
	void fetchContent(String link);
	void fetchContent(String link, String html);
}
