package com.calculation.formulacalculation.impl;


import com.calculation.formulacalculation.dto.InputParameterAndElementValue;
import com.calculation.formulacalculation.dto.InputParameterDto;
import com.calculation.formulacalculation.interfaces.InputParameterService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class InputParameterServiceImpl implements InputParameterService {

    @Override
    public Collector<InputParameterAndElementValue, ?, Map<InputParameterDto, String>> collectInputInformationToMap() {
        return Collectors.toMap(
                inputParameterAndElementValue ->
                        new InputParameterDto(
                                inputParameterAndElementValue.getCode()
                        )
                , InputParameterAndElementValue::getValue
        );
    }
}
