package com.calculation.formulacalculation.impl;

import com.calculation.formulacalculation.dto.Token;
import com.calculation.formulacalculation.dto.TokenType;
import com.calculation.formulacalculation.interfaces.FetchFunctionBusinessService;
import org.apache.commons.lang3.BooleanUtils;
import org.example.functionapi.FunctionDto;

import java.util.*;

public class FetchFunctionBusinessServiceImpl implements FetchFunctionBusinessService {
    @Override
    public Boolean fillFunctionList(
            String parameterName,
            List<Integer> indicatorIndex,
            List<FunctionDto> functionList,
            List<Token> tokenList,
            Map<String, Boolean> outPutParameterNameList
    ) {
        List<String> functionCodeList = getAllFunction();
        List<Boolean> hasOutputParamThatNotCalculated = new ArrayList<>();
        hasOutputParamThatNotCalculated.add(false);
        for (int i = 0; i < tokenList.size(); i++) {
            if (functionCodeList.contains(tokenList.get(i).getValue())) {
                List<Token> body = getFunctionBody(tokenList, i);
                String functionId = getFunctionIndicator(
                        parameterName,
                        body,
                        tokenList.get(i).getValue(),
                        indicatorIndex,
                        //0,
                        functionList,
                        outPutParameterNameList,
                        hasOutputParamThatNotCalculated
                );
                tokenList.set(
                        i,
                        new Token(
                                TokenType.VARIABLE,
                                functionId,
                                tokenList.get(i).getLevel(),
                                tokenList.get(i).getLineNumber()
                        )
                );
            }
        }
        return hasOutputParamThatNotCalculated.get(0);
    }

    private List<String> getAllFunction() {
        return new ArrayList<>();
    }

    private String getFunctionIndicator(
            String parameterName,
            List<Token> functionBody,
            String functionName,
            List<Integer> indicatorIndex,
            //Integer levelIndex,
            List<FunctionDto> functionList,
            Map<String, Boolean> outPutParameterNameList,
            List<Boolean> hasOutputParamThatNotCalculated) {
        List<String> functionCodeList = getAllFunction();
        for (int i = 0; i < functionBody.size(); i++) {
            if (functionCodeList.contains(functionBody.get(i).getValue())) {
                List<Token> body = getFunctionBody(functionBody, i);
                String functionId = getFunctionIndicator(
                        parameterName,
                        body,
                        functionBody.get(i).getValue(),
                        indicatorIndex,
                        //0,
                        functionList,
                        outPutParameterNameList,
                        hasOutputParamThatNotCalculated
                );
                functionBody.set(
                        i,
                        new Token(
                                TokenType.VARIABLE,
                                functionId,
                                functionBody.get(i).getLevel(),
                                functionBody.get(i).getLineNumber()
                        )
                );
            }
        }
        FunctionDto functionDto = new FunctionDto();
        functionDto.setId(functionName + indicatorIndex.get(0));
        functionDto.setArgumentMap(new LinkedHashMap<>());
        StringBuilder argumentBuilder = new StringBuilder();
        boolean startList = false;
        for (Token o : functionBody) {
            if (o.getValue().equalsIgnoreCase("("))
                startList = true;
            if (o.getValue().equalsIgnoreCase(",") && !startList) {
                String[] args = argumentBuilder.toString().split("=");
                if (args.length > 1) {
                    if (outPutParameterNameList.containsKey(args[1])) {
                        if (BooleanUtils.isFalse(outPutParameterNameList.get(args[1])))
                            hasOutputParamThatNotCalculated.set(0, true);
                        args[1] = args[1].substring(3, args[1].length() - 2);
                    }
                    functionDto.getArgumentMap().put(args[0], args[1]);
                }
                argumentBuilder = new StringBuilder();
            } else {
                argumentBuilder.append(o);
            }
            if (o.getValue().equalsIgnoreCase(")"))
                startList = false;
        }
        if (!argumentBuilder.isEmpty()) {
            String[] args = argumentBuilder.toString().split("=");
            if (args.length > 1) {
                if (outPutParameterNameList.containsKey(args[1])) {
                    if (BooleanUtils.isFalse(outPutParameterNameList.get(args[1])))
                        hasOutputParamThatNotCalculated.set(0, true);
                    args[1] = args[1].substring(3, args[1].length() - 2);
                }
                functionDto.getArgumentMap().put(args[0], args[1]);
            }
        }
        functionDto = getSameFunctionDtoFromList(functionDto, functionList);
        indicatorIndex.set(0, indicatorIndex.get(0) + 1);
        return functionDto.getId();
    }

    private FunctionDto getSameFunctionDtoFromList(FunctionDto functionDto, List<FunctionDto> functionList) {
        FunctionDto sameFunction = functionList
                .stream()
                //.filter(o -> o.getLevel().equals(functionDto.getLevel()))
                .filter(o -> o.getFunctionName().equalsIgnoreCase(functionDto.getFunctionName()))
                .filter(o -> o.getArgumentMap().size() == functionDto.getArgumentMap().size())
                .filter(o -> {
                    boolean isSame = true;
                    for (Map.Entry<String, Object> entry : o.getArgumentMap().entrySet()) {
                        if (
                                !String.valueOf(entry.getValue())
                                        .equalsIgnoreCase(String.valueOf(functionDto.getArgumentMap().get(entry.getKey())))
                        ) {
                            isSame = false;
                            break;
                        }
                    }
                    return isSame;
                }).findFirst()
                .orElse(null);
        if (sameFunction == null) {
            functionList.add(functionDto);
            return functionDto;
        } else {
            return sameFunction;
        }
    }

    private List<Token> getFunctionBody(List<Token> tokenList, int mainIndex) {
        List<Token> newSentence = new ArrayList<>();
        Deque<String> parenthesisStack = new LinkedList<>();
        do {
            newSentence.add(tokenList.get(mainIndex + 1));
            if (tokenList.get(mainIndex + 1).getValue().equalsIgnoreCase("("))
                parenthesisStack.push(tokenList.get(mainIndex + 1).getValue());
            if (tokenList.get(mainIndex + 1).getValue().equalsIgnoreCase(")"))
                parenthesisStack.pop();
            tokenList.remove(mainIndex + 1);
        } while (!parenthesisStack.isEmpty());
        newSentence.remove(0);
        newSentence.remove(newSentence.size() - 1);
        return newSentence;
    }
}
