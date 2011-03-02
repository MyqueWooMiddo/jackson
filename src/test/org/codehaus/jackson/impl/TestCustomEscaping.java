package org.codehaus.jackson.impl;

import java.io.*;

import org.codehaus.jackson.*;

public class TestCustomEscaping  extends main.BaseTest
{
    /**
     * Test to ensure that it is possible to force escaping
     * of non-ASCII characters.
     * Related to [JACKSON-102]
     */
    public void testNonAsciiEscapeWithReader() throws Exception
    {
        _testEscapeNonAscii(false); // reader
    }

    public void testNonAsciiEscapeWithUTF8Stream() throws Exception
    {
        _testEscapeNonAscii(true); // stream (utf-8)
    }
    
    /*
    /********************************************************
    /* Secondary test methods
    /********************************************************
     */

    private void _testEscapeNonAscii(boolean useStream) throws Exception
    {
        JsonFactory f = new JsonFactory();
        final String VALUE = "chars: [\u00A0]/[\u1234]";
        final String KEY = "fun:\u0088:\u3456";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JsonGenerator jgen;

        // First: output normally; should not add escaping
        if (useStream) {
            jgen = f.createJsonGenerator(bytes, JsonEncoding.UTF8);
        } else {
            jgen = f.createJsonGenerator(new OutputStreamWriter(bytes, "UTF-8"));
        }
        jgen.writeStartArray();
        jgen.writeString(VALUE);
        jgen.writeEndArray();
        jgen.close();
        String json = bytes.toString("UTF-8");
        
        assertEquals("["+quote(VALUE)+"]", json);

        // And then with forced ASCII; first, values

        bytes = new ByteArrayOutputStream();
        if (useStream) {
            jgen = f.createJsonGenerator(bytes, JsonEncoding.UTF8);
        } else {
            jgen = f.createJsonGenerator(new OutputStreamWriter(bytes, "UTF-8"));
        }
        jgen.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
        jgen.writeStartArray();
        jgen.writeString(VALUE);
        jgen.writeEndArray();
        jgen.close();
        json = bytes.toString("UTF-8");
        assertEquals("["+quote("chars: [\\u00A0]/[\\u1234]")+"]", json);

        // and then keys
        bytes = new ByteArrayOutputStream();
        if (useStream) {
            jgen = f.createJsonGenerator(bytes, JsonEncoding.UTF8);
        } else {
            jgen = f.createJsonGenerator(new OutputStreamWriter(bytes, "UTF-8"));
        }
        jgen.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
        jgen.writeStartObject();
        jgen.writeFieldName(KEY);
        jgen.writeBoolean(true);
        jgen.writeEndObject();
        jgen.close();
        json = bytes.toString("UTF-8");
        assertEquals("{"+quote("fun:\\u0088:\\u3456")+":true}", json);
    
    }
}