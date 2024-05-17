package com.mastrosql.app.utils

/*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

class KorosTimerTask internal constructor(
    name: String,
    private val delay: Duration = Duration.ZERO,
    private val repeat: Duration? = null,
    private val coroutineScope: CoroutineScope = GlobalScope,
    action: suspend () -> Unit
) {
    private val log = Logs.logger(this::class.java)
    private val keepRunning = AtomicBoolean(true)
    private var job: Job? = null
    private val tryAction = suspend {
        try {
            action()
        } catch (e: Throwable) {
            log.warn("$name timer action failed: $action")
        }
    }

    fun start() {
        job = coroutineScope.launch {
            delay(delay)
            if (repeat != null) {
                while (keepRunning.get()) {
                    tryAction()
                    delay(repeat)
                }
            } else {
                if (keepRunning.get()) {
                    tryAction()
                }
            }
        }
    }

    /**
     * Initiates an orderly shutdown, where if the timer task is currently running,
     * we will let it finish, but not run it again.
     * Invocation has no additional effect if already shut down.
     */
    fun shutdown() {
        keepRunning.set(false)
    }

    /**
     * Immediately stops the timer task, even if the job is currently running,
     * by cancelling the underlying Koros Job.
     */
    fun cancel() {
        shutdown()
        job?.cancel("cancel() called")
    }

    companion object {
        /**
         * Runs the given `action` after the given `delay`,
         * once the `action` completes, waits the `repeat` duration
         * and runs again, until `shutdown` is called.
         *
         * if action() throws an exception, it will be swallowed and a warning will be logged.
         */
        fun start(
            name: String,
            delay: Duration = Duration.ZERO,
            repeat: Duration? = null,
            coroutineScope: CoroutineScope = GlobalScope,
            action: suspend () -> Unit
        ): KorosTimerTask =
            KorosTimerTask(name, delay, repeat, coroutineScope, action).also { it.start() }
    }
}
*/

/**
 * import io.kotlintest.milliseconds
 * import io.kotlintest.shouldBe
 * import io.kotlintest.specs.StringSpec
 * import kotlinx.coroutines.CancellationException
 * import kotlinx.coroutines.cancel
 * import kotlinx.coroutines.channels.Channel
 *
 * class KorosTimerTaskTest : StringSpec(
 * 	{
 * 		"Basic operation" {
 * 			val channel = Channel<Unit>()
 * 			var times = 0
 * 			lateinit var timer: KorosTimerTask
 * 			timer = KorosTimerTask.start("test", repeat = 10.milliseconds, coroutineScope = this) {
 * 				if (times == 4) {
 * 					cancel("should not run a 5th time")
 * 				} else if (times == 3) {
 * 					// shutdown self to show the task finishes running.
 * 					timer.shutdown()
 * 				}
 *
 * 				channel.receive()
 * 				times++
 * 			}
 *
 * 			val loops = 4
 * 			repeat(loops) {
 * 				channel.send(Unit)
 * 			}
 *
 * 			times.shouldBe(loops)
 * 		}
 *
 * 		"cancel during task run" {
 * 			val c1 = Channel<Unit>()
 * 			val c2 = Channel<Unit>()
 * 			val cancelled = Channel<Unit>()
 * 			val timer = KorosTimerTask.start("test", repeat = 10.milliseconds, coroutineScope = this) {
 * 				try {
 * 					c1.receive()
 * 					c2.receive()
 * 				} catch (e: CancellationException) {
 * 					cancelled.send(Unit)
 * 				}
 * 			}
 * 			c1.send(Unit)
 * 			timer.cancel()
 * 			cancelled.receive()
 * 		}
 *
 * 		"shutdown before start never runs task" {
 * 			val task = KorosTimerTask("test", coroutineScope = this) {
 * 				cancel("should never run")
 * 			}
 * 			task.shutdown()
 * 			task.start()
 * 		}
 * 	}
 * )
 */

