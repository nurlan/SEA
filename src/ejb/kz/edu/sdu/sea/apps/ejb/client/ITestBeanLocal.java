package kz.edu.sdu.sea.apps.ejb.client;

import javax.ejb.Local;

@Local
public interface ITestBeanLocal {
	void persist(String linkHref, String linkText);
}
