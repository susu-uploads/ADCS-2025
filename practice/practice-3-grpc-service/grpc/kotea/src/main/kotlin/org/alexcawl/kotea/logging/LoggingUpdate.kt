package org.alexcawl.kotea.logging

import org.alexcawl.kotea.Next
import org.alexcawl.kotea.Update

internal class LoggingUpdate<State : Any, Event : Any, Command : Any, News : Any>(
    private val delegate: Update<State, Event, Command, News>,
    private val logger: Logger<State, Event, Command, News>,
) : Update<State, Event, Command, News> {

    override fun update(state: State, event: Event): Next<State, Command, News> {
        logger.onEvent(event = event)
        val next: Next<State, Command, News> = delegate.update(state = state, event = event)
        logger.onNext(next = next)
        return next
    }
}
