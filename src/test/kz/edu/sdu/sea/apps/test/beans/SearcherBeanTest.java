package kz.edu.sdu.sea.apps.test.beans;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import kz.edu.sdu.sea.apps.ejb.client.ISearcherRemote;

public class SearcherBeanTest {
	
	/**
	 * @param args
	 * @throws NamingException 
	 */
	public static void main(String[] args) throws NamingException {
		Properties properties = new Properties();
//		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "kz.edu.sdu.sea.apps.client.IWebCrawlerLocal");
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		properties.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		
		
		Context initialContext = new InitialContext(properties);
		
		ISearcherRemote iSearcher = (ISearcherRemote) initialContext.lookup("sea/SearcherBean/remote");
//		Map<String,String> map = iSearcher.search("услуги",1);
//		
//		for(String item : map.keySet() ){
//			System.out.println(map.get(item) + ":" + item);
//		}
		
		
		iSearcher.search("\"Бесплатная электронной\" AND Naruto?");
	}

}
