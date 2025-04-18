package com.calculation.formulacalculation.interfaces;


import com.calculation.formulacalculation.dto.Token;
import org.example.functionapi.FunctionDto;

import java.util.List;
import java.util.Map;

public interface FetchFunctionBusinessService {
    Boolean fillFunctionList(
            String parameterName,
            List<Integer> indicatorIndex,
            List<FunctionDto> functionList,
            List<Token> tokenList,
            Map<String, Boolean> outPutParameterNameList);
}
