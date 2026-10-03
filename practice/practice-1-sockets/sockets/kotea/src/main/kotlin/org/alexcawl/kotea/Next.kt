package org.alexcawl.kotea

/** This class represents the result of calling an [Update] function */
public class Next<out State : Any, out Command : Any, out News : Any>(
    /** New state to use (if set) */
    public val state: State? = null,
    /** Commands which can be handled in a [CommandsFlowHandler] to trigger some side effects */
    public val commands: List<Command> = emptyList(),
    /** One-off commands for UI (e.g. `ShowErrorDialog`) */
    public val news: List<News> = emptyList(),
)
