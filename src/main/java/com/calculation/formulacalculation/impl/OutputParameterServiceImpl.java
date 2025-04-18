package com.calculation.formulacalculation.impl;


import com.calculation.formulacalculation.dto.FormulaDto;
import com.calculation.formulacalculation.dto.OutputParameterDto;
import com.calculation.formulacalculation.dto.OutputParameterIdAndFormula;
import com.calculation.formulacalculation.interfaces.OutputParameterService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class OutputParameterServiceImpl implements OutputParameterService {

    @Override
    public Collector<OutputParameterIdAndFormula, ?, Map<OutputParameterDto, FormulaDto>> collectOutputInformationToMap() {
        return Collectors.toMap(
                outputParameterIdAndFormula ->
                        new OutputParameterDto(
                                outputParameterIdAndFormula.getOutputParameterCode()
                        )
                , outputParameterIdAndFormula ->
                        new FormulaDto(
                                outputParameterIdAndFormula.getFormula()
                        )
        );
    }
}
