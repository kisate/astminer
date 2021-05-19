package astminer.parse.antlr

import org.antlr.v4.runtime.Parser

import java.time.Instant

import org.antlr.v4.runtime.atn.ATNConfig

import org.antlr.v4.runtime.atn.ATNConfigSet

import org.antlr.v4.runtime.atn.PredictionContextCache

import org.antlr.v4.runtime.atn.ATN

import org.antlr.v4.runtime.atn.ParserATNSimulator
import org.antlr.v4.runtime.dfa.DFA
import java.lang.Exception
import java.time.Duration


class ParseTimeoutException(message: String) : Exception(message)


class ParserATNSimulatorWithTimeout(
    parser: Parser?, atn: ATN?, decisionToDFA: Array<DFA?>?,
    sharedContextCache: PredictionContextCache?,
    private val timeout: Long
) :
    ParserATNSimulator(parser, atn, decisionToDFA, sharedContextCache) {
    private val start = Instant.now()
    override fun closure(
        config: ATNConfig,
        configs: ATNConfigSet,
        closureBusy: Set<ATNConfig>,
        collectPredicates: Boolean,
        fullCtx: Boolean,
        treatEofAsEpsilon: Boolean
    ) {
        val timeElapsed: Duration = Duration.between(start, Instant.now())
        if (timeElapsed.toMillis() >= timeout) {
            throw ParseTimeoutException("Parsing took more than $timeout millis.")
        }
        super.closure(config, configs, closureBusy, collectPredicates, fullCtx, treatEofAsEpsilon)
    }
}