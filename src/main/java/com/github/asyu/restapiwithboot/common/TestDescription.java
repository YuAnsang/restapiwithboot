package com.github.asyu.restapiwithboot.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE) 
public @interface TestDescription {

    String value();
    
}
