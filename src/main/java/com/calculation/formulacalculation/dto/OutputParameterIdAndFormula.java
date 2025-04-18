package com.calculation.formulacalculation.dto;

public class OutputParameterIdAndFormula {
    private String outputParameterCode;
    private Long elementId;
    private String formula;

    public String getOutputParameterCode() {
        return outputParameterCode;
    }

    public void setOutputParameterCode(String outputParameterCode) {
        this.outputParameterCode = outputParameterCode;
    }

    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
