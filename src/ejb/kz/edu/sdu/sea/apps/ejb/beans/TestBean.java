package kz.edu.sdu.sea.apps.ejb.beans;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import kz.edu.sdu.sea.apps.ejb.client.ITestBeanLocal;
import kz.edu.sdu.sea.apps.ejb.client.ITestBeanRemote;
import kz.edu.sdu.sea.apps.ejb.db.Link;

@Stateless
public class TestBean implements ITestBeanLocal, ITestBeanRemote{

	@PersistenceContext(unitName="SeaPU")
	EntityManager em;
	
	@Override
	public void persist(String linkHref, String linkText) {
		Link link = new Link();
		link.setLink(linkHref);
		link.setLinkText(linkText);
		
		em.persist(link);
	}

}
