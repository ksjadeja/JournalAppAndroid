package com.myjournal.journalapp;

import java.util.HashMap;
import java.util.Map;

public class ExpEntriesMap extends HashMap{

    public static HashMap<String,Integer> ExpEntriesIndex = new HashMap<>();
    public static void addFirst(String key){
        for(Map.Entry<String, Integer> element : ExpEntriesIndex.entrySet()){
            element.setValue(element.getValue()+1);
        }
        ExpEntriesIndex.put(key,0);
    }

    public static void delete(String key, Integer value){
        for(Map.Entry<String, Integer> element : ExpEntriesIndex.entrySet()){
            if(element.getValue()>value){
                element.setValue(element.getValue()-1);
            }
        }
        ExpEntriesIndex.remove(key);
    }
}
