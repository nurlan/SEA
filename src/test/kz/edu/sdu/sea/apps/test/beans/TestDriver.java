package kz.edu.sdu.sea.apps.test.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TestDriver {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NamingException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NamingException, IllegalArgumentException, InvocationTargetException {
		
		Properties properties = new Properties();
		properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "kz.edu.sdu.sea.apps.client.IWebCrawlerLocal");
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		properties.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		
		Context initialContext = new InitialContext(properties);
		
		String classes[] = {"kz.edu.sdu.sea.apps.test.beans.Test1","kz.edu.sdu.sea.apps.test.beans.Test3"};
		
		
		for( String c : classes ) {
			
			Object test = Class.forName(c).newInstance();
			Field fields[] = test.getClass().getFields();
			
			Method methods[] = test.getClass().getDeclaredMethods();
			
			for(Field f : fields) {
				EJB a = f.getAnnotation(EJB.class);
				
				if( a != null )
				{
					String name = a.name();
					f.setAccessible(true);
					f.set(test, initialContext.lookup(name));
				}
			}
			
			for(Method m : methods) {
				TestThis t = m.getAnnotation(TestThis.class);
				if( t != null ) {
					System.out.println("method: "+m.getName());
					m.invoke(test, new Object[]{});
				}
			}
			
		}
		 
	}

}
