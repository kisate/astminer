package astminer.storage.ast

import astminer.cli.LabeledResult
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.storage.*
import astminer.storage.Storage
import java.io.File
import java.io.PrintWriter

/**
 * Stores multiple ASTs by their roots and saves them in .csv format.
 * Output consists of 3 .csv files: with node types, with tokens and with ASTs.
 */
class CsvAstStorage(override val outputDirectoryPath: String) : Storage {

    private val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val nodeTypesMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()

    private val astsOutputStream: PrintWriter

    init {
        File(outputDirectoryPath).mkdirs()
        val astsFile = File("$outputDirectoryPath/asts.csv")
        astsFile.createNewFile()
        astsOutputStream = PrintWriter(astsFile)
        astsOutputStream.write("id,ast\n")
    }

    override fun store(labeledResult: LabeledResult<out Node>) {
        for (node in labeledResult.root.preOrder()) {
            tokensMap.record(node.getToken())
            nodeTypesMap.record(node.getTypeLabel())
        }
        dumpAst(labeledResult.root, labeledResult.label)
    }

    override fun close() {
        dumpTokenStorage(File("$outputDirectoryPath/tokens.csv"))
        dumpNodeTypesStorage(File("$outputDirectoryPath/node_types.csv"))

        astsOutputStream.close()
    }

    private fun dumpTokenStorage(file: File) {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, file)
    }

    private fun dumpNodeTypesStorage(file: File) {
        dumpIdStorageToCsv(nodeTypesMap, "node_type", nodeTypeToCsvString, file)
    }

    private fun dumpAst(root: Node, id: String) {
        astsOutputStream.write("$id,${astString(root)}\n")
    }

    internal fun astString(node: Node): String {
        return "${tokensMap.getId(node.getToken())} ${nodeTypesMap.getId(node.getTypeLabel())}{${
            node.getChildren().joinToString(separator = "", transform = ::astString)
        }}"
    }
}
