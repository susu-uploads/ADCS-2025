package org.alexcawl.kotea

import kotlinx.coroutines.flow.Flow

/**
 * ```
 * Flow<Command> ╭─────────────────────╮ Flow<Event>
 *       ────────> CommandsFlowHandler ├───────>
 *               ╰────────Λ───┬────────╯
 *                        ╵   V
 *                        Model
 * ```
 */
public fun interface CommandsFlowHandler<in Command : Any, out Event : Any> {

    /**
     * Flow can be collected from an any thread, including main.
     * So you should always offload any expensive operations to a background threads.
     */
    public fun handle(commands: Flow<Command>): Flow<Event>
}
