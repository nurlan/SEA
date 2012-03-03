package kz.edu.sdu.sea.apps.test.beans;

import javax.ejb.EJB;

import kz.edu.sdu.sea.apps.ejb.client.ITestBeanLocal;


public class Test1 {

	@EJB(name="sea/TestBean/remote") public ITestBeanLocal testBean;
	
	public Test1(){
		
	}
	
	@TestThis
	void doSmth1() {
		testBean.persist("link from Test1", "link text from Test1");
	}

	@TestThis
	void doSmth12() {
		testBean.persist("link2 from Test1", "link2 text from Test1");
	}
}
