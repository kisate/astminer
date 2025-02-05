package astminer.examples

import astminer.cli.LabeledResult
import astminer.common.model.MethodInfo
import astminer.parse.gumtree.python.GumTreePythonMethodSplitter
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.python.GumTreePythonParser
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

private fun getCsvFriendlyMethodId(methodInfo: MethodInfo<GumTreeNode>): String {
    val className = methodInfo.enclosingElementName() ?: ""
    val methodName = methodInfo.name() ?: "unknown_method"
    val parameterTypes = methodInfo.methodParameters.joinToString("|") { it.name() ?: "_" }
    return "$className.$methodName($parameterTypes)"
}

fun allPythonMethods() {
    val inputDir = "src/test/resources/gumTreeMethodSplitter"

    val outputDir = "out_examples/allPythonMethods"
    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    File(inputDir).forFilesWithSuffix(".py") { file ->
        // parse file
        val fileNode = GumTreePythonParser().parseInputStream(file.inputStream())

        // extract method nodes
        val methodNodes = GumTreePythonMethodSplitter().splitIntoMethods(fileNode)

        methodNodes.forEach { methodInfo ->
            // Retrieve a method identifier
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodInfo)}"
            val labelingResult = LabeledResult(fileNode, entityId, file.path)
            // Retrieve paths from each method individually and store them
            storage.store(labelingResult)
        }
    }

    storage.close()
}
