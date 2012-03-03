package kz.edu.sdu.sea.apps.ejb.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import kz.edu.sdu.sea.apps.ejb.client.IContentFetcherLocal;
import kz.edu.sdu.sea.apps.ejb.client.IContentFetcherRemote;
import kz.edu.sdu.sea.apps.ejb.db.Page;
import kz.edu.sdu.sea.apps.ejb.util.Cache;

@Stateless
public class ContentFetcherBean implements IContentFetcherLocal, IContentFetcherRemote {

	@PersistenceContext(unitName="SeaPU")
	EntityManager em;
	
	Logger log = Logger.getLogger(getClass());
	
	@Override
	public void createPage(Long pageId, String content, String title,String description) {
		Page page = new Page();
		page.setPageId(pageId);
		page.setContent(content);
		page.setTitle(title);
		page.setDescription(description);
		
		em.persist(page);
	}

	@Override
	public void defineCharset(String link) {
		String html = "";
		URL url;
		URLConnection conn;
		
		try {
			url = new URL(link);
			conn = url.openConnection();
			
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.91 Safari/534.30");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String htmlLine = "";
			
			while( (htmlLine = in.readLine()) != null ) {
				html += htmlLine;
			}
			
			Document document = Jsoup.parse(html); 
			
			Element meta = document.getElementsByTag("meta").get(0);
			
			String contentType = "UTF-8";
			
			
			String charset = null;
			
			charset = meta.attr("content").toString().split("=")[1];
			
			
			if( charset != null )
				contentType = charset;
			
			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%:" + contentType);
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void fetchContent(String link) {
		String html = "";
		URL url;
		URLConnection conn;
		
		try {
			url = new URL(link);
			conn = url.openConnection();
			
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.91 Safari/534.30");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String htmlLine = "";
			
			while( (htmlLine = in.readLine()) != null ) {
				html += htmlLine;
			}
			
			Document document = Jsoup.parse(html); 
			
			Element body = document.getElementsByTag("body").get(0);
			Element title = document.getElementsByTag("title").get(0);
			
			Elements desc = document.getElementsByAttribute("description");
			
			String content = body.text();
			String titleString = title.text();
			String description = null;
			
			if( desc.size() > 0 )
				description = desc.get(0).attr("content");
			
			createPage(Cache.get(link),content,titleString,description);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void fetchContent(String link, String html) {
		Document document = Jsoup.parse(html/*,contentType*/); 

		Element body = document.getElementsByTag("body").get(0);
		Element title = document.getElementsByTag("title").get(0);
		
		Elements desc = document.getElementsByAttributeValue("name", "description");
		
		log.info("$$$$$$$$$$$$$$$$$ size: "+desc.size());
		
		String content = body.text();
		String titleString = title.text();
		String description = null;
		
		if( desc.size() > 0 ) {
			description = desc.get(0).attr("content");
			log.info(description);
		}
		createPage(Cache.get(link),content,titleString,description);
//		log.info("CONTENT:"+html);
	}
}
