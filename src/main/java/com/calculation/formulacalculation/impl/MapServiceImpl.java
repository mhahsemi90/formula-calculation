package com.calculation.formulacalculation.impl;

import com.calculation.formulacalculation.interfaces.MapService;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class MapServiceImpl implements MapService {
    private List<List<Object>> objects = null;

    @Override
    public void setObjects(List<List<Object>> objects) {
        this.objects = objects;
    }

    @Override
    public Map<Object, Map<String, Object>> convertAllListToMap(String referenceHeader) {
        if (objects == null || objects.size() < 2 || CollectionUtils.isEmpty(objects.get(0))) {
            return new LinkedHashMap<>();
        }
        int referenceHeaderIndex = -1;
        for (int j = 0; j < objects.get(0).size(); j++) {
            if (objects.get(0).get(j).toString().equalsIgnoreCase(referenceHeader)) {
                referenceHeaderIndex = j;
            }
        }
        if (referenceHeaderIndex < 0) {
            throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
        }
        Map<Object, Map<String, Object>> allValueMap = new LinkedHashMap<>();
        for (int i = 1; i < objects.size(); i++) {
            Map<String, Object> rowObjectMap = new LinkedHashMap<>();
            for (int j = 0; j < objects.get(0).size(); j++) {
                rowObjectMap.put(objects.get(0).get(j).toString(), objects.get(i) != null && j < objects.get(i).size() ? objects.get(i).get(j) : null);
            }
            allValueMap.put(objects.get(i) != null && referenceHeaderIndex < objects.get(i).size() ? objects.get(i).get(referenceHeaderIndex) : null, rowObjectMap);
        }
        return allValueMap;
    }


    @Override
    public Map<Object, Map<String, Object>> convertPartialListToMap(String referenceHeader, String[] otherHeader) {
        if (objects == null || objects.size() < 2) {
            return new LinkedHashMap<>();
        }
        int referenceHeaderIndex = -1;
        for (int j = 0; j < objects.get(0).size(); j++) {
            if (objects.get(0).get(j).toString().equalsIgnoreCase(referenceHeader)) {
                referenceHeaderIndex = j;
            }
        }
        if (referenceHeaderIndex < 0) {
            throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
        }
        List<Integer> indexList = new ArrayList<>();
        Arrays.stream(otherHeader).forEach(s -> {
            int headerIndex = -1;
            for (int j = 0; j < objects.get(0).size(); j++) {
                if (objects.get(0).get(j).toString().equalsIgnoreCase(s)) {
                    headerIndex = j;
                }
            }
            if (headerIndex > -1) {
                indexList.add(headerIndex);
            } else {
                throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
            }
        });
        indexList.add(referenceHeaderIndex);
        Map<Object, Map<String, Object>> partialExcelValueMap = new LinkedHashMap<>();
        for (int i = 1; i < objects.size(); i++) {
            Map<String, Object> rowObjectMap = new LinkedHashMap<>();
            for (Integer index : indexList) {
                rowObjectMap.put(objects.get(0).get(index).toString(), objects.get(i).get(index));
            }
            partialExcelValueMap.put(objects.get(i).get(referenceHeaderIndex), rowObjectMap);
        }
        return partialExcelValueMap;
    }

    @Override
    public List<List<Object>> convertDataMapToDataList(List<Map<String, Object>> dataList, List<String> headerRowList) {
        if (dataList == null || CollectionUtils.isEmpty(headerRowList)) {
            throw new RuntimeException("PCN_HEADER_LIST_NOT_SAME");
        }
        List<List<Object>> objects = new ArrayList<>();
        dataList.forEach(objectMap -> {
            List<Object> objectList = new ArrayList<>();
            if (objectMap != null) {
                headerRowList.forEach(s -> objectList.add(objectMap.get(s)));
            }
            objects.add(objectList);
        });
        return objects;
    }
}
