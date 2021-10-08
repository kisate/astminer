package astminer.parse.antlr.kotlin

import me.vovak.antlr.parser.KotlinParser
import org.antlr.v4.runtime.TokenStream
import astminer.parse.antlr.ParserATNSimulatorWithTimeout




class KotlinParserWithTimeout(input: TokenStream?, timeout: Long) : KotlinParser(input) {
    init {
        _interp = ParserATNSimulatorWithTimeout(this, _ATN, _decisionToDFA, _sharedContextCache, timeout)
    }
}