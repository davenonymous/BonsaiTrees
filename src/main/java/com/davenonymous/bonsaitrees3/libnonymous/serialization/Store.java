package com.davenonymous.bonsaitrees3.libnonymous.serialization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Store {
	String key() default "";

	boolean storeWithItem() default false;

	boolean sendInUpdatePackage() default false;
}
