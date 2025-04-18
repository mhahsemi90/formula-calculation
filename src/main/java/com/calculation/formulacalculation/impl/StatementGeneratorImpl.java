package com.calculation.formulacalculation.impl;

import com.calculation.formulacalculation.dto.Token;
import com.calculation.formulacalculation.dto.TokenType;
import com.calculation.formulacalculation.interfaces.StatementGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StatementGeneratorImpl implements StatementGenerator {
    @Override
    public List<Token> parsingToListOfTokenList(String script) {
        List<Character> characterList = new ArrayList<>();
        for (char c : script.toCharArray()) {
            characterList.add(c);
        }
        List<Character> mainSeparator = getAllSeparatorCharacter();
        List<Character> noChar = getNoChar();
        List<String> mainFormula = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean isFirstTextEnd = false;
        for (int i = 0, characterListSize = characterList.size(); i < characterListSize; i++) {
            Character character = characterList.get(i);
            if (character.equals('\\')) {
                i++;
                builder.append("\\");
                if (i < characterListSize) {
                    character = characterList.get(i);
                    builder.append(character);
                }
            } else if (isFirstTextEnd) {
                builder.append(character);
                if (character.equals('\'') || character.equals('"')) {
                    mainFormula.add(builder.toString());
                    builder = new StringBuilder();
                    isFirstTextEnd = false;
                }
            } else {
                if (character.equals('\'') || character.equals('"')) {
                    if (StringUtils.isNotBlank(builder))
                        mainFormula.add(builder.toString().trim());
                    builder = new StringBuilder();
                    builder.append(character);
                    isFirstTextEnd = true;
                } else {
                    if (mainSeparator.contains(character)) {
                        if (StringUtils.isNotBlank(builder))
                            mainFormula.add(builder.toString().trim());
                        if (!character.equals(' ') ||
                                !mainFormula.isEmpty() && !mainFormula.get(mainFormula.size() - 1).equals(" ")
                        )
                            mainFormula.add(String.valueOf(character));
                        builder = new StringBuilder();
                    } else {
                        if (!noChar.contains(character))
                            builder.append(character);
                    }
                }
            }
        }
        if (!builder.isEmpty()) {
            mainFormula.add(builder.toString());
        }
        int size = mainFormula.size();
        List<String> stringTokenList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String token = mainFormula.remove(0);
            if (getHaveSequenceString().contains(token)) {
                String typeSequence = "NONE";
                if (mainFormula.size() > 2) {
                    String sequence = token + mainFormula.get(0) + mainFormula.get(1) + mainFormula.get(2);
                    if (sequence.equals(">>>=")) {
                        typeSequence = "FOUR";
                        mainFormula.remove(0);
                        mainFormula.remove(0);
                        i += 2;
                        token = sequence;
                    }
                }
                if (mainFormula.size() > 1 && !typeSequence.equalsIgnoreCase("FOUR")) {
                    String sequence = token + mainFormula.get(0) + mainFormula.get(1);
                    if (sequence.equals("**=") || sequence.equals("===") ||
                            sequence.equals(">>=") || sequence.equals("<<=") ||
                            sequence.equals("!==") || sequence.equals(">>>")) {
                        typeSequence = "THREE";
                        mainFormula.remove(0);
                        mainFormula.remove(0);
                        i += 2;
                        token = sequence;
                    }
                }
                if (!mainFormula.isEmpty() && !typeSequence.equalsIgnoreCase("THREE")) {
                    String sequence = token + mainFormula.get(0);
                    if (sequence.equals("++") || sequence.equals("--") ||
                            sequence.equals("**") || sequence.equals("+=") ||
                            sequence.equals("-=") || sequence.equals("/=") ||
                            sequence.equals("%=") || sequence.equals("==") ||
                            sequence.equals("!=") || sequence.equals(">=") ||
                            sequence.equals("<=") || sequence.equals("&&") ||
                            sequence.equals("||") || sequence.equals("<<") ||
                            sequence.equals("//") || sequence.equals("/*") ||
                            sequence.equals("*/") || sequence.equals(">>")) {
                        mainFormula.remove(0);
                        i += 1;
                        token = sequence;
                    }
                }
            }
            stringTokenList.add(token);
        }
        boolean isRemove = false;
        for (int i = stringTokenList.size() - 1; i >= 0; i--) {
            if (isRemove && (stringTokenList.get(i).equals(" ") || stringTokenList.get(i).equals("\n")))
                stringTokenList.remove(i);
            if (stringTokenList.get(i).equals("(") ||
                    stringTokenList.get(i).equals("[")) {
                isRemove = true;
            }
            if (!(stringTokenList.get(i).equals("(") ||
                    stringTokenList.get(i).equals("[") ||
                    stringTokenList.get(i).equals(" ") ||
                    stringTokenList.get(i).equals("\n"))) {
                isRemove = false;
            }
        }
        List<Token> tokenList = new ArrayList<>();
        List<String> allSeparatorTokenStringList = getAllOperatorAndNotOperator();
        int lineNumber = 1;
        int level = 0;
        for (String s1 : stringTokenList) {
            Token token = new Token(lineNumber);
            if (getAllKeyword().contains(s1)) {
                token.setTokenType(TokenType.KEYWORD);
                token.setValue(s1);
                token.setLevel(level);
            } else if (allSeparatorTokenStringList.contains(s1)) {
                token.setTokenType(TokenType.PUNCTUATOR);
                if (s1.equals("]") || s1.equals(")") || s1.equals("}"))
                    level--;
                token.setLevel(level);
                if (s1.equals("[") || s1.equals("(") || s1.equals("{"))
                    level++;
                if (s1.equals("\n")) {
                    token.setValue(String.valueOf(lineNumber++));
                    token.setTokenType(TokenType.NEW_LINE);
                } else {
                    token.setValue(s1);
                }
            } else if (
                    s1.startsWith("'") ||
                            s1.startsWith("\"") ||
                            StringUtils.isNumeric(s1) ||
                            getAllLiteralKeyword().contains(s1)) {
                token.setTokenType(TokenType.LITERAL);
                token.setValue(s1);
                token.setLevel(level);
            } else if (StringUtils.isNotBlank(s1)) {
                token.setTokenType(TokenType.VARIABLE);
                token.setValue(s1);
                token.setLevel(level);
            }
            if (token.getTokenType() != null)
                tokenList.add(token);
        }
        if (CollectionUtils.isNotEmpty(tokenList)
                && tokenList.get(tokenList.size() - 1).getTokenType() != TokenType.NEW_LINE)
            tokenList.add(new Token(TokenType.NEW_LINE, String.valueOf(lineNumber), 0, lineNumber));
        return tokenList;
    }
}
