package astminer.parse.antlr.kotlin

import astminer.common.model.Parser
import astminer.parse.ParsingException
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.convertAntlrTree
import astminer.parse.antlr.shortenNodes
import me.vovak.antlr.parser.KotlinLexer
import me.vovak.antlr.parser.KotlinParser
import org.antlr.v4.runtime.*
import java.io.InputStream

class KotlinParser:Parser<AntlrNode> {
    override fun parseInputStream(content: InputStream): AntlrNode {
        try {
            val lexer = KotlinLexer(CharStreams.fromStream(content))
            lexer.removeErrorListeners()
            val tokens = CommonTokenStream(lexer)
            val parser = KotlinParserWithTimeout(tokens, 10000)
            parser.removeErrorListeners()
            val context = parser.kotlinFile()
            return shortenNodes(convertAntlrTree(context, KotlinParser.ruleNames, KotlinParser.VOCABULARY))
        } catch (e: Exception) {
            throw ParsingException("ANTLR", "Kotlin", e.message)
        }
    }
}