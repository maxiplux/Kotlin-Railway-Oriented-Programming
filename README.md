# Kotlin-Railway-Oriented-Programming

A Kotlin-based demonstration project showcasing Railway-Oriented Programming (ROP) pattern for elegant error handling and workflow management in functional programming.

## Overview
This project demonstrates how to implement Railway-Oriented Programming in Kotlin to handle operations and errors gracefully. It provides a practical implementation of the ROP pattern with real-world examples including data validation, transformation pipelines, and service layer operations.

## Features
- ✅ Railway-oriented programming pattern for error handling
- ✅ Type-safe operation chaining with `flatMap` and `map`
- ✅ User creation service with multi-step validation
- ✅ Comprehensive logging with KotlinLogging
- ✅ Gradle build automation
- ✅ Code quality checks with Detekt

## Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher
- **Kotlin**: Version 2.0.21 (managed by Gradle)
- **Gradle**: 8.x or higher (wrapper included)

## Technologies Used
- **Kotlin**: 2.0.21
- **Gradle**: Build automation and dependency management
- **kotlin-logging-jvm**: 7.0.0 - Logging framework
- **Logback**: 1.4.11 - Logging implementation
- **SLF4J**: 2.0.16 - Logging facade
- **Detekt**: 1.19.0 - Static code analysis

## Project Structure
```
Kotlin-Railway-Oriented-Programming/
├── src/main/kotlin/
│   ├── Main.kt                          # Entry point with examples
│   ├── v1/
│   │   └── RailwayHandler.kt            # Core ROP implementation
│   └── v4/
│       ├── contract/
│       │   └── UserDTO.kt               # Data Transfer Object
│       ├── model/
│       │   └── User.kt                  # User entity model
│       └── services/
│           ├── UserRepository.kt        # Repository interface
│           └── UserService.kt           # Service with ROP workflow
├── build.gradle.kts                     # Build configuration
├── settings.gradle.kts                  # Project settings
└── README.md                            # This file
```

## Getting Started

### Installation
1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd Kotlin-Railway-Oriented-Programming
   ```

2. **Build the project**:
   ```bash
   ./gradlew build
   ```
   On Windows:
   ```cmd
   gradlew.bat build
   ```

### Running the Project
Execute the main application:
```bash
./gradlew run
```
On Windows:
```cmd
gradlew.bat run
```

Or run directly from your IDE (IntelliJ IDEA, Eclipse, etc.) by executing the `Main.kt` file.

## Railway-Oriented Programming (ROP)
Railway-Oriented Programming (ROP) is a functional programming pattern that helps manage operations that can succeed or fail. It is particularly useful for handling complex workflows with multiple steps, where each step can potentially fail. The pattern is inspired by the idea of a railway track, where the process can either stay on the "success" track or switch to the "failure" track if an error occurs.

### Key Concepts

#### RailwayHandler Class
The `RailwayHandler` class encapsulates the result of an operation, which can either be a success (with a value) or a failure (with an error). It provides methods to chain operations (`flatMap` and `map`) and handle errors gracefully.

**Key methods**:
- `success(value)` - Creates a successful result
- `failure(error)` - Creates a failed result
- `flatMap(mapper)` - Chains operations that return `RailwayHandler`
- `map(mapper)` - Transforms the value inside a successful result
- `isSuccess` - Property to check if operation succeeded

#### Success and Failure
The `RailwayHandler` class has companion object methods `success` and `failure` to create instances representing successful and failed operations, respectively.

#### Chaining Operations
The `flatMap` method is used to chain operations. If the current operation is successful, it applies the provided function to the value and returns a new `RailwayHandler` with the result. If the current operation has failed, it skips the function and returns the current failure, automatically propagating errors through the chain.

### Example Usage

#### Basic Example: Number Processing Pipeline
In the `main` function, several operations (like `multiplyByTwo`, `subtractFive`, `validateNumber`, and `convertToString`) are chained together using `flatMap`. If any operation fails, the chain short-circuits, and the error is propagated.

```kotlin
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

val result: RailwayHandler<String>? = RailwayHandler
    .success(15)                    // Start with initial value
    .flatMap(validateNumber)         // Validate: 15 is valid
    ?.flatMap(subtractFive)          // Transform: 15 - 5 = 10
    ?.flatMap(multiplyByTwo)         // Transform: 10 * 2 = 20
    ?.flatMap(convertToString)       // Convert: 20 -> "20"

if (result?.isSuccess == true) {
    println("Operation succeeded with result: ${result.value}")
    // Output: Operation succeeded with result: 20
} else {
    println("Operation failed: ${result?.error?.message}")
}
```

#### Advanced Example: UserService
The `UserService` class demonstrates how to use ROP for a more complex workflow, such as creating a user. It validates the input, maps it to an entity, saves it to a repository, and maps it back to a DTO, handling errors at each step.

```kotlin
val userService = UserService()

val result: RailwayHandler<UserDTO>? = userService.createUser(
    UserDTO("John Doe", "john@example.com")
)

if (result?.isSuccess == true) {
    println("User created successfully: ${result.value}")
} else {
    println("User creation failed: ${result?.error?.message}")
}
```

**The UserService workflow includes**:
1. **Validation** - Checks that UserDTO is not null, name is not empty, and email contains "@"
2. **Entity Mapping** - Converts UserDTO to User entity
3. **Persistence** - Saves the user to the repository
4. **Response Mapping** - Converts saved User entity back to UserDTO

Each step can fail independently, and errors are automatically propagated through the chain without nested try-catch blocks.

## Benefits of ROP
- **Explicit Error Handling**: All possible failures are explicit in the type system
- **Composability**: Operations can be easily composed and reused
- **Readability**: Code reads like a linear sequence of operations
- **No Exception Throwing**: Errors are values, not exceptions
- **Automatic Short-Circuiting**: Failed operations stop the pipeline automatically
- **Type Safety**: Compile-time guarantees about success and failure paths

## Building and Testing

### Build the project
```bash
./gradlew build
```

### Run tests
```bash
./gradlew test
```

### Run code quality checks
```bash
./gradlew detekt
```

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is available for educational and demonstration purposes.

## Additional Resources
- [Railway Oriented Programming (Scott Wlaschin)](https://fsharpforfunandprofit.com/rop/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Functional Error Handling in Kotlin](https://arrow-kt.io/)
