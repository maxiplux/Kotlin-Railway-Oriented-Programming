package v4.services

import v4.model.User


interface UserRepository {
    fun save(user: User?): User?
}
