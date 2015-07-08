package com.google.common.base;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.truth.Truth.assertThat;

/**
 * Created by rigel on 7/8/15.
 */
public class DubuBaseTest {

    private static final Logger logger = Logger.getLogger("com.google.common.base.DubuBaseTest");


    @Test
    public void testOption(){

        String[]  asfd = null;
        // preconditon check
        checkNotNull(asfd);    // fire exception
        assertThat(asfd[1]).isNull();
    }


    @Test
    public void asserThat(){

        // google test liblary
        List<String> asf  = Lists.newArrayList("app", "bana" , "cc");

        int numA = 5;
        assertThat(numA).isEqualTo(5);
        //assertThat(asf).is(new ArrayList<String>().add("app"));

    }

    private enum DubuEnum {
        MANDU,
        DOO
    }


    @Test
    public void numsTest(){

        if(Optional.of(DubuEnum.DOO).equals(Enums.getIfPresent(DubuEnum.class, "DOO"))){

        //if(DubuEnum.DOO.equals(Enums.getIfPresent(DubuEnum.class, "DOO"))){
            logger.info("ok");
        }else{
            logger.info("fail");
        }
    }


}
