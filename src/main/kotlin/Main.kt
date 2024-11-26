package app.quantun

import v1.RailwayHandler
import v4.contract.UserDTO
import v4.services.UserService


fun main() {

    val multiplyByTwo = { number: Int -> RailwayHandler.success(number * 2) }
    val subtractFive = { number: Int -> RailwayHandler.success(number - 5) }
    val validateNumber = { number: Int ->
        when {
            number > 100 -> RailwayHandler.failure(IllegalArgumentException("Number must be less than 100"))
            number < 0 -> RailwayHandler.failure(IllegalArgumentException("Number must be greater than 0"))
            else -> RailwayHandler.success(number)
        }
    }
    val convertToString = { number: Int -> RailwayHandler.success(number.toString()) }

    // Use RailwayHandler to chain these operations together
    val result: RailwayHandler<String>? = RailwayHandler
        .success(15) // Start with initial value of type Integer
        .flatMap(validateNumber) // Changes to RailwayHandler<Int>
        ?.flatMap(subtractFive) // Changes to RailwayHandler<Int>
        ?.flatMap(multiplyByTwo) // Changes to RailwayHandler<Int>
        ?.flatMap(convertToString) // Final mapping to a RailwayHandler<String>

    // Handle the result
    if (result?.isSuccess == true) {
        println("Operation succeeded with result: ${result.value}")
    } else {
        println("Operation failed ${result?.error?.message}")
    }



    val userService: UserService = UserService()

    val result2: RailwayHandler<UserDTO>? = userService.createUser(UserDTO("John Doe", "demo@demo.com"))
    if (result2?.isSuccess == true) {
        println("User created successfully: ${result2.value}")
    } else {
        println("User creation failed: ${result2?.error?.message}")
    }



}
