package com.calculation.formulacalculation.dto;


import java.util.Map;

public class CalculatedOutputParameterForElement {
    private Long elementId;
    private Map<OutputParameterDto, String> outputParamValueMapList;
    private Map<Long, FormulaDto> outputParamFormulaMapList;

    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public Map<OutputParameterDto, String> getOutputParamValueMapList() {
        return outputParamValueMapList;
    }

    public void setOutputParamValueMapList(Map<OutputParameterDto, String> outputParamValueMapList) {
        this.outputParamValueMapList = outputParamValueMapList;
    }

    public Map<Long, FormulaDto> getOutputParamFormulaMapList() {
        return outputParamFormulaMapList;
    }

    public void setOutputParamFormulaMapList(Map<Long, FormulaDto> outputParamFormulaMapList) {
        this.outputParamFormulaMapList = outputParamFormulaMapList;
    }
}
