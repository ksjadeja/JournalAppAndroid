package com.journalapp;

import java.util.HashMap;
import java.util.Map;

public class AccEntriesMap extends HashMap {

    public static HashMap<String,Integer> AccEntriesIndex = new HashMap<>();
    public static void addFirst(String key){
        for(Map.Entry<String, Integer> element : AccEntriesIndex.entrySet()){
            element.setValue(element.getValue()+1);
        }
        AccEntriesIndex.put(key,0);
    }

    public static void delete(String key, Integer value){
        for(Map.Entry<String, Integer> element : AccEntriesIndex.entrySet()){
            if(element.getValue()>value){
                element.setValue(element.getValue()-1);
            }
        }
        AccEntriesIndex.remove(key);
    }
    public static void clearMap()
    {
        AccEntriesIndex.clear();
    }
    public static boolean isKeyPresent(String key)
    {
        return AccEntriesIndex.containsKey(key);
    }

}
