package astminer.storage.path

import astminer.common.model.PathContextId
import astminer.storage.TokenProcessor

class Code2VecPathStorage(
    outputDirectoryPath: String,
    config: PathBasedStorageConfig,
    tokenProcessor: TokenProcessor = TokenProcessor.Normalize
) :
    PathBasedStorage(outputDirectoryPath, config, tokenProcessor) {

    override fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String {
        val joinedPathContexts = pathContextIds.joinToString(" ") { pathContextId ->
            "${pathContextId.startTokenId},${pathContextId.pathId},${pathContextId.endTokenId}"
        }
        return "$label $joinedPathContexts"
    }
}
