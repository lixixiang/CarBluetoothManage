package com.shenzhen.honpe.carbluetoothmanage.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * FileName: StringNullAdapter
 * Author: asus
 * Date: 2020/10/19 13:59
 * Description:
 */
public class StringNullAdapter extends TypeAdapter {
    /**
     * Writes one JSON value (an array, object, string, number, boolean or null)
     * for {@code value}.
     *
     * @param writer
     * @param ob the Java object to write. May be null.
     */
    @Override
    public void write(JsonWriter writer, Object ob) throws IOException {
        if (ob == null) {
            writer.value("");
            return;
        }
        writer.value((Boolean) ob);
    }

    @Override
    public String read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return "";
        }
        return reader.nextString();
    }

}
