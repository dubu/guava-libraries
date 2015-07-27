package com.google.common.escape;

import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by rigel on 7/10/15.
 */
public class DubuEscapeTest {

    private static final Logger logger = Logger.getLogger("com.google.common.base.DubuEscapeTest");



    @Test
    public void adsfjla(){

        Escapers.Builder builder = Escapers.builder();
        builder.setSafeRange('a' , 'z');
        builder.setUnsafeReplacement("ZZ");

        Escaper escaper  = builder.build();
        escaper.escape("Asdf23423asdfasdf as dfasdfas 2214324 jl234");

    }
}
