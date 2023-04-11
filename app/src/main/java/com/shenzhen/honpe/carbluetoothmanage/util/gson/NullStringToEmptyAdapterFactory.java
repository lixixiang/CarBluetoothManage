package com.shenzhen.honpe.carbluetoothmanage.util.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * FileName: NullStringToEmptyAdapterFactory
 * Author: asus
 * Date: 2020/10/19 13:58
 * Description:
 */
public class NullStringToEmptyAdapterFactory implements TypeAdapterFactory {
    public TypeAdapter create(Gson gson, TypeToken type) {
        Class rawType = (Class) type.getRawType();
        if (rawType != String.class) {
            return null;
        }
        return (TypeAdapter) new StringNullAdapter();
    }
}
