package com.calculation.formulacalculation.dto;

import java.util.Map;

public class InputAndOutputParameterElement {
    private Long elementId;
    private Map<InputParameterDto, String> inputParamMapList;
    private Map<OutputParameterDto, FormulaDto> outputParamMapList;

    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public Map<InputParameterDto, String> getInputParamMapList() {
        return inputParamMapList;
    }

    public void setInputParamMapList(Map<InputParameterDto, String> inputParamMapList) {
        this.inputParamMapList = inputParamMapList;
    }

    public Map<OutputParameterDto, FormulaDto> getOutputParamMapList() {
        return outputParamMapList;
    }

    public void setOutputParamMapList(Map<OutputParameterDto, FormulaDto> outputParamMapList) {
        this.outputParamMapList = outputParamMapList;
    }
}
