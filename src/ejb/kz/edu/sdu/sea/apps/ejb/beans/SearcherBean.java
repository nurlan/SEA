package kz.edu.sdu.sea.apps.ejb.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

import kz.edu.sdu.sea.apps.ejb.client.ISearcherLocal;
import kz.edu.sdu.sea.apps.ejb.client.ISearcherRemote;
import kz.edu.sdu.sea.apps.ejb.client.dto.SearchResultDTO;
import kz.edu.sdu.sea.apps.ejb.db.Link;
import kz.edu.sdu.sea.apps.ejb.db.Page;

@Stateless
public class SearcherBean implements ISearcherLocal, ISearcherRemote {
	
	@PersistenceContext(unitName="SeaPU")
	EntityManager em;
	
	Logger log = Logger.getLogger(getClass());

	@Override
	public List<SearchResultDTO> search(String keyword) {
		keyword = escapeCharacters(keyword);
		log.info("escaped keyword: "+keyword);
		
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

		String[] fields = new String[]{"linkText", "page.title", "page.description","page.content"};
		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
		Query query;
		
		List<Link> result = new ArrayList<Link>();
		
		try {
			query = parser.parse( keyword );
			javax.persistence.Query hibQuery = fullTextEntityManager.createFullTextQuery(query, Link.class);

			result = hibQuery.getResultList();
			
			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			
			for(Link item : result)
				log.info(item.getLink()+ " : $" + item.getLinkText() + "$");
			
			log.info("size: "+result.size());
			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<SearchResultDTO> searchResult = new ArrayList<SearchResultDTO>();
		try {
			searchResult = getSearchResultDTOs(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return searchResult;
	}
	
	private String escapeCharacters(String keyword) {
		String [] specialChars = {"!","?"};
		
		for(int i = 0; i < specialChars.length; i++ ) 
			if(keyword.indexOf(specialChars[i]) > -1)
				keyword = keyword.replace(specialChars[i], "\\"+specialChars[i]);

		return keyword;
	}
	
	private List<SearchResultDTO> getSearchResultDTOs(List<Link> links) throws Exception {
		List<SearchResultDTO> result = new ArrayList<SearchResultDTO>();
		Page page;
		for(int i = 0; i < links.size(); i++ ) {
			page = links.get(i).getPage();
			SearchResultDTO dto = new SearchResultDTO();
			
			dto.setTitle(page.getTitle());
			dto.setLink(links.get(i).getLink());
			dto.setLinkText(links.get(i).getLinkText());
			dto.setContent(page.getContent());
			
			if( page.getDescription() != null )
				dto.setDescription(page.getDescription());
			
			result.add(dto);
		}
		
		return result;
	}
	
	/*
	 * hibernate search
	 * @see kz.edu.sdu.sea.apps.ejb.client.ISearcherLocal#search(java.lang.String, int)
	 */
	@Override
	public Map<String, String> search(String keyword, int page) {
		int pageSize = 10;
		
		List<Link> links = em.createQuery("select l from Link l where l.linkText like :keyword order by l.priority desc")
			.setParameter("keyword", "%"+ keyword +"%").getResultList();
		
		log.info("Search Result: "+links.size());
		
		
		Map<String, String> map = new HashMap<String, String>();
		map = linksToMap(links);
		
//		if( links.size() < pageSize ) {
			int rest = pageSize - links.size();
			List<Page> pages = em.createQuery("select p from Page p where p.title like :keyword or p.content like :keyword")
			.setParameter("keyword", "%"+ keyword +"%").getResultList();
	//		map = pagesToMap(pages);
			map.putAll(pagesToMap(pages));
//		}
		
		return map;
	}
	
	private Map<String, String> linksToMap(List<Link> links) {
		Map<String, String> map = new HashMap<String, String>();
		
		for(Link link : links) {
			map.put(link.getLink(), link.getLinkText());
		}
		
		return map;
	}
	
	private Map<String, String> pagesToMap(List<Page> pages) {
		Map<String, String> map = new HashMap<String, String>();
		
		for(Page page : pages) {
			map.put(page.getLink().getLink(), page.getContent().substring(0,50));
		}
		
		return map;
	}
	
}
