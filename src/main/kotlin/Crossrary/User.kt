package Crossrary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var token by Users.token
    var latitude by Users.latitude
    var longitude by Users.longitude
}

