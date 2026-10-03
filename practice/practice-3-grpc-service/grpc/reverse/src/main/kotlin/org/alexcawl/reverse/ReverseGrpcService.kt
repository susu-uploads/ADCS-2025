package org.alexcawl.reverse

import org.alexcawl.common.logger

internal class ReverseGrpcService(
    private val reverseTextService: ReverseTextService = ReverseTextService(),
) : ReverseServiceGrpcKt.ReverseServiceCoroutineImplBase() {

    override suspend fun reverseString(request: ReverseRequest): ReverseResponse {
        val reversedText: String = reverseTextService.reverse(request.text)
        logger.info("Processed request: '${request.text}' -> '$reversedText'")
        return ReverseResponse.newBuilder()
            .setReversedText(reversedText)
            .build()
    }
}
