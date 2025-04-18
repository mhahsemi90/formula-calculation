package com.calculation.formulacalculation.dto;

public class OutputParameterDto {
    private Long id;
    private String code;
    private String title;
    private String dataType;

    public OutputParameterDto(String outputParameterCode) {
        this.code = outputParameterCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
