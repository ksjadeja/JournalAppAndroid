package com.journalapp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntriesMap extends HashMap {

    public static HashMap<String,Integer> EntriesIndex = new HashMap<>();

    public static void addFirst(String key){
        for(Map.Entry<String, Integer> element : EntriesIndex.entrySet()){
            element.setValue(element.getValue()+1);
        }
        EntriesIndex.put(key,0);
    }

    public static void delete(String key, Integer value){
        for(Map.Entry<String, Integer> element : EntriesIndex.entrySet()){
            if(element.getValue()>value){
                element.setValue(element.getValue()-1);
            }
        }
        EntriesIndex.remove(key);
    }

}
