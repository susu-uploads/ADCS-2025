package org.alexcawl.kotea

internal class CommandsFlowHandlerException(
    handlerClass: Class<out CommandsFlowHandler<*, *>>,
    cause: Throwable
) : RuntimeException("Exception in ${handlerClass.canonicalName}", cause)
