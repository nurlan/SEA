package kz.edu.sdu.sea.apps.test.beans;

import javax.ejb.EJB;

import kz.edu.sdu.sea.apps.ejb.client.ITestBeanLocal;


public class Test2 {

	@EJB(name="sea/TestBean/remote") public ITestBeanLocal testBean;
	
	public Test2(){
		
	}
	
	@TestThis
	void doSmth2() {
		testBean.persist("link from Test2", "link text from Test2");		
	}
}
