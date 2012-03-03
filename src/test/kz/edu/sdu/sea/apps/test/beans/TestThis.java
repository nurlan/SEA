package kz.edu.sdu.sea.apps.test.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface TestThis {
	
}
