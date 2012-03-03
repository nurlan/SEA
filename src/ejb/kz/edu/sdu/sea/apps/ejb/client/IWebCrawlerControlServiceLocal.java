package kz.edu.sdu.sea.apps.ejb.client;

import javax.ejb.Local;

@Local
public interface IWebCrawlerControlServiceLocal {
	public void create() throws Exception;
	public void destroy();
	public void start() throws Exception;
	public void stop();
}
