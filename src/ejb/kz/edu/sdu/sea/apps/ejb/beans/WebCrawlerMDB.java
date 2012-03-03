package kz.edu.sdu.sea.apps.ejb.beans;

import javax.ejb.ActivationConfigProperty;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

//import kz.edu.sdu.sea.apps.ejb.client.IContentFetcherLocal;
import kz.edu.sdu.sea.apps.ejb.client.IWebCrawlerLocal;
import kz.edu.sdu.sea.apps.ejb.util.Cache;

@MessageDriven(mappedName="linkQueue", activationConfig= {
		@ActivationConfigProperty(propertyName="destinationType",propertyValue="javax.jms.Queue"),
		@ActivationConfigProperty(propertyName="destination",propertyValue="queue/linkQueue"),
		@ActivationConfigProperty(propertyName="maxSession",propertyValue="50"),
		@ActivationConfigProperty(propertyName="minSession",propertyValue="5")
})
public class WebCrawlerMDB implements MessageListener {

//	@EJB
//	private IContentFetcherLocal iContentFetcher;
	
	@EJB
	private IWebCrawlerLocal iWebCrawler;
	
	Logger log = Logger.getLogger(getClass());
	
	public static int i=0;
	
	public WebCrawlerMDB() {
		log.info("MDB#:"+i);
//		System.out.println("MDB:#"+i);
		i++;
	}
	
	@Override
	public void onMessage(Message msg) {
		if( isLimit() ) {
			try {
				String link = msg.getStringProperty("link");
	//			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	//			iContentFetcher.fetchContent(link);
				iWebCrawler.crawling(link);
	//			TextMessage m = (TextMessage)msg;
	//			System.out.println(m.getText());
	//			System.out.println(msg.getStringProperty("link"));
	//			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			} catch(JMSException e) {
//				throw new RuntimeException(e);
			}
		}
	}
	
	public boolean isLimit() {
		if( Cache.size() >= 200 ) return false;
		return true;
	}

}
