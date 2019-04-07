package Crossrary

import com.google.gson.Gson
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.reflect.TypeToken;
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

data class TestModel(
    val token: String,
    val latitude: String,
    val longitude: String
)

data class TestModel1(
    val title: String,
    val publication: String,
    val state: String,
    val isbn: String,
    val matched: Boolean,
    val photos: Integer
)

data class GetBook(
    val title: String,
    val publication: String,
    val state: String,
    val isbn: String,
    val photos: Int
)

data class GetListBooks(
    val list_of_jsons: ArrayList<String>
)

fun get_list(user_token: String?) : SizedIterable<Book> {
    print(Books.token)
    Book.find {Books.token eq user_token.toString()}
    return Book.all()
}

fun assign_images_to_books(photos: Integer, book: Int) {
    transaction {
        ImageToBook.new {
            id_book = book
            id_img = photos.toInt()
        }
        commit()
    }
}

fun count_photos(b: Book): Int {
    return (ImageToBook.find {ImagesToBook.id_book eq b.id.value}).count()
}

fun update_books(toke: String?, book: String): Pair<Book?, Integer> {
    val gson = Gson()
    val targetto = gson.fromJson(book, TestModel1::class.java)
    var x: Book? = null
    transaction {
        x = Book.new {
            title = targetto.title
            publication = targetto.publication
            state = targetto.state
            isbn = targetto.isbn
            matched = targetto.matched
            token = toke.toString()
        }
        commit()
    }
    // czy dziala bez commita?
    return Pair(x, targetto.photos)
}

fun update_users(user: String) {
    transaction {
        val gson = Gson() // Or use new GsonBuilder().create();
        val target2 = gson.fromJson(user, TestModel::class.java) // deserializes json into target2
        User.new {
            token = target2.token
            latitude = target2.latitude
            longitude = target2.longitude
        }
        commit()
    }
}

data class Book_title (
    val title: String
)

fun sign_match(to_parse: String, user_token: String) {
    transaction {
        val gson = Gson() // Or use new GsonBuilder().create();
        val target2 = gson.fromJson(to_parse, Book_title::class.java) // deserializes json into target2
        var title = target2.title

        Books.update ({ Books.title eq title }) {
            it[Books.matched] = true
        }
        our_book = Book.find {Books.title eq title}

        commit()
    }
}