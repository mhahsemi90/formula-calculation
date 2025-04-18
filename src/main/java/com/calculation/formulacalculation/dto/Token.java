package com.calculation.formulacalculation.dto;

public class Token implements Cloneable {
    private TokenType tokenType;
    private String value;
    private Integer level;
    private Integer lineNumber;

    public Token() {
    }

    public Token(TokenType tokenType, String value, Integer level, Integer lineNumber) {
        this.tokenType = tokenType;
        this.value = value;
        this.level = level;
        this.lineNumber = lineNumber;
    }

    public Token(String value, Integer lineNumber) {
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public Token(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public Token clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (Token) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
