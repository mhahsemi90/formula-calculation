package com.calculation.formulacalculation.interfaces;


import com.calculation.formulacalculation.dto.CalculatedOutputParameterForElement;
import com.calculation.formulacalculation.dto.InputParameterAndElementValue;
import com.calculation.formulacalculation.dto.OutputParameterIdAndFormula;

import java.sql.Timestamp;
import java.util.List;

public interface CalculationService {

    List<CalculatedOutputParameterForElement> calculate(
            List<InputParameterAndElementValue> inputParameterAndElementValueList,
            List<OutputParameterIdAndFormula> outputParameterIdAndFormulaList,
            Timestamp actionDate);
}
