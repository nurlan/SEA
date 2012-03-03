package kz.edu.sdu.sea.apps.ejb.service;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.Service;

import kz.edu.sdu.sea.apps.ejb.client.IWebCrawlerControlServiceLocal;
import kz.edu.sdu.sea.apps.ejb.client.IWebCrawlerControlServiceRemote;
import kz.edu.sdu.sea.apps.ejb.client.IWebCrawlerLocal;

//@Service
public class WebCrawlerControlService implements IWebCrawlerControlServiceLocal, IWebCrawlerControlServiceRemote {

	@EJB(beanInterface=IWebCrawlerLocal.class)
	private IWebCrawlerLocal iWebCrawlerLocal;
	
	Logger log = Logger.getLogger(getClass());
	
	@Override
	public void create() throws Exception {
		log.info("Initializing...");
	}

	@Override
	public void destroy() {
		log.info("Destroying...");
	}

	@Override
	public void start() throws Exception {
		log.info("Starting...");
		iWebCrawlerLocal.scheduleMyTimer(getFirstRunTime());
	}

	@Override
	public void stop() {
		log.info("Stopping...");
		iWebCrawlerLocal.cancelMyTimer();
	}
	
	private Date getFirstRunTime() {
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.HOUR_OF_DAY) >= 9) {
            cal.add(Calendar.DAY_OF_YEAR, 7);
        }
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
	}
}
