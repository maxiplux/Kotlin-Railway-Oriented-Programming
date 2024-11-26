package v4.services

import v1.RailwayHandler
import v4.contract.UserDTO
import v4.model.User

class UserService {
    private val userRepository: UserRepository = object : UserRepository {
        override fun save(user: User?): User {
            val userWithId = User(1L, user!!.name, user.email + "SAVE")
            return userWithId
        }
    }

    fun createUser(userDTO: UserDTO?): RailwayHandler<UserDTO>? {
        // Define the steps of the railway pipeline
        val validateDTO = { userDTO: UserDTO? ->
            when {
                userDTO == null -> RailwayHandler.failure(IllegalArgumentException("UserDTO cannot be null"))
                userDTO.name.isEmpty() -> RailwayHandler.failure(IllegalArgumentException("Name cannot be null or empty"))
                !userDTO.email.contains("@") -> RailwayHandler.failure(IllegalArgumentException("Invalid email address"))
                else -> RailwayHandler.success(userDTO)
            }
        }

        return RailwayHandler.success(userDTO)
            .flatMap { validateDTO(it) }
            ?.flatMap { mapToEntity(it) }
            ?.flatMap { this.saveEntity(it) }
            ?.map { this.mapToDTO(it) }
    }

    // Step 2: Map DTO to Entity
    private fun mapToEntity(userDTO: UserDTO?): RailwayHandler<User?> {
        return try {
            val user = User(null, userDTO!!.name, userDTO.email)
            RailwayHandler.success(user)
        } catch (e: Exception) {
            RailwayHandler.failure(e)
        }
    }

    // Step 3: Save Entity to Database
    private fun saveEntity(user: User?): RailwayHandler<User?> {
        return try {
            if (user == null) {
                RailwayHandler.failure(IllegalArgumentException("User cannot be null"))
            } else {
                val savedUser = userRepository.save(user)
                RailwayHandler.success(savedUser)
            }
        } catch (e: Exception) {
            RailwayHandler.failure(e)
        }
    }

    // Step 4: Map Entity back to DTO
    private fun mapToDTO(user: User?): UserDTO {
        return UserDTO(user!!.name, user.email)
    }
}
