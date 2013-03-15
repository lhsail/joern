package tests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import main.TokenSubStream;
import main.ShallowParser.ShallowParser;
import main.codeitems.CodeItem;
import main.codeitems.Name;
import main.codeitems.declarations.ClassDef;
import main.codeitems.declarations.IdentifierDecl;
import main.codeitems.function.FunctionDef;
import main.codeitems.function.Parameter;
import main.codeitems.function.ParameterType;
import main.processors.TestProcessor;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;

import antlr.CodeSensorLexer;

public class BuilderTest {

	@Test
	public void testNestedStructs()
	{
		String input = "struct x{ struct y { struct z{}; }; };";
		List<CodeItem> codeItems = parseInput(input);
		assertTrue(codeItems.size() == 3);
	}
	
	@Test
	public void testStructName()
	{
		String input = "struct foo{};";
		List<CodeItem> codeItems = parseInput(input);
		ClassDef codeItem = (ClassDef) codeItems.get(0);
		assertTrue(codeItem.name.getCodeStr().equals("foo"));
	}
	
	@Test
	public void testDecl()
	{
		String input = "int foo;";
		List<CodeItem> codeItems = parseInput(input);
		IdentifierDecl codeItem = (IdentifierDecl) codeItems.get(0);
		assertTrue(codeItem.name.getCodeStr().equals("foo"));
	}

	@Test
	public void testDeclListAfterClass()
	{
		String input = "class foo{int x;} y;";
		List<CodeItem> codeItems = parseInput(input);
		IdentifierDecl codeItem = (IdentifierDecl) codeItems.get(codeItems.size() - 1);
		System.out.println(codeItem.name.getCodeStr());
		assertTrue(codeItem.name.getCodeStr().equals("y"));
	}
	
	@Test
	public void testClassDefBeforeContent()
	{
		String input = "class foo{int x;}";
		List<CodeItem> codeItems = parseInput(input);
		
		ClassDef classCodeItem = (ClassDef) codeItems.get(0);
		IdentifierDecl identifierCodeItem = (IdentifierDecl) codeItems.get(1);
		
		assertTrue(classCodeItem.name.getCodeStr().equals("foo"));
		assertTrue(identifierCodeItem.name.getCodeStr().equals("x"));
	}
	
	@Test
	public void testFuncName()
	{
		String input = "void foo(){};";
		List<CodeItem> codeItems = parseInput(input);
		FunctionDef codeItem = (FunctionDef) codeItems.get(0);
		assertTrue(codeItem.name.getCodeStr().equals("foo"));
	}
	
	@Test
	public void testFuncSignature()
	{
		String input = "void foo(int x, char **ptr){};";
		List<CodeItem> codeItems = parseInput(input);
		FunctionDef codeItem = (FunctionDef) codeItems.get(0);
		System.out.println(codeItem.getCodeStr());
		assertTrue(codeItem.getCodeStr().equals("foo (int x , char * * ptr)"));
	}
	
	
	@Test
	public void testParamListGetCodeStr()
	{
		String input = "int foo(char *myParam, myType x){}";
		List<CodeItem> codeItems = parseInput(input);
		FunctionDef codeItem = (FunctionDef) codeItems.get(0);
		String codeStr = codeItem.parameterList.getCodeStr();
		System.out.println(codeStr);
		assertTrue(codeStr.equals("char * myParam , myType x"));
	}
	
	@Test
	public void testParamGetCodeStr()
	{
		String input = "int foo(char *myParam, myType x){}";
		List<CodeItem> codeItems = parseInput(input);
		FunctionDef codeItem = (FunctionDef) codeItems.get(0);
		Parameter parameter = codeItem.parameterList.parameters.get(0);
		String codeStr = parameter.getCodeStr();
		System.out.println(codeStr);
		assertTrue(codeStr.equals("char * myParam"));
	}
		
	@Test
	public void testParamName()
	{
		String input = "int foo(myType myParam){}";
		List<CodeItem> codeItems = parseInput(input);
		FunctionDef codeItem = (FunctionDef) codeItems.get(0);
		Name name = codeItem.parameterList.parameters.get(0).name;
		assertTrue(name.getCodeStr().equals("myParam"));
	}
	
	@Test
	public void testParamType()
	{
		String input = "int foo(char *myParam){}";
		List<CodeItem> codeItems = parseInput(input);
		FunctionDef codeItem = (FunctionDef) codeItems.get(0);
		ParameterType type = codeItem.parameterList.parameters.get(0).type;
		System.out.println(type.getCodeStr());
		assertTrue(type.getCodeStr().equals("char *"));
	}
	
	@Test
	public void testFunctionPtrParam()
	{
		String input = "int foo(void (*ptr)(char *)){}";
		List<CodeItem> codeItems = parseInput(input);
		FunctionDef codeItem = (FunctionDef) codeItems.get(0);
		System.out.println(codeItem.getCodeStr());
		assertTrue(codeItem.name.getCodeStr().equals("foo"));
	}

	private List<CodeItem> parseInput(String input)
	{
		ShallowParser parser = new ShallowParser();		
		parser.setProcessor(new TestProcessor());
		
		ANTLRInputStream inputStream = new ANTLRInputStream(input);
		CodeSensorLexer lex = new CodeSensorLexer(inputStream);
		TokenSubStream tokens = new TokenSubStream(lex);
		
		parser.parseAndWalk(tokens);
		TestProcessor processor = (TestProcessor) parser.listener.getProcessor();
		return processor.codeItems;
	}

}
