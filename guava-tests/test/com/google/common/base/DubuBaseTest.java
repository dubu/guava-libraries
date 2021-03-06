package com.google.common.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void asserThatasfasf(){

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

    @Test
    public void afjlasjd(){

        //ImmutableList<String> a = Lists.newArrayList("aa","bb");

        List<String> b = Lists.newArrayList("aa","bb");


        List<String> asfd = ImmutableList.of("aa" , "bb");


        assertThat(b).isEqualTo(asfd);
    }


    @Test
    public void arrayCopyTset(){

        String[] core =  new String[]{"dd", "d" ,"ee"};

        List<String> aa = Arrays.asList("a", "b", "c", "e");

        Lists.asList("A" , "b", core);


        ///

        List<String> k  = Arrays.asList("aaa", "bbbb" ,"ccc" , "dddd");
        List<String> kk  = Arrays.asList("000", "111" ,"222" , "3333");
        ImmutableList<String> kkk  = ImmutableList.of("kk", "ggg");


        ImmutableList<String> zzz =  new ImmutableList.Builder<String>()
                .add("..aa")
                .add("..bb")
                .addAll(k.iterator())
                .addAll(kk)
                .addAll(kkk)
                .build();

        logger.info("ASdfasfd");

    }


    @Test
    public void afasdf(){

        Iterator<String>  iterator =  Iterators.singletonIterator("A");

        while (iterator.hasNext()) {
            Object next =  iterator.next();
            logger.info(String.valueOf(next));

        }

        ImmutableList<Integer> asdfaasf  = ImmutableList.of(1, 2, 124312, 534534);

          for(Integer i : asdfaasf){

              logger.info(String.valueOf(i));

        }
    }


    @Test
    public void convertStringListToIntList(){


        List<String> strList = ImmutableList.of("123" , "435");

        Converter<String , Integer> strToIntger  = new Converter<String, Integer>() {
            @Override
            protected Integer doForward(String s) {
                return Integer.valueOf(s);
            }

            @Override
            protected String doBackward(Integer integer) {
                return String.valueOf(integer);
            }
        };

        Iterable<Integer> intList =  strToIntger.convertAll(strList);

        for (Integer i  : intList){
            i.toString();
        }

    }


    @Test
    public void joinnerTest(){


        List<String> aa = ImmutableList.of("aaaa" , "bbbb" , "11");
        logger.info(Joiner.on(",").join(aa));

    }

    @Test
    public void testRegx(){

        Pattern p = Pattern.compile("(http|https)://.*.net/article/([0-9]+)$");
        Matcher m = p.matcher("http://dubu.net/article/72");

        if(m.matches()){
            System.out.println(m.group());
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }
    }

    @Test
    public void subStringByte(){

        String simple = " 郭郭郭郭한글 포함한 한 글  adsfadf  asd f af  1 1 1  1" +
                "121212 1 2" +
                "   hello";
        Iterable<String> letters = Splitter.fixedLength(2).split(simple);
        final Iterator<String> iterator = letters.iterator();
        while (iterator.hasNext()) {
            String next =  iterator.next();
            
        }


        for (String l : letters){
            System.out.println(l);
        }




    }


    @Test
    public void testSub(){

        System.out.println("unhappy".substring(0,2) );
        System.out.println("한글".substring(0,2));
    }


}
