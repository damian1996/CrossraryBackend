package Crossrary
import com.google.gson.Gson
import io.javalin.Javalin
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.util.FileUtil
import java.util.*
import kotlin.collections.HashSet
import com.google.gson.GsonBuilder
import kotlin.collections.ArrayList
import java.sql.Blob



object Users : IntIdTable() {
    val token = varchar("token", 512).index()
    val latitude = varchar("latitude", 20).index()
    val longitude = varchar("longitude", 20).index()
}

object Books : IntIdTable() {
    val title = varchar("title", 50).index()
    val publication = varchar("publication", 100).index() //year + place
    var state = varchar("state", 50).index()
    val isbn = varchar("isbn", 100).index()
    var matched = bool("matched")
    var token = varchar("token", 512)
}

// id(automatycznie) , blob
object Images : IntIdTable() {
    var binary_image  = blob("binary_image")
}

// id_ksiazki, id_obrazka z tabeli wyzej
object ImagesToBook : IntIdTable() {
    val id_book = integer("id_book").index()
    val id_img = integer("id_img").index()
}

object MatchedPairs

fun ser_usr (obj: HashSet<User>): String {
    for(el in obj) {
        var str = """ {
                token: ${el.token},
                latitude: ${el.latitude},
                longitude: ${el.longitude}
            }
        """.replace("\\s".toRegex(), "").replace("\\n".toRegex(), "")
        return str
    }
    return ""
}

fun ser_book(obj: HashSet<Book>): String {
    return ""
}

fun main() {

    val app = Javalin.create().start(7000)
    app.get("/") { ctx -> ctx.result("Hello World") }

    Database.connect("jdbc:sqlite:./data/data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED

    transaction {
        SchemaUtils.create(Users, Books, Images, ImagesToBook)
        commit()
    }

    transaction {
        app.routes {
            post("users") {ctx ->
                var user = ctx.body()
                update_users(user)
                ctx.status(201)
            }

            post("user_books/add") {ctx ->
                var book = ctx.body()
                print(book)
                val user_token = ctx.header("token")
                val (b, c) = update_books(user_token, book)
                assign_images_to_books(c, b?.id!!.value)
                ctx.status(201)
            }

            get("/user_books/list") { ctx ->
                //print(ctx.header("token"))
                transaction {
                    val user_token = ctx.header("token")
                    val l_books = get_list(user_token).toHashSet()
                    var gson = Gson()
                    var to_concat: ArrayList<String> = ArrayList<String>()
                    var sb = StringBuilder("[")

                    for(b in l_books) {
                        var cnt = count_photos(b)
                        var jsonString = gson.toJson(GetBook(b.title, b.publication, b.state, b.isbn, cnt))
                        //ctx.json(jsonString)
                        to_concat.add(jsonString)

                        sb.append("$jsonString, ")
                        //break
                    }
                    sb.append("]")
                    ctx.result(sb.toString());
                }
            }

            get("/check_matches") { ctx ->

            }
        }
        commit()
    }
    /*
    var users : HashSet<User> = HashSet<User>()
    transaction {
        users = User.all().toHashSet()
        var json_kurwa = ser_usr(users)

        app.routes {
            get("/users") {ctx ->
                var user = ctx.body()
                print(user)
                //update_users(user)
                ctx.json(json_kurwa)
                ctx.status(200)
            }
        }
    }
    print(users)
    */
}