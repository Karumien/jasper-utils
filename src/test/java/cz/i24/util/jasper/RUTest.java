/*
 * Copyright (c) 2014 Karumien s.r.o.
 * 
 * The contractor, Karumien s.r.o., does not take any responsibility for defects
 * arising from unauthorized changes to the source code.
 */
package cz.i24.util.jasper;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * Test of {@link RU}.
 * 
 * @author <a href="miroslav.svoboda@karumien.com">Miroslav Svoboda</a>
 * @version 1.0
 * @since 27.04.2014 10:11:12
 */
public class RUTest {

    @Test
    public void testNN() {

        Assert.assertEquals("----", RU.nn("", "----"));
        Assert.assertEquals("----", RU.nn(null, "----"));

    }

    @Test
    public void testSplit() {

        String split[] = "sub1.sn.2".split("[.]");
        System.out.println(split.length + ", " + split[0]);

    }

    @Test
    public void testNumber() {
        Assert.assertEquals("1 000,00", RU.f(new BigDecimal("1000")));
        Assert.assertEquals("1 000", RU.f(new BigDecimal("1000"), "#,##0"));
        Assert.assertEquals("-1 000,00", RU.f(new BigDecimal("-1000")));
        Assert.assertEquals("-1 000", RU.f(new BigDecimal("-1000"), "#,##0"));
        Assert.assertEquals("1 000", RU.fd(new BigDecimal("1000")));
        Assert.assertEquals("-1 000", RU.fd(new BigDecimal("-1000")));
    }


}
