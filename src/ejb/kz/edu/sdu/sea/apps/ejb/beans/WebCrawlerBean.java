package kz.edu.sdu.sea.apps.ejb.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
//import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
//import javax.persistence.Query;

import org.apache.log4j.Logger;
//import org.hibernate.search.jpa.Search;
//import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import kz.edu.sdu.sea.apps.ejb.client.IContentFetcherLocal;
import kz.edu.sdu.sea.apps.ejb.client.IWebCrawlerLocal;
import kz.edu.sdu.sea.apps.ejb.client.IWebCrawlerRemote;
import kz.edu.sdu.sea.apps.ejb.db.Link;
import kz.edu.sdu.sea.apps.ejb.util.Cache;

@Stateless
public class WebCrawlerBean implements IWebCrawlerLocal, IWebCrawlerRemote {

	@PersistenceUnit(unitName="SeaPU")
	EntityManagerFactory entityManagerFactory;
	
	@PersistenceContext(unitName="SeaPU")
	EntityManager em;
	
//	@Resource(name="java:/SeaDS")
//	DataSource ds;
	
	@Resource
	TimerService timerService; 
	
	@Resource(mappedName="/ConnectionFactory")
	ConnectionFactory connectionFactory;
	
	@Resource(mappedName="queue/linkQueue")
	Queue queue;
	
	@EJB
	private IContentFetcherLocal iContentFetcher;
	
	private static final String MY_TIMER_EVENT = "MY_TIMER_EVENT";
    private static final long  MY_TIMER_WAIT_TIME = 86400000l; //once per day

	final int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
	final int deliveryMode = DeliveryMode.NON_PERSISTENT;

	Connection jmsConnection;
	
	Logger log = Logger.getLogger(getClass());
	
	@PostConstruct
	public void init(){
		Properties jndiParameters = new Properties();
		jndiParameters.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		jndiParameters.put("java.naming.factory.url.pkgs=", "org.jboss.naming:org.jnp.interfaces");
		jndiParameters.put(javax.naming.Context.PROVIDER_URL, "jnp://localhost:1099");

		Context initialContext;

		try {
			initialContext = new InitialContext(jndiParameters);
		} catch (NamingException e) {
//			e.printStackTrace();
		}

		try {
			jmsConnection = connectionFactory.createConnection();
		} catch (JMSException e) {
//			e.printStackTrace();
		}

	}
	
	@PreDestroy
	public void destroy() {
		try {
			if( jmsConnection != null ) {
				jmsConnection.close();
			}
		} catch( JMSException e )
		{
			//log.error(e);
//			e.printStackTrace();
		}
	}
	
	@Override
	public void doSend(String link) {
		try {
			
			Session jmsSession = jmsConnection.createSession(false, acknowledgeMode);

			TextMessage jmsMsg = jmsSession.createTextMessage("My Text Message");
			jmsMsg.setJMSDeliveryMode(deliveryMode);
			jmsMsg.setStringProperty("link", link);
			
//			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%DOSEND:"+link+"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			
			MessageProducer producer = jmsSession.createProducer(queue); 
			producer.send(jmsMsg);
			producer.close();
			jmsSession.close();
			
		} catch(JMSException e) {
			//log.error(e);
//			e.printStackTrace();
//			throw new RuntimeException(e);
		} 
	}
	
	@Override
	public boolean persist(String linkText,String linkHref) {
		
		Link link = new Link();
		link.setLink(linkHref);
		link.setLinkText(linkText);
		try {
			em.persist(link);
			Cache.add(linkHref, link.getLinkId());
			doSend(linkHref);
		}
		catch( Exception e )
		{
			//log.error(e);
//			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void startCrawling(String text, String link) {
		persist(text, link);
		crawling(link);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void crawling(String baseLink) {
		/*TODO
		 * timer service
		 * pageRank
		 */
		try {
//			log.info("%%%%%%%%%%%%%%%%%%%start crawing%%%%%%%%%%%%%%%%%%%%%%%%");
//			log.info(baseLink);
//			log.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

			URL url = new URL(baseLink);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.91 Safari/534.30");
//			log.info("link:"+baseLink+"ENCODING:"+conn.getContentType());
			
			String encodings[] = conn.getContentType().split("=");
			String encoding = "UTF-8";
//			log.info("after getContentType()");
			
			
			if( encodings.length == 2 ) {
				encoding = encodings[1];
//				log.info("inside if == statemet");
			}
			InputStream is = conn.getInputStream();
			byte[] byteContent = inputStreamToByteArray(is);
			String html;
  
			//if content type is null
			if( encodings.length < 2 ) {
				html = new String(byteContent,"UTF-8");

				Document document = Jsoup.parse(html); 
//				log.info("before meta");
				Element meta = document.getElementsByTag("meta").get(0);
//				log.info("after meta");
				String contentType = "UTF-8";
				
				String charset = null;
				
				charset = meta.attr("content").toString().split("=")[1];
				
				if( charset != null )
					contentType = charset;
				
				encoding = contentType;
//				log.info("encoding:"+encoding);
//				log.info("end of if");
			}
			
			
//			byte[] byteContent = inputStreamToByteArray(is);
			
//			log.info("encoding2:"+encoding);
//			log.info("byte[]:"+byteContent.length);
			html = new String(byteContent,encoding);
			
//			log.info("#$%"+html);
			
			Map<String,String> links = new HashMap<String,String>();
			links = scanLinks(html);

			for(String href : links.keySet()) {
//				log.info(links.get(href) + ":" + corrector(baseLink,href)/*+":"+baseLink*/);

				String l = corrector(baseLink,href);
				if( !l.equals(baseLink) && !isAreadyExist(l,baseLink) /*&& isLimit()*/ && isCorrectLink(l) ) {
					persist(links.get(href),l);
					iContentFetcher.fetchContent(l, html);
//					crawing(corrector(baseLink,href));
				}
			}
		
		} catch( MalformedURLException m ) {
//			m.printStackTrace();
			//log.error(m);
		} catch( IOException i ) {
//			i.printStackTrace();
			//log.error(i);
		}
	}

	public byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[8192];
	    int bytesRead;
	    while ((bytesRead = inStream.read(buffer)) > 0) {
	        baos.write(buffer, 0, bytesRead);
	    }
	    return baos.toByteArray();
	}

	
	@Override
	public Map<String,String> scanLinks(String html) {
		Map<String,String> linksMap = new HashMap<String,String>();
		
		Document document = Jsoup.parse(html);
		Element body = document.getElementsByTag("body").get(0);
		
		Elements links = body.getElementsByTag("a");
		
		for( Element link : links ) {
			if(linksMap.containsKey(link.attr("href"))) {
				String oldText = linksMap.get(link.attr("href"));
				linksMap.remove(link.attr("href"));
				linksMap.put(link.attr("href"), (oldText == null || oldText.equals(" "))?link.text():oldText + ", " + link.text());
			}
			else
				linksMap.put(link.attr("href"), link.text());
		}
		return linksMap;
	}

	@Override
	public boolean isAreadyExist(String linkString,String base) {
		if(Cache.isAlreadyExists(linkString) ) {
			if( incrementPriority(base,linkString) ) {
				Link link = em.find(Link.class, Cache.get(linkString));
				link.setPriority(link.getPriority()+1);
				em.merge(link);
			}
			return true;
		}
		return false;
	}

	@Override
	public void indexing() {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
		int step = 1000;
		for(int i = 0; i < 2000; i += step ) {
			List<Link> links = em.createQuery("select l from Link as l").setFirstResult(i).setMaxResults(step).getResultList();
			
			for (Link link : links) {
				fullTextEntityManager.index(link);
			}
			log.info("%%%%%%%%%%%% #:" + i);
			em.flush();
		}
	}
	//bean managed transaction
	@Timeout
    public void processTimerEvent(Timer timer) {
        if (timer.getInfo() instanceof String) {
            String info = (String) timer.getInfo();
 
            if (info.equals(MY_TIMER_EVENT)) {
                log.info("Start crawling...");
                cleaningTables();
                crawling("http://www.kiwi.kz/");
            }
        }
    }
 
	@Override
    public void scheduleMyTimer(Date firstRun) {
        timerService.createTimer(firstRun, MY_TIMER_WAIT_TIME, MY_TIMER_EVENT);
    }

	@Override
    public void cancelMyTimer() {
        cancelTimers(MY_TIMER_EVENT);
    }
 
    private void cancelTimers(String timerInfo) {
        Collection<Timer> timers = timerService.getTimers();
        for (Timer timer : timers) {
            if (timer.getInfo() instanceof String && ((String) timer.getInfo()).equals(timerInfo)) {
                timer.cancel();
            }
        }
    }
	
    @Override
    public void cleaningTables() {
    	try {
	    	log.info("Cleanning tables...");
	
	    	em.createQuery("delete from Link l").executeUpdate();
	    	em.createQuery("delete from Page p").executeUpdate();
	
	    	log.info("Tables were successfully cleaned.");
    	} catch(Exception e) {
//    		e.printStackTrace();
    	}
    }
    
	private boolean incrementPriority(String base, String link) {
		if( link.startsWith(base) )
			return false;
		
		return true;
	}
	
	private String corrector(String base, String link) {
		base = getDomain(base);
		if( link.indexOf("http") == -1 ) {
			if(link.startsWith("/") && base.endsWith("/"))
				link = base.substring(0, base.length()-1) + link;
			else if(!link.startsWith("/") && base.endsWith("/"))
				link = base + link;
			else if(link.startsWith("/") && !base.endsWith("/"))
				link = base + link;
			else if(!link.startsWith("/") && !base.endsWith("/"))
				link = base + "/" + link;
		}
		
		
		
		if( !link.endsWith("/") ) {
			if( !base.endsWith("/") ) base += "/";
			if( (link + "/").equals(base) ) {
				link += "/";
			}
		}
		
		return link;
	}
	
	public String getDomain(String link) {
		link = link.substring(0, (link.indexOf('/', 8)!=-1)?link.indexOf('/', 8):link.length());
		return link;
	}
	
	public boolean isLimit() {
		if( Cache.size() >= 1000 ) return false;
		return true;
	}
	
	public boolean isCorrectLink(String link) {
		if( link.indexOf("mailto:") != -1 ||
			link.indexOf("maito:") != -1 ||
			link.indexOf("javascript:") != -1 ||
			link.endsWith("http://") )
			return false;
		return true;
	}
	
}
