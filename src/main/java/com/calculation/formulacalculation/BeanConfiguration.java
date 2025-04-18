package com.calculation.formulacalculation;

import com.calculation.formulacalculation.dto.Token;
import com.calculation.formulacalculation.impl.StatementGeneratorImpl;
import com.calculation.formulacalculation.interfaces.StatementGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfiguration {
    @Bean
    String test(){
        StatementGenerator statementGenerator = new StatementGeneratorImpl();
        List<Token> tokenList = statementGenerator
                .parsingToListOfTokenList("person.child.age = 12;\n" +
                        "let x = getTest(x = 'value');");
        return "test";
    }
}
