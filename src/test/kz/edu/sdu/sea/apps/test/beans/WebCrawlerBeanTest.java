package kz.edu.sdu.sea.apps.test.beans;

import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import kz.edu.sdu.sea.apps.ejb.client.IWebCrawlerLocal;
import kz.edu.sdu.sea.apps.ejb.client.IWebCrawlerRemote;

/**
 * @author Nurlan Rakhimzhanov
 *
 */
public class WebCrawlerBeanTest {

	/**
	 * @param args
	 * @throws NamingException 
	 */
	
	@EJB static IWebCrawlerLocal local;
	
	public static void main(String[] args) throws NamingException {
		
//			local.crawing("http://www.size.kz");
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "kz.edu.sdu.sea.apps.client.IWebCrawlerLocal");
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		properties.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		
		
		Context initialContext = new InitialContext(properties);
		
//		initialContext.lookup("IWebCrawlerLocal");
		
		IWebCrawlerRemote localWebCrawler = (IWebCrawlerRemote)initialContext.lookup("sea/WebCrawlerBean/remote");
//		IHelloWorldRemote hello=(IHelloWorldRemote) initialContext.lookup("helloworld/HelloWorldBean/remote");
//		localWebCrawler.startCrawing("OK.KZ", "http://www.ok.kz/");
		localWebCrawler.crawling("http://en.wikipedia.org/wiki/Java");
//		localWebCrawler.doSend(236L);
//		localWebCrawler.indexing();
//		localWebCrawler.createTimer( 1000L * 60 * 60 * 24 * 3 );
//		localWebCrawler.cleaningTables();
	}

}
