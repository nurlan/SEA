package kz.edu.sdu.sea.apps.test.beans;

import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import kz.edu.sdu.sea.apps.ejb.client.IContentFetcherLocal;
import kz.edu.sdu.sea.apps.ejb.client.IContentFetcherRemote;

public class ContentFetcherBeanTest {
	
	@EJB
	public static IContentFetcherLocal localContent;
	
	public static void main(String[]args) throws NamingException {
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "kz.edu.sdu.sea.apps.client.IWebCrawlerLocal");
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		properties.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		
		
		Context initialContext = new InitialContext(properties);
//		localContent = (IContentFetcherLocal) initialContext.lookup("sea/ContentFetcherBean/local");
//		
//		localContent.fetchContent(1L);
		IContentFetcherRemote remoteContent = (IContentFetcherRemote) initialContext.lookup("sea/ContentFetcherBean/remote");
//		remoteContent.fetchContent(235L);
	}
}
