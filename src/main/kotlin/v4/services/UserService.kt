package v4.services

import v1.RailwayHandler
import v4.contract.UserDTO
import v4.model.User


class UserService {
    private val userRepository: UserRepository = object : UserRepository {
        override fun save(user: User?): User? {
            val userWithId = User(1L, user!!.name, user.email + "SAVE")
            return userWithId
        }
    }


    fun createUser(userDTO: UserDTO?): RailwayHandler<UserDTO> {
        // Start the railway pipeline
        return RailwayHandler.success(userDTO)
            .flatMap { userDTO: UserDTO -> this.validateDTO(userDTO) }
            .flatMap { userDTO: UserDTO -> this.mapToEntity(userDTO) }
            .flatMap { user: User -> this.saveEntity(user) }
            .map { user: User -> this.mapToDTO(user) }
    }

    // Step 1: Validate DTO
    private fun validateDTO(userDTO: UserDTO): RailwayHandler<UserDTO?> {
        if (userDTO.name == null || userDTO.name.isEmpty()) {
            return RailwayHandler.failure(IllegalArgumentException("Name cannot be null or empty"))
        }
        if (userDTO.emai == null || !userDTO.emai.contains("@")) {
            return RailwayHandler.failure(IllegalArgumentException("Invalid email address"))
        }
        return RailwayHandler.success(userDTO)
    }

    // Step 2: Map DTO to Entity
    private fun mapToEntity(userDTO: UserDTO): RailwayHandler<User?> {
        try {
            val user = User(null, userDTO.name, userDTO.emai)
            return RailwayHandler.success(user)
        } catch (e: Exception) {
            return RailwayHandler.failure(e)
        }
    }

    // Step 3: Save Entity to Database
    private fun saveEntity(user: User): RailwayHandler<User?> {
        try {
            val savedUser = userRepository.save(user)
            return RailwayHandler.success(savedUser)
        } catch (e: Exception) {
            return RailwayHandler.failure(e)
        }
    }

    // Step 4: Map Entity back to DTO
    private fun mapToDTO(user: User): UserDTO {
        return UserDTO(user.name, user.email)
    }
}
