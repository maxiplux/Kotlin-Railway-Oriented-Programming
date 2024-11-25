package v1



import io.github.oshai.kotlinlogging.KotlinLogging

class RailwayHandler<T> private constructor(
    val value: T?,
    val error: Throwable?
) {
    private val log = KotlinLogging.logger {}
    private val steps: MutableList<(T) -> RailwayHandler<T>> = mutableListOf()

    val isSuccess: Boolean
        get() = error == null

    fun addStep(step: (T) -> RailwayHandler<T>): RailwayHandler<T> {
        log.info { "Adding step: $step" }
        steps.add(step)
        return this
    }

    fun execute(): RailwayHandler<T> {
        log.info { "Executing pipeline with initial value: $value" }
        var current = this
        for (step in steps) {
            if (current.isSuccess) {
                try {
                    current = current.value?.let { step(it) } ?: return failure(IllegalStateException("Value is null"))
                    log.info { "Step executed successfully, current value: ${current.value}" }
                } catch (e: Throwable) {
                    log.error(e) { "Step execution failed" }
                    return failure(e)
                }
            } else {
                log.warn { "Short-circuiting on error: ${current.error?.message}" }
                return current
            }
        }
        log.info { "Pipeline execution completed with final value: ${current.value}" }
        return current
    }

    fun <R> flatMap(mapper: (T) -> RailwayHandler<R>?): RailwayHandler<R>? {
        return if (isSuccess) {
            try {
                log.info { "FlatMapping value: $value" }
                value?.let { mapper(it) }
            } catch (e: Throwable) {
                log.error(e) { "FlatMapping failed" }
                failure(e)
            }
        } else {
            log.warn { "FlatMapping skipped due to error: ${error?.message}" }
            failure(error)
        }
    }

    fun <B> map(mapper: (T) -> B): RailwayHandler<B> {
        return if (isSuccess) {
            try {
                log.info { "Mapping value: $value" }
                success(value?.let(mapper) ?: throw IllegalStateException("Value is null"))
            } catch (e: Throwable) {
                log.error(e) { "Mapping failed" }
                failure(e)
            }
        } else {
            log.warn { "Mapping skipped due to error: ${error?.message}" }
            failure(error)
        }
    }

    companion object {
        fun <T> start(value: T): RailwayHandler<T> {
            return success(value)
        }

        fun <T> success(value: T): RailwayHandler<T> {
            return RailwayHandler(value, null)
        }

        fun <T> failure(error: Throwable?): RailwayHandler<T> {
            return RailwayHandler(null, error)
        }
    }
}
