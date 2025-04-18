package com.calculation.formulacalculation.interfaces;


import com.calculation.formulacalculation.dto.InputParameterAndElementValue;
import com.calculation.formulacalculation.dto.InputParameterDto;

import java.util.Map;
import java.util.stream.Collector;

public interface InputParameterService {

    Collector<InputParameterAndElementValue, ?, Map<InputParameterDto, String>> collectInputInformationToMap();
}
