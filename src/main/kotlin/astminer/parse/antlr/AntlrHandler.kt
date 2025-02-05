package astminer.parse.antlr

import astminer.common.model.ParseResult
import astminer.common.model.HandlerFactory
import astminer.common.model.LanguageHandler
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.javascript.JavaScriptMethodSplitter
import astminer.parse.antlr.javascript.JavaScriptParser
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.antlr.python.PythonParser
import java.io.File

object AntlrJavaHandlerFactory : HandlerFactory {
    override fun createHandler(file: File) = AntlrJavaHandler(file)

    class AntlrJavaHandler(file: File) : LanguageHandler<AntlrNode>() {
        override val parseResult: ParseResult<AntlrNode> = JavaParser().parseFile(file)
        override val splitter = JavaMethodSplitter()
    }
}

object AntlrPythonHandlerFactory : HandlerFactory {
    override fun createHandler(file: File) = AntlrPythonHandler(file)

    class AntlrPythonHandler(file: File) : LanguageHandler<AntlrNode>() {
        override val parseResult: ParseResult<AntlrNode> = PythonParser().parseFile(file)
        override val splitter = PythonMethodSplitter()
    }
}

object AntlrJavascriptHandlerFactory : HandlerFactory {
    override fun createHandler(file: File) = AntlrJavascriptHandler(file)

    class AntlrJavascriptHandler(file: File) : LanguageHandler<AntlrNode>() {
        override val parseResult: ParseResult<AntlrNode> = JavaScriptParser().parseFile(file)
        override val splitter = JavaScriptMethodSplitter()
    }
}