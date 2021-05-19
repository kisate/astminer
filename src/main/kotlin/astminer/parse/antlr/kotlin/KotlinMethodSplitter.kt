package astminer.parse.antlr.kotlin

import astminer.common.model.*
import astminer.common.preOrder
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.decompressTypeLabel

class KotlinMethodSplitter : TreeMethodSplitter<AntlrNode> {
    companion object {
        private const val METHOD_NODE = "classMemberDeclaration/functionDeclaration"
        private const val METHOD_RETURN_TYPE_NODE = "type/Identifier"
        private const val METHOD_NAME_NODE = "simpleIdentifier/Identifier"

        private const val CLASS_DECLARATION_NODE = "declaration/classDeclaration"
        private const val CLASS_NAME_NODE = "simpleIdentifier/Identifier"

        private const val METHOD_PARAMETER_NODE = "functionValueParameters"
        private const val METHOD_SINGLE_PARAMETER_NODE = "functionValueParameter/parameter"
        private const val PARAMETER_RETURN_TYPE_NODE = "type/Identifier"
        private const val PARAMETER_NAME_NODE = "simpleIdentifier/Identifier"
    }

    override fun splitIntoMethods(root: AntlrNode): Collection<MethodInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            decompressTypeLabel(it.getTypeLabel()).last() == METHOD_NODE
        }
        return methodRoots.map { collectMethodInfo(it as AntlrNode) }
    }

    private fun collectMethodInfo(methodNode: AntlrNode): MethodInfo<AntlrNode> {
        val methodName = methodNode.getChildOfType(METHOD_NAME_NODE)
        val methodReturnTypeNode = methodNode.getChildOfType(METHOD_RETURN_TYPE_NODE)
        methodReturnTypeNode?.setToken(collectParameterToken(methodReturnTypeNode))

        val classRoot = getEnclosingClass(methodNode)
        val className = classRoot?.getChildOfType(CLASS_NAME_NODE)

        val parametersRoot = methodNode.getChildOfType(METHOD_PARAMETER_NODE)

        val parametersList = when {
            parametersRoot != null -> getListOfParameters(parametersRoot)
            else -> emptyList()
        }

        return MethodInfo(
            MethodNode(methodNode, methodReturnTypeNode, methodName),
            ElementNode(classRoot, className),
            parametersList
        )
    }

    private fun getEnclosingClass(node: AntlrNode): AntlrNode? {
        if (decompressTypeLabel(node.getTypeLabel()).last() == CLASS_DECLARATION_NODE) {
            return node
        }
        val parentNode = node.getParent() as? AntlrNode
        if (parentNode != null) {
            return getEnclosingClass(parentNode)
        }
        return null
    }

    private fun getListOfParameters(parametersRoot: AntlrNode): List<ParameterNode<AntlrNode>> {
        if (decompressTypeLabel(parametersRoot.getTypeLabel()).last() == METHOD_SINGLE_PARAMETER_NODE) {
            return listOf(getParameterInfoFromNode(parametersRoot))
        }
        return parametersRoot.getChildren().filter {
            METHOD_SINGLE_PARAMETER_NODE == decompressTypeLabel(it.getTypeLabel()).first()
        }.map {
            getParameterInfoFromNode(it)
        }
    }

    private fun getParameterInfoFromNode(parameterRoot: AntlrNode): ParameterNode<AntlrNode> {
        val returnTypeNode = parameterRoot.getChildOfType(PARAMETER_RETURN_TYPE_NODE)
        returnTypeNode?.setToken(collectParameterToken(returnTypeNode))
        return ParameterNode(
            parameterRoot,
            returnTypeNode,
            parameterRoot.getChildOfType(PARAMETER_NAME_NODE)
        )
    }

    private fun collectParameterToken(parameterRoot: AntlrNode): String {
        if (parameterRoot.isLeaf()) {
            return parameterRoot.getToken()
        }
        return parameterRoot.getChildren().joinToString(separator = "") { child ->
            collectParameterToken(child)
        }
    }
}