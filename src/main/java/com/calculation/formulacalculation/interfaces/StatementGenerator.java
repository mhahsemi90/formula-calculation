package com.calculation.formulacalculation.interfaces;

import com.calculation.formulacalculation.dto.Token;

import java.util.ArrayList;
import java.util.List;

public interface StatementGenerator {
    List<Token> parsingToListOfTokenList(String script);
    default List<Character> getAllSeparatorCharacter() {
        List<Character> characterList = new ArrayList<>();
        characterList.add('!');
        characterList.add('\n');
        characterList.add('&');
        characterList.add('|');
        characterList.add('(');
        characterList.add(')');
        characterList.add('+');
        characterList.add('-');
        characterList.add('*');
        characterList.add('/');
        characterList.add(',');
        characterList.add(':');
        characterList.add(';');
        characterList.add('=');
        characterList.add('>');
        characterList.add('<');
        characterList.add('?');
        characterList.add(' ');
        characterList.add('{');
        characterList.add('}');
        characterList.add('[');
        characterList.add(']');
        return characterList;
    }
    default List<Character> getNoChar() {
        List<Character> characterList = new ArrayList<>();
        characterList.add('\n');
        characterList.add('\t');
        return characterList;
    }
    default List<String> getHaveSequenceString() {
        List<String> haveSequenceStringList = new ArrayList<>();
        haveSequenceStringList.add("+");
        haveSequenceStringList.add("-");
        haveSequenceStringList.add("*");
        haveSequenceStringList.add("/");
        haveSequenceStringList.add("%");
        haveSequenceStringList.add("=");
        haveSequenceStringList.add("!");
        haveSequenceStringList.add("&");
        haveSequenceStringList.add("|");
        haveSequenceStringList.add(">");
        haveSequenceStringList.add("<");
        return haveSequenceStringList;
    }
    default List<String> getAllOperatorAndNotOperator() {
        List<String> operatorList = new ArrayList<>();
        operatorList.add("\n");
        operatorList.add("(");
        operatorList.add(")");
        operatorList.add(",");
        operatorList.add(".");
        operatorList.add(":");
        operatorList.add(";");
        operatorList.add("{");
        operatorList.add("}");
        operatorList.add("[");
        operatorList.add("]");
        operatorList.addAll(getArithmeticOperatorList());
        operatorList.addAll(getAssignmentOperatorList());
        operatorList.addAll(getComparisonOperatorList());
        operatorList.addAll(getLogicalOperatorList());
        operatorList.addAll(getBitwiseOperatorList());
        return operatorList;
    }
    default List<String> getArithmeticOperatorList() {
        List<String> operatorList = new ArrayList<>();
        operatorList.add("+");
        operatorList.add("-");
        operatorList.add("*");
        operatorList.add("/");
        operatorList.add("%");
        operatorList.add("++");
        operatorList.add("--");
        operatorList.add("**");
        return operatorList;
    }

    default List<String> getAssignmentOperatorList() {
        List<String> operatorList = new ArrayList<>();
        operatorList.add("=");
        operatorList.add("+=");
        operatorList.add("-=");
        operatorList.add("*=");
        operatorList.add("/=");
        operatorList.add("%=");
        operatorList.add("**=");
        operatorList.add("<<=");
        operatorList.add(">>=");
        operatorList.add(">>>=");
        operatorList.add("&=");
        operatorList.add("^=");
        operatorList.add("|=");
        return operatorList;
    }

    default List<String> getComparisonOperatorList() {
        List<String> operatorList = new ArrayList<>();
        operatorList.add("==");
        operatorList.add("===");
        operatorList.add("!=");
        operatorList.add("!==");
        operatorList.add(">");
        operatorList.add("<");
        operatorList.add(">=");
        operatorList.add("<=");
        operatorList.add("?");
        return operatorList;
    }

    default List<String> getLogicalOperatorList() {
        List<String> operatorList = new ArrayList<>();
        operatorList.add("!");
        operatorList.add("&&");
        operatorList.add("||");

        return operatorList;
    }

    default List<String> getBitwiseOperatorList() {
        List<String> operatorList = new ArrayList<>();
        operatorList.add("&");
        operatorList.add("|");
        operatorList.add("~");
        operatorList.add("^");
        operatorList.add(">");
        operatorList.add("<<");
        operatorList.add(">>");
        operatorList.add(">>>");
        return operatorList;
    }
    default List<String> getAllKeyword() {
        List<String> keyWordList = new ArrayList<>();
        keyWordList.add("arguments");
        keyWordList.add("await");
        keyWordList.add("break");
        keyWordList.add("case");
        keyWordList.add("catch");
        keyWordList.add("class");
        keyWordList.add("const");
        keyWordList.add("continue");
        keyWordList.add("debugger");
        keyWordList.add("default");
        keyWordList.add("delete");
        keyWordList.add("do");
        keyWordList.add("else");
        keyWordList.add("enum");
        keyWordList.add("eval");
        keyWordList.add("export");
        keyWordList.add("extends");
        keyWordList.add("finally");
        keyWordList.add("for");
        keyWordList.add("function");
        keyWordList.add("if");
        keyWordList.add("implements");
        keyWordList.add("import");
        keyWordList.add("in");
        keyWordList.add("instanceof");
        keyWordList.add("interface");
        keyWordList.add("let");
        keyWordList.add("new");
        keyWordList.add("package");
        keyWordList.add("private");
        keyWordList.add("protected");
        keyWordList.add("public");
        keyWordList.add("return");
        keyWordList.add("static");
        keyWordList.add("super");
        keyWordList.add("switch");
        keyWordList.add("this");
        keyWordList.add("throw");
        keyWordList.add("try");
        keyWordList.add("typeof");
        keyWordList.add("var");
        keyWordList.add("void");
        keyWordList.add("while");
        keyWordList.add("with");
        keyWordList.add("yield");
        keyWordList.addAll(getAllLiteralKeyword());
        return keyWordList;
    }
    default List<String> getAllLiteralKeyword() {
        List<String> keyWordList = new ArrayList<>();
        keyWordList.add("false");
        keyWordList.add("true");
        keyWordList.add("null");
        keyWordList.add("undefined");
        return keyWordList;
    }
}
