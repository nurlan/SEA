package kz.edu.sdu.sea.apps.ejb.client;

import java.util.Date;
import java.util.Map;

import javax.ejb.Local;

@Local
public interface IWebCrawlerLocal {
	void doSend(String link);
//	void sentToExpiry(String link);
	void startCrawling(String text,String link);
	void crawling(String baseLink);
	void indexing();
	void scheduleMyTimer(Date firstRun);
    void cancelMyTimer();
    void cleaningTables();
    
	Map<String,String> scanLinks(String html);

	boolean persist(String linkText,String linkHref);
	boolean isAreadyExist(String link,String base);
}
