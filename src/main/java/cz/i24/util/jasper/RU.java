/*
 * Copyright (c) 2014 Karumien s.r.o.
 * 
 * The contractor, Karumien s.r.o., does not take any responsibility for defects
 * arising from unauthorized changes to the source code.
 */
package cz.i24.util.jasper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import net.sf.jasperreports.engine.util.FileResolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * ReportUtils for JasperReports.
 * 
 * @author <a href="miroslav.svoboda@karumien.com">Miroslav Svoboda</a>
 * @version 1.0
 * @since 27.04.2014 10:11:12
 */
public final class RU {

    public static final String DATE_FORMAT = "dd.MM.yyyy";

    public static final String NUMBER_FORMAT = "#,##0.00;-#,##0.00";

    public static final String DIVIDED_BY_ZERO_VALUE = "--";

    public static final String TAB = "_______";

    public static final String NBSP = "_";

    public static final String BASE_FONT = "Helvetica";

    public static final String CHECKBOX_CHECKED = "X";

    public static final String CHECKBOX_NOT_CHECKED = " ";

    public static final int MATH_ROUND = 0;

    public static final int MATH_FLOOR = -1;

    public static final int MATH_CEIL = 1;

    public static final int VERSION_LEVEL = 3;

    public static Locale defaultLocale = new Locale("cs", "CZ");


    private RU() {
    }

    public static QRCodeColored getQRCodeColored(String text, int width, int onColor, int offColor) {
        return QRCodeColored.from(text).to(QRCodeColored.PNG).withErrorCorrection(ErrorCorrectionLevel.M)
                .withSize(width, width).withColor(onColor, offColor);
    }

    public static InputStream qr(String text, int width, int onColor, int offColor) {
        return new ByteArrayInputStream(getQRCodeColored(text, width, onColor, offColor).stream().toByteArray());
    }


    // --------------------------------------------------------------------------

    /* DATE FORMAT FUNCTIONS */

    public static String formatDate(Date date, String mask) {
        if (date == null) {
            return "";
        }

        if (mask == null) {
            mask = DATE_FORMAT;
        }

        return new SimpleDateFormat(mask).format(date);
    }

    /* DATE FORMAT ALIAS */

    public static String formatDate(Date date) {
        return formatDate(date, null);
    }


    // --------------------------------------------------------------------------

    /* NUMBER FORMAT FUNCTIONS */

    public static String formatNumber(Number value, String mask, double round) {
        double val = 0;
        if (value != null) {
            val = value.doubleValue();
        }
        return formatNumber(val, mask, round);
    }

    public static String formatNumber(double value, String mask, double round) {

        if (mask == null) {
            mask = NUMBER_FORMAT;
        }

        if (round < 0) {

            value = Math.round(value / round) * round;

        } else {
            double rounding = 10;
            if (mask.lastIndexOf(".") > 0) {
                String ma = mask.substring(mask.lastIndexOf(".") + 1);
                if (ma.length() > 0) {
                    double len = ma.length();
                    rounding = Math.pow(rounding, len);

                    value = Math.round(value * rounding) / rounding;
                }
            }
        }

        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(defaultLocale);
        df.applyPattern(mask);
        return df.format(value);
    }


    public static Object json(Object data, String hierarchy) {

        if (!(data instanceof JsonNode) || isBlank(hierarchy)) {
            return null;
        }

        try {

            String[] parts = hierarchy.split("[.]");
            JsonNode actual = (JsonNode) data;

            for (String part : parts) {
                actual = actual.get(part);
                if (actual == null) {
                    break;
                }
            }

            if (actual != null) {

                if (actual.isArray()) {

                    List<JsonNode> dataList = new ArrayList<JsonNode>();

                    for (final JsonNode objNode : actual) {
                        dataList.add(objNode);
                    }

                    return dataList;
                }

                return actual.asText();
            }

        } catch (Exception e) {
            return hierarchy;
        }

        return null;
    }

    /* NUMBER FORMAT ALIASES */

    public static String formatNumber(Number value) {
        return formatNumber(value, 0);
    }

    public static String formatNumber(Number value, double round) {
        return formatNumber(value, null, round);
    }

    public static String formatNumber(Number value, String mask) {
        return formatNumber(value, mask, 0);
    }


    public static String formatNumber(double value, String mask) {
        return formatNumber(value, mask, 0);
    }

    public static String formatNumber(double value, double round) {
        return formatNumber(value, null, 0);
    }

    // --------------------------------------------------------------------------

    /* GLOBAL FORMAT FUNCTION */

    public static String format(Object obj, String mask, double round) {

        if (obj == null) {
            return "";
        }

        if (obj instanceof Date) {
            return formatDate((Date) obj, mask);
        }

        if (obj instanceof Number) {
            return formatNumber((Number) obj, mask, round);
        }

        return obj.toString();
    }

    /* GLOBAL FORMAT ALIASES */

    public static String format(Object obj) {
        return format(obj, 0);
    }

    public static String format(Object obj, double round) {
        return format(obj, null, round);
    }

    public static String format(Object obj, String mask) {
        return format(obj, mask, 0);
    }


    public static String f(Object obj) {
        return format(obj);
    }

    public static String f(Object obj, String mask) {
        return format(obj, mask);
    }

    public static String f(Object obj, double round) {
        return format(obj, round);
    }

    public static String f(Object obj, String mask, double round) {
        return format(obj, mask, round);
    }

    // --------------------------------------------------------------------------


    /**
     * compatibility
     *
     * @deprecated Use method formatDate(Date date)
     */
    @Deprecated
    public static String getStringFromDate(Date date) {
        return formatDate(date);
    }

    public static String w(Object printWhen, Object value) {
        return RU.when(printWhen, value);
    }

    public static String wn(Object printWhen, Object value) {
        return RU.whenNot(printWhen, value);
    }

    public static String when(Object printWhen, Object value) {
        return RU.nn(printWhen(printWhen, value));
    }

    public static String whenNot(Object printWhen, Object value) {
        return RU.nn(printWhenNot(printWhen, value));
    }

    public static Object printWhen(Object printWhen, Object value) {
        if (asBool(printWhen) == null) {
            return null;
        }
        return Boolean.TRUE.equals(asBool(printWhen)) ? value : null;
    }

    public static Object printWhenNot(Object printWhen, Object value) {
        if (asBool(printWhen) == null) {
            return null;
        }
        return Boolean.TRUE.equals(asBool(printWhen)) ? null : value;
    }

    /*
     * return string with first upper case
     */
    public static String getWithFirstUpperCase(String string) {
        if (string == null) {
            return "";
        }

        if (string.length() == 1) {
            return string.substring(0, 1).toUpperCase();
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static Date calculateTimeDifference(Date startTime, Date endTime) {

        if (startTime == null || endTime == null) {
            return null;
        }

        return new Date(endTime.getTime() - startTime.getTime());
    }

    public static String substringRight(String s, int chars) {

        if (s == null) {
            return "";
        }

        if (s.length() > chars) {
            s = s.subSequence(s.length() - chars, s.length()).toString();
        }
        return s;
    }

    public static String getLetterFromPosition(String s, int position) {

        if (s == null) {
            s = "";
        } else if (position <= s.length()) {
            s = s.substring(position - 1, position);
        } else {
            s = "";
        }

        return s;
    }

    public static Integer getMonth(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new Integer(cal.get(Calendar.MONTH) + 1);

    }

    public static Integer getYear(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new Integer(cal.get(Calendar.YEAR));

    }

    public static Integer getDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new Integer(cal.get(Calendar.DAY_OF_MONTH));

    }

    /**
     * Not null string (blank)
     *
     * @param object
     * @return
     */
    public static String nn(Object object) {
        if (object == null) {
            return "";
        }

        return object.toString();
    }


    /**
     * Not null string (blank)
     *
     * @param object
     * @return
     */
    public static String nn(Object object, Object defaultString) {
        if (object == null || object.toString().isEmpty()) {
            return defaultString == null ? "" : defaultString.toString();
        }

        return object.toString();
    }

    /**
     * 
     * @deprecated use {@link #nn(Object)}
     * @param object
     * @return
     */
    @Deprecated
    public static String blank(Object object) {
        return nn(object);
    }

    public static String percentageOf(Number value, Number base) {
        return percentageOf(value, base, null);
    }

    public static String percentageOf(Number value, Number base, String mask) {
        if (base == null || base.doubleValue() == 0) {
            return DIVIDED_BY_ZERO_VALUE;
        }

        if (value == null) {
            return formatNumber(0, mask);
        }

        return formatNumber(value.doubleValue() / base.doubleValue() * 100d, mask);

    }


    /**
     * @return difference between two Dates in string format hh:mm:ss
     * */
    public static String dateDifference(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTime(startDate);
        endCal.setTime(endDate);

        long diffInMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
        long hours = diffInMillis / (1000 * 60 * 60);
        long minutes = diffInMillis % (1000 * 60 * 60) / (1000 * 60);
        long seconds = diffInMillis % (1000 * 60 * 60) % (1000 * 60) / 1000;
        // long millis = ((diffInMillis % (1000*60*60)) % (1000*60)) % 1000;

        // String millisStr = (millis<10 ? "00" : (millis<100 ? "0" : ""))+millis;
        String secondsStr = (seconds < 10 ? "0" : "") + seconds;
        String minutesStr = (minutes < 10 ? "0" : "") + minutes;
        String hoursStr = (hours < 10 ? "0" : "") + hours;

        return hoursStr + ":" + minutesStr + ":" + secondsStr;
    }


    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isBlank(Object o) {
        if (o == null) {
            return true;
        }
        return o.toString().length() == 0;
    }

    public static String red(Object o) {
        return font("red", blank(o));
    }

    public static String blue(Object o) {
        return font("blue", blank(o));
    }

    public static String green(Object o) {
        return font("green", blank(o));
    }

    public static String yellow(Object o) {
        return font("yellow", blank(o));
    }

    public static String pink(Object o) {
        return font("pink", blank(o));
    }

    public static String cyan(Object o) {
        return font("cyan", blank(o));
    }

    public static String white(Object o) {
        return font("white", blank(o));
    }

    public static String orange(Object o) {
        return font("orange", blank(o));
    }

    public static String silver(Object o) {
        return font("silver", blank(o));
    }

    /**
     * nithia color
     *
     * @param o
     * @return
     */
    public static String nc(Object o) {
        return font("#D16B1B", blank(o));
    }

    public static String li() {
        return white("___") + RU.style("wingding", "\u00fa") + " ";
    }

    public static String i(Object o) {
        return tag("i", blank(o));
    }

    public static String b(Object o) {
        return tag("b", blank(o));
    }

    public static String u(Object o) {
        return tag("u", blank(o));
    }

    public static String s(Object original, Boolean striked) {
        return s(original, striked, nn(original).length() <= 3);
    }

    public static String s(Object original, Boolean striked, Boolean addSpaces) {
        if (original == null) {
            return "";
        }

        String space = "";
        if (Boolean.TRUE.equals(addSpaces)) {
            space = " ";
        }

        return "<style" + (Boolean.TRUE.equals(striked) ? " isStrikeThrough=\"true\"" : "") + ">" + space
                + blank(original) + space + "</style>";
    }

    public static String u(Object o, Boolean striked) {
        return Boolean.TRUE.equals(striked) ? u(o) : blank(o);
    }

    public static String bi(Object o) {
        return tag("i", tag("b", blank(o)));
    }

    /**
     * compatibility
     *
     * @deprecated Use method i(Object o)
     */
    @Deprecated
    public static String italic(Object o) {
        return i(o);
    }

    /**
     * compatibility
     *
     * @deprecated Use method b(Object o)
     */
    @Deprecated
    public static String bold(Object o) {
        return b(o);
    }

    /**
     * compatibility
     *
     * @deprecated Use method u(Object o)
     */
    @Deprecated
    public static String underline(Object o) {
        return u(o);
    }

    /**
     * compatibility
     *
     * @deprecated Use method bi(Object o)
     */

    @Deprecated
    public static String boldItalic(Object o) {
        return bi(o);
    }


    public static String font(String color, String original) {
        if (original == null) {
            return "";
        }
        return "<font color=\"" + color + "\">" + original + "</font>";
    }

    public static String tag(String name, String original) {
        if (original == null) {
            return "";
        }
        return "<" + name + ">" + original + "</" + name + ">";
    }


    /*
     * public static String format(Object obj, int decimal) {
     * 
     * if (obj instanceof Number) {
     * String a = "";
     * if (decimal>0)
     * a = ".";
     * 
     * for (int i=0; i<decimal; i++)
     * a+="0";
     * 
     * return format(obj, "#,##0"+a);
     * }
     * 
     * return format(obj);
     * }
     */

    public static String tab() {
        return white(TAB);
    }

    public static String paragraph(int size) {
        return "\n<font size=\"" + size + "\" color=\"white\">X</font>\n";
    }

    public static String paragraph() {
        return paragraph(4);
    }

    public static String paragraph(String text) {
        return paragraph(text, 4);
    }

    public static String paragraph(String text, int size) {

        String preparedText = "";

        if (text == null) {
            return preparedText;
        }

        String[] separated = text.split("\n\t");

        for (String par : separated) {
            preparedText += par + paragraph(size);
        }

        return preparedText;
    }


    /*
     * public static String tab(String color) {
     * return font(color, (TAB));
     * }
     */

    public static String tab(String text) {
        return white(text);
    }

    public static String n() {
        return white(NBSP);
    }

    public static String nbsp() {
        return white(NBSP);
    }


    public static String nbsp(String color) {
        return font(color, NBSP);
    }

    public static String style(String pdfFontName, String original, int size) {
        return style(pdfFontName, original, size, true);
    }


    public static Object get(List<?> collection, int index) {

        if (collection == null || collection.isEmpty() || index < 0 || index >= collection.size()) {
            return null;
        }

        return collection.get(index);

    }

    public static String style(String pdfFontName, String original, int size, boolean trueType) {

        if (original == null) {
            return "";
        }

        if (pdfFontName == null) {
            return "";
        }

        String finalString = "<style pdfFontName=\"" + pdfFontName.toLowerCase() + "." + (trueType ? "ttf" : "otf")
                + "\"";

        if (size > 0) {
            finalString += " size=\"" + size + "\"";
        }

        finalString += ">" + original + "</style>";
        return finalString;
    }


    public static String style(String pdfFontName, String original) {
        return style(pdfFontName, original, -1, true);
    }

    public static String style(String pdfFontName, String original, boolean trueType) {
        return style(pdfFontName, original, -1, trueType);
    }

    public static String b(String baseFontName, String original, int size) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "bd", original, size, true);
    }

    public static String b(String baseFontName, String original, int size, boolean trueType) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "bd", original, size, trueType);
    }

    public static String b(String baseFontName, String original) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "bd", original, -1, true);
    }

    public static String b(String baseFontName, String original, boolean trueType) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "bd", original, -1, trueType);
    }

    public static String i(String baseFontName, String original, int size) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "i", original, size, true);
    }

    public static String i(String baseFontName, String original, int size, boolean trueType) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "i", original, size, trueType);
    }

    public static String sup(String original) {
        return tag("sup", original);
    }

    public static String sub(String original) {
        return tag("sub", original);
    }

    public static String i(String baseFontName, String original) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "i", original, -1, true);
    }

    public static String i(String baseFontName, String original, boolean trueType) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "i", original, -1, trueType);
    }

    public static String bi(String baseFontName, String original, int size) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "bi", original, size, true);
    }

    public static String bi(String baseFontName, String original, int size, boolean trueType) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "bi", original, size, trueType);
    }

    public static String bi(String baseFontName, String original) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "bi", original, -1, true);
    }

    public static String bi(String baseFontName, String original, boolean trueType) {
        if (baseFontName == null) {
            baseFontName = BASE_FONT;
        }
        return style(baseFontName + "bi", original, -1, trueType);
    }

    public static String checkbox(Boolean value) {
        if (value != null && value.booleanValue()) {
            return CHECKBOX_CHECKED;
        } else {
            return CHECKBOX_NOT_CHECKED;
        }
    }

    public static String checkbox(Object dbValue, String value) {
        if (dbValue != null && value != null && value.equals(String.valueOf(dbValue))) {
            return CHECKBOX_CHECKED;
        } else {
            return CHECKBOX_NOT_CHECKED;
        }
    }


    public static Number math(Number num, double decimals, int type) {

        Number ret = num;

        if (num instanceof BigDecimal) {
            ret = new BigDecimal(round(num.doubleValue() / decimals, type) * decimals);
        } else if (num instanceof Double) {
            ret = new Double(round(num.doubleValue() / decimals, type) * decimals);
        } else if (num instanceof Float) {
            ret = new Float(round(num.doubleValue() / decimals, type) * decimals);
        }

        return ret;

    }

    private static double round(double d, int type) {
        double returnVal = d;

        switch (type) {

        case MATH_FLOOR:
            returnVal = Math.floor(d);
            break;

        case MATH_CEIL:
            returnVal = Math.ceil(d);
            break;

        default:
            returnVal = Math.round(d);
            break;
        }

        return returnVal;
    }


    public static String barCode39(String value) {

        if (value == null) {
            value = "";
        }

        String retVal = "";

        for (int i = 0; i < value.length(); i++) {
            retVal += value.charAt(i) + " ";
        }

        retVal = "* " + retVal + "*";

        return retVal;

    }


    public static String message(String[] messages, Number index) {

        if (index == null || messages == null) {
            return "";
        }

        if (index.intValue() > messages.length) {
            return "";
        }

        return messages[index.intValue()];
    }


    public static String message(String[] messages, int[] indexes, Number index) {

        if (index == null || messages == null) {
            return "";
        }

        int finalIndex = -1;
        for (int i = 0; i < indexes.length; i++) {

            if (indexes[i] == index.intValue()) {
                finalIndex = i;
            }
        }

        if (finalIndex == -1) {
            return "";
        }

        return messages[finalIndex];
    }

    public static String dateToAcc(Date date) {

        if (date == null) {
            return "";
        }

        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.YEAR) + "." + addZerosBefore("" + (cal.get(Calendar.MONTH) + 1), 2);

    }

    public static String monthAdd(String acc, int months) {

        if (acc == null) {
            return "";
        }

        int year = Integer.valueOf(acc.substring(0, 4));
        int month = Integer.valueOf(acc.substring(5)) - 1;
        int day = 1;

        Calendar cal = new GregorianCalendar(year, month, day);
        cal.add(Calendar.MONTH, months);

        return cal.get(Calendar.YEAR) + "." + addZerosBefore("" + (cal.get(Calendar.MONTH) + 1), 2);
    }

    /**
     * Method addZerosBefore.
     *
     * @param orderNo
     * @param count
     * @return String
     */
    public static String addZerosBefore(String orderNo, int count) {

        if (orderNo == null) {
            return "";// orderNo = "";
        }

        if (orderNo.length() > count) {
            orderNo = "?" + orderNo.substring(orderNo.length() - count - 1, orderNo.length() - 1);
        } else {

            int le = orderNo.length();

            for (int i = 0; i < count - le; i++) {
                orderNo = "0" + orderNo;
            }

        }

        return orderNo;
    }

    public static String ts(String separator, Object... data) {
        return tokens(separator, data);
    }

    public static String tokens(String separator, Object... data) {

        if (data == null || data.length == 0 || separator == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        
        boolean separate = false;
        
        for (Object token : data) {
            
            if (nn(token).trim().length() == 0) {
                continue;
            }

            if (separate) {
                sb.append(separator);
            } else {
                separate = true;
            }

            sb.append(nn(token));
        }
        
        return sb.toString();

    }

    public static Object getResource(FileResolver fileResolver, String filename) {

        if (fileResolver == null || !(fileResolver instanceof ClassPathFileResolver)) {
            return filename;
        }

        for (String path : ((ClassPathFileResolver) fileResolver).getClassPath()) {
            InputStream is = RU.class.getResourceAsStream(path + "/" + filename);
            if (is != null) {
                return is;
            }
        }

        return filename;
    }

    public static String bool(Object value, String trueValue, String falseValue) {

        Boolean valueBool = asBool(value);

        if (valueBool == null) {
            return null;
        }

        return Boolean.TRUE.equals(asBool(value)) ? trueValue : falseValue;

    }



    public static Boolean asBool(Object value) {

        if (value == null || value.toString().isEmpty()) {
            return null;
        }

        Boolean valueBool = null;

        if (value instanceof String || value instanceof Boolean) {
            valueBool = Boolean.valueOf(value.toString());
        }

        if (valueBool == null) {
            return null;
        }

        return valueBool;
    }

}
