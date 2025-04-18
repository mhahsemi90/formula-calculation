package com.calculation.formulacalculation.interfaces;

import java.util.List;
import java.util.Map;

public interface MapService {
    void setObjects(List<List<Object>> objects);

    Map<Object, Map<String, Object>> convertAllListToMap(String referenceHeader);

    Map<Object, Map<String, Object>> convertPartialListToMap(String referenceHeader, String... otherHeader);

    List<List<Object>> convertDataMapToDataList(List<Map<String, Object>> dataList, List<String> headerRowList);
}
