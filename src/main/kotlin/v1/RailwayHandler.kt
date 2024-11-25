package v1

import java.util.*
import java.util.function.Function

class RailwayHandler<T> // Private constructor for internal success/failure creation
private constructor(// Internal data structure to hold the value or error
    val value: T, val error: Throwable?
) {
    // List of steps to build the pipeline
    private val steps: MutableList<Function<T, RailwayHandler<T>>> = LinkedList()


    val isSuccess: Boolean
        // Check whether the operation was successful
        get() = error == null

    // Add step to the pipeline
    fun addStep(step: Function<T, RailwayHandler<T>>): RailwayHandler<T> {
        steps.add(step)
        return this
    }

    // Execute all steps in the pipeline
    fun execute(): RailwayHandler<T> {
        var current = this
        for (step in steps) {
            if (current.isSuccess) {
                try {
                    current = step.apply(current.value)
                } catch (e: Throwable) {
                    return failure(e)
                }
            }
            return current // Short-circuit on error
        }
        return current
    }


    // Function to add more transformations in a composable manner (remains of type A)
    fun <R> flatMap(mapper: Function<T, RailwayHandler<R>?>): RailwayHandler<R>? {
        if (isSuccess) {
            return try {
                mapper.apply(value)
            } catch (e: Throwable) {
                failure(e)
            }
        }
        return failure(error)
    }

    // Function for mapping the type A to a different type B without affecting the railway flow
    fun <B> map(mapper: Function<T, B>): RailwayHandler<B> {
        if (isSuccess) {
            return try {
                success(mapper.apply(value))
            } catch (e: Throwable) {
                failure(e)
            }
        }
        return failure(error)
    }


    companion object {
        // Example of how to create a new builder that can be initialized
        fun <T> start(value: T): RailwayHandler<T> {
            return success(value)
        }

        // Static methods for success and failure creation
        fun <T> success(value: T): RailwayHandler<T> {
            return RailwayHandler(value, null)
        }

        fun <T> failure(error: Throwable?): RailwayHandler<T?> {
            return RailwayHandler(null, error)
        }
    }
}

