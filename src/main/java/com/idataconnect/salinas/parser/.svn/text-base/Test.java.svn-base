package com.idataconnect.salinas.parser;

import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.function.CallStack;
import java.util.LinkedList;
import java.util.List;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 */
public class Test {

    public static void main(String[] args) {
        ScriptContext context = null;
        try {
            final List<String> codes = new LinkedList<String>();
//            codes.add("_t = ;\n1;;5 * (1 * (1 + 1)) \n0xff - 2\n-----5.5 \n\n -500;; 5.0000\nuse \"this is a test.dbf\";;use \"this is a test\";;use \"test.dbf\";; use;;");
//            codes.add("t or /*bl\n/**ah*/1 /*+*/+ /**/3 .and. 5");
//            codes.add("3.and.5 + .5 and true or.f.;;");
//            codes.add("use \"test\" in select() alias test");
//            codes.add("\"test\"$\"123test123\"$\"123\"*1.and..not.23.333.and..t.\n");
//            codes.add("-5 + -((3)) + -t\n5 + 5 + select();; \tt - 5;;_t_:=0xff");
//            codes.add("a + test_function(\"test\", len('blah'),-2);;;;\n");
//            codes.add("?? \"test\"");
//            codes.add("? \"test\";;?'test'");
//            codes.add("{1/1/2001} + date()");
//            codes.add("set directory to \"c:\\test\\\"");
//            codes.add("set century off");
//            codes.add("? set(\"century\")");
//            codes.add("y + set(\"directory\") + '.dbf'");
//            codes.add("false !== null\n\"test\" === \"test\"");
//            codes.add("function test(test:number);;t = \"test\"\na = 3 - 2;;endfunc");
//            codes.add("function _t(test = 'a')\n? a\nendfunc");
//            codes.add("? {^2001-01-01}");
//            codes.add("? set(\"directory\")");
//            codes.add("_a = _b = _c = _d = 5");
//            codes.add("for x = 1 to 10;;a = x;;? a;;next");
//            codes.add("a = 10\ndo while a > 0\n? a = a - 1\nenddo");
//            codes.add("do case;;case x;;? 'x';;otherwise;;? 'other';;endcase");
//            codes.add("a:number = 1");
//            codes.add("a = function();;? 'test';;endfunc;; ? a()");
//            codes.add("public static a = 1;;public a = 1\npublic static a = 1");
//            codes.add("use//test comment\nstatic a&&=2\na=1");

//            codes.add("10 / 0"); // Divide by zero error

//            codes.add("1 + 1\n1+1-1\n-1\n\"a \"+1-1"); // string concat using + and -
//            codes.add("-1-(-(-1))-1+1+1"); // parentheses and unary minus
//            codes.add("6.25 * 3 / 2 + 4 * 6"); // floating point and basic math operator precedence
//            codes.add("-5.25 * 3"); // unary minus and multiplication
//            codes.add("a=1\n-5 + -((3)) + a"); // variable support
//            codes.add("a=1\na\na * 2 + 1"); // variable support
//            codes.add("x = 5;;y = 8;;x * (y + 2)"); // variable, arithmetic and operator precedence
//            codes.add("x = 5;;y:string = 8;;x * (y + 2)"); // strong types causing type conversion
//            codes.add("1+.t.+\"1\""); // conversion between true and one
//            codes.add("a = (1 = 2 <> 5)"); // assignment statement, equality checks (multiple meanings of =)
//            codes.add("? 3 == \"3\""); // is equal to
//            codes.add("\"a\"$\"ab\""); // contains
//            codes.add("\"c\"$\"a\"$\"ab\""); // contains with multiple search terms
//            codes.add("true and (not true or false)"); // boolean logic
//            codes.add("a = false;; ? a;; a = a + 1;; a:boolean;; ? a;; a = -a+1;;?a"); // late application of strong type to a variable
//            codes.add("if 1 == 1;;? '1 does equal 1';;elseif 1 == 2;;? '1 does equal 2';;elseif 3 = 4;;? 'blah';;else;;? 'none are true';;endif"); // if statement
//            codes.add("y = 1;;a = 20;;for x = a to 1 step -1;;if y = 1;;x = x + 1;;y = 0;;endif;;? x;;next"); // for loop
//            codes.add("do case;;case 1 = 1;;? 'first';; case 2 = 2;; ? 'second';;otherwise;;? 'third';;endcase"); // case statement
//            codes.add("a = 1;;do while a < 10;;a = a + 1;; ? a;;enddo"); // while loop
//            codes.add("a = 1;; return a;; 10;;"); // return statement
//            codes.add("a = function b();;? 'here';;endfunc;;a();;? b;;"); // named user defined function as a closure
//            codes.add("a = function c(b = 10);;? b;; return b;;endfunc;; a(1);;c(2);;a();;c()"); // return from function, default values
//            codes.add("a = 1\n c();;function c();;? 'here';;endfunc"); // access function before it is declared
//            codes.add("? a := 0xff;;? a := 0b10_00"); // hex and binary literals
//            codes.add("if 1=1;;public a:string = 'test';;endif;;? a"); // variable with public scope
//            codes.add("? upper('test')"); // builtin function
//            codes.add("left('test', 3)"); // builtin function
//            codes.add("? RIGHT('test', 2);;? right('test', 5)"); // builtin function
//            codes.add("? UPPER(SUBSTR('test', 2, 1))"); // builtin functions
//            codes.add("? asc('a')"); // builtin function
//            codes.add("at('st', 'test') + .5 * 2");
//            codes.add("at('st', 'testing testing 1 2 3', 2)");
//            codes.add("at('st', 'ststst', 2)");
//            codes.add("rat('st', 'ststst')");
//            codes.add("rat('st', 'ststst', 2)");
//            codes.add("a = function();;return 2;;endfunc;;-a()"); // minus sign before function call
//            codes.add("function a;;return function(num);;return num + 1;;endfunc;;endfunc;;a()(2)"); // calling a return value from a function, as a function
//            codes.add("a[1][2] = 5;; a[1][2]"); // implicitly create a two dimensional array
//            codes.add("a[1, 2] = 5;; a[1, 2]"); // implicitly create a two dimensional array using legacy syntax
//            codes.add("a['test'] = 'testing';; a[2] = 'blah';; ? a['test'];; ? a[2]"); // different types stored in an array
//            codes.add("a = {1, {2 / 3}, 3, 'string', {'nested array :)'}, {1/1/01}, {^2001-01-01}}"); // array literals, differentiated from dates
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("salinas");
            System.out.println(scriptEngine.getFactory().getEngineName() + " " + scriptEngine.getFactory().getLanguageVersion());
            if (scriptEngine == null) {
                System.err.println("Can't find salinas script engine");
            }

            context = scriptEngine.getContext();

            //scriptEngine.getContext().setWriter(new OutputStreamWriter(System.out));

            Object value;
            for (String code : codes) {
                System.out.println("-------------\n" + code + "\n-------------");
                value = (scriptEngine.eval(code));
                System.out.println("\nResult: " + (value == null ? "<null>" : 
                        value));
                System.out.println();
            }
        } catch (ScriptException ex) {
            context.getErrorWriter();
            System.err.println(ex.getMessage());
            if (ex.getLineNumber() > 0) {
                System.err.println("\tat " + ex.getFileName() + ":" + ex.getLineNumber());
            } else {
                System.err.println("\tat <unknown>");
            }
            if (context != null) {
                final CallStack callStack = ((CallStack) context.getAttribute("salinasCallStack"));
                if (callStack != null) {
                    callStack.printStackTrace();
                }
            }
//            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
