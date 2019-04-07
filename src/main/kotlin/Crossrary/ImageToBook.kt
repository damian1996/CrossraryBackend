package Crossrary

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class ImageToBook(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ImageToBook>(ImagesToBook)

    var id_book by ImagesToBook.id_book
    var id_img by ImagesToBook.id_img
}