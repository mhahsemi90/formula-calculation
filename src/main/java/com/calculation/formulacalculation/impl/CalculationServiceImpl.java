package com.calculation.formulacalculation.impl;

import com.calculation.formulacalculation.dto.*;
import com.calculation.formulacalculation.interfaces.CalculationService;
import com.calculation.formulacalculation.interfaces.InputParameterService;
import com.calculation.formulacalculation.interfaces.OutputParameterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CalculationServiceImpl implements CalculationService {
    private final OutputParameterService outputParameterService;
    private final InputParameterService inputParameterService;

    public CalculationServiceImpl(OutputParameterService outputParameterService, InputParameterService inputParameterService) {
        this.outputParameterService = outputParameterService;
        this.inputParameterService = inputParameterService;
    }

    @Override
    public List<CalculatedOutputParameterForElement> calculate(
            List<InputParameterAndElementValue> inputParameterAndElementValueList,
            List<OutputParameterIdAndFormula> outputParameterIdAndFormulaList,
            Timestamp actionDate) {
        List<InputAndOutputParameterElement> inputAndOutputParameterElementList = getCalculationInformation(
                inputParameterAndElementValueList,
                outputParameterIdAndFormulaList
        );
        return inputAndOutputParameterElementList
                .stream()
                .parallel()
                .map(this::calculateForEachElementByOwnParameter)
                .toList();
    }


    private CalculatedOutputParameterForElement calculateForEachElementByOwnParameter(InputAndOutputParameterElement inputAndOutputParameterElement) {
        CalculatedOutputParameterForElement calculatedOutputParameterForElement = new CalculatedOutputParameterForElement();
        StringBuilder formulaBuilder = new StringBuilder();
        calculatedOutputParameterForElement
                .setElementId(inputAndOutputParameterElement.getElementId());
        inputAndOutputParameterElement
                .getInputParamMapList()
                .forEach(getBuilderToAddInputToFormula(formulaBuilder));
        inputAndOutputParameterElement
                .getOutputParamMapList()
                .forEach(getBuilderToAddOutputToFormula(formulaBuilder));
        formulaBuilder
                .append("var _doCalculate = function(){\n")
                .append("var _values = new Map();\n");
        inputAndOutputParameterElement
                .getOutputParamMapList()
                .keySet()
                .forEach(getBuilderToAddOutputToCalculation(formulaBuilder));
        formulaBuilder
                .append("return JSON.stringify(Object.fromEntries(_values));\n")
                .append("};\n");
        calculatedOutputParameterForElement.setOutputParamValueMapList(doCalculationFormulaScript(
                formulaBuilder.toString(),
                inputAndOutputParameterElement.getOutputParamMapList()
        ));
        calculatedOutputParameterForElement.setOutputParamFormulaMapList(new LinkedHashMap<>());
        inputAndOutputParameterElement.getOutputParamMapList().forEach((outputParameter, formula) ->
                calculatedOutputParameterForElement.getOutputParamFormulaMapList()
                        .put(outputParameter.getId(), formula)
        );
        return calculatedOutputParameterForElement;
    }

    private Map<OutputParameterDto, String> doCalculationFormulaScript(
            String script,
            Map<OutputParameterDto, FormulaDto> outputParameterDtoFormulaDtoMap
    ) {
        Map<Object, Object> outputParamMap = new LinkedHashMap<>();
        Map<OutputParameterDto, String> outputParameterDtoValueMap = new LinkedHashMap<>();
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("graal.js");
            Reader inputString = new StringReader(script);
            BufferedReader reader = new BufferedReader(inputString);
            engine.eval(reader);
            Invocable invocable = (Invocable) engine;
            Object result = invocable.invokeFunction("_doCalculate");
            ObjectMapper objectMapper = new ObjectMapper();
            outputParamMap.putAll(
                    new LinkedHashMap<>(
                            (Map<?, ?>) objectMapper.readValue(String.valueOf(result), Object.class)
                    )
            );
        } catch (ScriptException | NoSuchMethodException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        outputParameterDtoFormulaDtoMap.forEach((outputParameter, s) -> {
            String result = String.valueOf(outputParamMap.get("O_" + outputParameter.getId()));
            outputParameterDtoValueMap.put(outputParameter, result);
        });
        return outputParameterDtoValueMap;
    }

    private BiConsumer<InputParameterDto, String> getBuilderToAddInputToFormula(StringBuilder formulaBuilder) {
        return (inputParameterDto, s) ->
                formulaBuilder
                        .append("var I_")
                        .append(inputParameterDto.getId())
                        .append(" = ")
                        .append(setInputValue(inputParameterDto, s))
                        .append(";\n");
    }

    private BiConsumer<OutputParameterDto, FormulaDto> getBuilderToAddOutputToFormula(StringBuilder formulaBuilder) {
        return (outputParameter, formula) ->
                formulaBuilder
                        .append("var getO_")
                        .append(outputParameter.getId())
                        .append(" = function () {\n")
                        .append(formula.getFormula())
                        .append("};\n");
    }

    private Consumer<OutputParameterDto> getBuilderToAddOutputToCalculation(StringBuilder formulaBuilder) {
        return outputParameterDto ->
                formulaBuilder
                        .append("_values.set(\"O_")
                        .append(outputParameterDto.getId())
                        .append("\",getO_")
                        .append(outputParameterDto.getId())
                        .append("());\n");
    }

    private Object setInputValue(InputParameterDto inputParameterDto, String s) {
        return inputParameterDto.getDataType().equals("TEXT") ? "'" + s + "'" : s;
    }

    private List<InputAndOutputParameterElement> getCalculationInformation(
            List<InputParameterAndElementValue> inputParameterAndElementValueList,
            List<OutputParameterIdAndFormula> outputParameterIdAndFormulaList) {
        Set<Long> allElementId = inputParameterAndElementValueList
                .stream()
                .map(InputParameterAndElementValue::getElementId)
                .collect(Collectors.toSet());
        List<InputAndOutputParameterElement> inputAndOutputParameterElementList = new ArrayList<>();
        allElementId.forEach(aLong -> {
            InputAndOutputParameterElement inputAndOutputParameterElement = new InputAndOutputParameterElement();
            inputAndOutputParameterElement.setElementId(aLong);
            inputAndOutputParameterElement.setInputParamMapList(
                    inputParameterAndElementValueList
                            .stream()
                            .filter(getFilterForAllSameInput(aLong))
                            .collect(inputParameterService.collectInputInformationToMap())
            );
            inputAndOutputParameterElement.setOutputParamMapList(
                    outputParameterIdAndFormulaList
                            .stream()
                            .filter(getFilterForAllSameOutput(aLong))
                            .collect(outputParameterService.collectOutputInformationToMap())
            );
            inputAndOutputParameterElementList.add(inputAndOutputParameterElement);
        });
        return inputAndOutputParameterElementList;
    }

    private Predicate<InputParameterAndElementValue> getFilterForAllSameInput(Long elementId) {
        return inputParameterAndElementValue ->
                inputParameterAndElementValue.getElementId().equals(elementId);
    }

    private Predicate<OutputParameterIdAndFormula> getFilterForAllSameOutput(Long elementId) {
        return inputParameterAndElementValue ->
                inputParameterAndElementValue.getElementId().equals(elementId);
    }
}
