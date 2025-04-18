package com.calculation.formulacalculation.interfaces;


import com.calculation.formulacalculation.dto.FormulaDto;
import com.calculation.formulacalculation.dto.OutputParameterDto;
import com.calculation.formulacalculation.dto.OutputParameterIdAndFormula;

import java.util.Map;
import java.util.stream.Collector;

public interface OutputParameterService {
    Collector<OutputParameterIdAndFormula, ?, Map<OutputParameterDto, FormulaDto>> collectOutputInformationToMap();
}
