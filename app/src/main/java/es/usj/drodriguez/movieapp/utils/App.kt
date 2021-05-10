package es.usj.drodriguez.movieapp.utils

import android.app.Application
import es.usj.drodriguez.movieapp.database.Database
import es.usj.drodriguez.movieapp.database.DatabaseRepository

class App : Application() {
     val database by lazy { Database.getDatabase(this) }
    val repository by lazy { database.let { DatabaseRepository(it.movieDao(), it.genreDao(), it.actorDao()) } }
}