package org.fosstrak.epcis.queryclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.fosstrak.epcis.model.ArrayOfString;
import org.fosstrak.epcis.model.QueryParam;
import org.fosstrak.epcis.utils.TimeParser;

/**
 * This is a helper class which encapsulates common functionality used within
 * the query client classes.
 */
public class QueryClientHelper {

    /**
     * Converts the values in a calendar object into a nicely formatted string.
     * 
     * @param cal
     *            with the Calendar-Date
     * @return String
     */
    public static String printCalendar(final Calendar cal) {
        if (cal == null) {
            return null;
        }
        // set to current timezone
        cal.setTimeZone(TimeZone.getDefault());
        return TimeParser.format(cal);
    }

    /**
     * Converts a space-separated list of strings to an ArrayOfString.
     * 
     * @param txt
     *            A space-separated list of strings.
     * @return An ArrayOfString object containing single string tokens.
     */
    public static ArrayOfString stringListToArray(final String txt) {
        List<String> tokens = Arrays.asList(txt.split(" "));
        ArrayOfString strings = new ArrayOfString();
        strings.getString().addAll(tokens);
        return strings;
    }

    /**
     * Implements a class that holds examples for the EPCIS Query Interface
     * Client.
     * 
     * @author David Gubler
     */
    public static class ExampleQueries {

        private static List<Query> examples = null;

        /**
         * Sets up the examples. Add examples here if you wish.
         */
        private static void initExamples() {
            examples = new ArrayList<Query>();
            Query ex = new Query();
            ex.setDescription("查询一个托盘上的所有聚集");
            ex.setReturnAggregationEvents(true);
            QueryParam param = new QueryParam();
            param.setName("EQ_action");
            param.setValue("ADD");
            ex.getQueryParameters().add(param);
            param = new QueryParam();
            param.setName("MATCH_parentID");
            param.setValue("urn:epc:id:sscc:0614141.1234567890");
            ex.getQueryParameters().add(param);
            examples.add(ex);

            ex = new Query();
            ex.setDescription("返回指定 EPC 在特定时间之后发生的所有事件");
            ex.setReturnObjectEvents(true);
            param = new QueryParam();
            param.setName("GE_eventTime");
            param.setValue("2006-01-01T05:20:31Z");
            ex.getQueryParameters().add(param);
            param = new QueryParam();
            param.setName("MATCH_epc");
            param.setValue("urn:epc:id:sgtin:0034000.987650.2686");
            ex.getQueryParameters().add(param);
            examples.add(ex);

            ex = new Query();
            ex.setDescription("返回指定阅读器产生的所有事件");
            ex.setReturnObjectEvents(true);
            ex.setReturnAggregationEvents(true);
            ex.setReturnQuantityEvents(true);
            ex.setReturnTransactionEvents(true);
            param = new QueryParam();
            param.setName("EQ_readPoint");
            param.setValue("urn:epc:id:sgln:0614141.00729.whatever215");
            ex.getQueryParameters().add(param);
            examples.add(ex);

            ex = new Query();
            ex.setDescription("检索 EPC 的出货日期");
            ex.setReturnObjectEvents(true);
            param = new QueryParam();
            param.setName("EQ_action");
            param.setValue("OBSERVE");
            ex.getQueryParameters().add(param);
            param = new QueryParam();
            param.setName("EQ_bizStep");
            param.setValue("urn:epcglobal:cbv:bizstep:shipping");
            ex.getQueryParameters().add(param);
            param = new QueryParam();
            param.setName("MATCH_epc");
            param.setValue("urn:epc:id:sgtin:0057000.123430.2028");
            ex.getQueryParameters().add(param);
            examples.add(ex);

            ex = new Query();
            ex.setDescription("检索 2006 年退回的所有 EPC");
            ex.setReturnObjectEvents(true);
            param = new QueryParam();
            param.setName("EQ_action");
            param.setValue("OBSERVE");
            ex.getQueryParameters().add(param);
            param = new QueryParam();
            param.setName("GE_eventTime");
            param.setValue("2006-01-01T00:00:00Z");
            ex.getQueryParameters().add(param);
            param = new QueryParam();
            param.setName("LT_eventTime");
            param.setValue("2007-01-01T00:00:00Z");
            ex.getQueryParameters().add(param);
            param = new QueryParam();
            param.setName("EQ_disposition");
            param.setValue("urn:epcglobal:cbv:disp:returned");
            ex.getQueryParameters().add(param);
            examples.add(ex);
        }

        /**
         * @return A List of Query.
         */
        public static List<Query> getExamples() {
            if (examples == null) {
                initExamples();
            }
            return examples;
        }
    }
}