/*
 * Copyright (c) 2014 Karumien s.r.o.
 * 
 * The contractor, Karumien s.r.o., does not take any responsibility for defects
 * arising from unauthorized changes to the source code.
 */
package cz.i24.util.jasper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


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
        Assert.assertEquals("-1 000 000", RU.fd(new BigDecimal("-1000000")));
    }

    @Test
    public void testJson() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode data = mapper.readTree("{\"value\": 1000.45 }");
        Object val = RU.json(data, "value");
        Assert.assertNotNull(val);

        data = mapper.readTree("{\"value\": null }");
        val = RU.json(data, "value");
        Assert.assertNull(val);

        data = mapper.readTree("{\"value\": null }");
        val = RU.json(data, "value", "xxx");
        Assert.assertNotNull(val);
    }


    @Test
    public void testDate() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree("{\"value\": \"14.02.1978\" }");

        Object val = RU.json(data, "value");

        Assert.assertNotNull(val);
        Date date = RU.date(val);
        Assert.assertNotNull(date);

    }

    @Test
    public void testLogical() {
        Assert.assertEquals(true, RU.isTrue("true"));
        Assert.assertEquals(true, RU.isFalse("false"));

        Assert.assertEquals(false, RU.isFalse("true"));
        Assert.assertEquals(false, RU.isTrue("false"));

        Assert.assertEquals(true, RU.isTrue(Boolean.TRUE));
        Assert.assertEquals(true, RU.isFalse(Boolean.FALSE));

        Assert.assertEquals(false, RU.isFalse(Boolean.TRUE));
        Assert.assertEquals(false, RU.isTrue(Boolean.FALSE));

        Assert.assertEquals(false, RU.isTrue(null));
        Assert.assertEquals(false, RU.isTrue(null, false));
        Assert.assertEquals(true, RU.isTrue(null, true));
        Assert.assertEquals(true, RU.isFalse(null, true));
        Assert.assertEquals(false, RU.isFalse(null, false));

        Assert.assertEquals(true, RU.and(RU.isTrue("true"), RU.isTrue(Boolean.TRUE)));
        Assert.assertEquals(false, RU.and(RU.isTrue("true"), RU.isTrue(null)));
        Assert.assertEquals(false, RU.and(null, RU.isTrue(null)));

        Assert.assertEquals(true, RU.or(RU.isTrue("true"), RU.isTrue(Boolean.TRUE)));
        Assert.assertEquals(true, RU.or(RU.isTrue("true"), RU.isTrue(null)));
        Assert.assertEquals(false, RU.or(null, RU.isTrue(null)));

        Assert.assertEquals(false, RU.not(RU.isTrue("true")));
        Assert.assertEquals(true, RU.not(RU.isTrue("false")));
        Assert.assertEquals(true, RU.not(null));

        Assert.assertEquals("Ano", RU.w(true, "Ano"));
        Assert.assertEquals("", RU.w(false, "Ano"));
        Assert.assertEquals("", RU.w(null, "Ano"));

        Assert.assertEquals("", RU.w(false, "Ano", null));

        Assert.assertEquals("Ano", RU.w(true, "Ano", "Ne"));
        Assert.assertEquals("Ne", RU.w(false, "Ano", "Ne"));
        Assert.assertEquals("", RU.w(null, "Ano", "Ne"));

        Assert.assertEquals("Ano", RU.w(true, "Ano", "Ne", "----"));
        Assert.assertEquals("Ne", RU.w(false, "Ano", "Ne", "----"));
        Assert.assertEquals("----", RU.w(null, "Ano", "Ne", "----"));

        Assert.assertEquals("Ano", RU.wn(false, "Ano"));
        Assert.assertEquals("", RU.wn(true, "Ano"));
        Assert.assertEquals("", RU.wn(null, "Ano"));

        Assert.assertEquals("", RU.wn(true, "Ano", null));

        Assert.assertEquals("Ano", RU.wn(false, "Ano", "Ne"));
        Assert.assertEquals("Ne", RU.wn(true, "Ano", "Ne"));
        Assert.assertEquals("", RU.wn(null, "Ano", "Ne"));

        Assert.assertEquals("Ano", RU.wn(false, "Ano", "Ne", "----"));
        Assert.assertEquals("Ne", RU.wn(true, "Ano", "Ne", "----"));
        Assert.assertEquals("----", RU.wn(null, "Ano", "Ne", "----"));


        Assert.assertEquals(true, RU.and(RU.isTrue("true"), RU.isTrue(Boolean.TRUE), true));
        Assert.assertEquals(false, RU.and(RU.isTrue("true"), RU.isTrue(null), false));
    }

    @Test
    public void testIn() {

        Assert.assertEquals(RU.in(Arrays.asList("CSO4", "CS", "XA", "XB"), Arrays.asList("X", "XS")).size(), 0);
        Assert.assertEquals(RU.in(Arrays.asList("CSO4", "CS", "XA", "XB"), Arrays.asList("CS", "XA")).size(), 2);
        Assert.assertEquals(RU.in(Arrays.asList("CSO4", "CS", "XA", "XB"), Arrays.asList("CS", "XA", "XM")).size(), 2);

        Assert.assertEquals(
                RU.in(Arrays.asList(new RiderVO("CSO4"), new RiderVO("CS"), new RiderVO("XA"), new RiderVO("XB")),
                        "product", Arrays.asList("Z", "X")).size(), 0);
        Assert.assertEquals(
                RU.in(Arrays.asList(new RiderVO("CSO4"), new RiderVO("CS"), new RiderVO("XA"), new RiderVO("XB")),
                        "product", Arrays.asList("CS", "XA")).size(), 2);
        Assert.assertEquals(
                RU.in(Arrays.asList(new RiderVO("CSO4"), new RiderVO("CS"), new RiderVO("XA"), new RiderVO("XB")),
                        "product", Arrays.asList("CS", "XA", "XM")).size(), 2);

        Assert.assertEquals(RU.nin(Arrays.asList("CSO4", "CS", "XA", "XB"), Arrays.asList("X", "XS")).size(), 4);
        Assert.assertEquals(RU.nin(Arrays.asList("CSO4", "CS", "XA", "XB"), Arrays.asList("CS")).size(), 3);
        Assert.assertEquals(RU.nin(Arrays.asList("CSO4", "CS", "XA", "XB"), Arrays.asList("CS", "XA", "XM")).size(), 2);


        Assert.assertEquals(
                RU.nin(Arrays.asList(new RiderVO("CSO4"), new RiderVO("CS"), new RiderVO("XA"), new RiderVO("XB")),
                        "product", Arrays.asList("Z", "X")).size(), 4);
        Assert.assertEquals(
                RU.nin(Arrays.asList(new RiderVO("CSO4"), new RiderVO("CS"), new RiderVO("XA"), new RiderVO("XB")),
                        "product", Arrays.asList("CS")).size(), 3);
        Assert.assertEquals(
                RU.nin(Arrays.asList(new RiderVO("CSO4"), new RiderVO("CS"), new RiderVO("XA"), new RiderVO("XB")),
                        "product", Arrays.asList("CS", "XA", "XM")).size(), 2);

    }

    @SuppressWarnings("rawtypes")
    public static List list(Object... data) {
        if (data == null) {
            return new ArrayList();
        }
        return Arrays.asList(data);
    }
}
