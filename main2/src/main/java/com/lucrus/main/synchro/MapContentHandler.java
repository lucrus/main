/**
 *
 */
package com.lucrus.main.synchro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luca.russo
 */
public class MapContentHandler extends BaseContentHandler {
    private List<Map<String, String>> res;

    /**
     *
     */
    public MapContentHandler(String rootName) {
        super(rootName);
        this.res = null;
    }


    @Override
    protected void createResultList() {
        this.res = new ArrayList<Map<String, String>>();
    }


    @Override
    protected Object createNewObject() {
        return new HashMap<String, String>();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void addResultObject(Object obj) {
        this.res.add((Map<String, String>) obj);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void setProperty(Object obj, String name, Object value, String type) {
        name = name.replaceAll("_", " ");
        Map<String, Object> map = (Map<String, Object>) obj;
        map.put(name, value);
        if (type != null && type.trim().length() > 0) {
            map.put(name + "§§type", type);
        }
    }


    public List<Map<String, String>> getMap() {
        return this.res;
    }


}
