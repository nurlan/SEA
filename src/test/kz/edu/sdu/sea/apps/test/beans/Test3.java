package kz.edu.sdu.sea.apps.test.beans;

import javax.ejb.EJB;

import kz.edu.sdu.sea.apps.ejb.client.ITestBeanLocal;


public class Test3 {

	@EJB(name="sea/TestBean/remote") public ITestBeanLocal testBean;
	
	public Test3(){
		
	}
	
	@TestThis
	void doSmth3() {
		testBean.persist("link from Test3", "link text from Test3");
	}
}
