package Crossrary

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class Book(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Book>(Books)

    var title by Books.title
    var publication by Books.publication
    var state by Books.state
    var isbn by Books.isbn
    //var id_user by Books.id_user
    var matched by Books.matched
    var token by Books.token
}