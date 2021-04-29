package es.usj.drodriguez.movieapp.database

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


@Entity(tableName = Movie.TABLE_NAME)
data class Movie(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) @SerializedName(ID) val id: Int,
    @ColumnInfo(name = TITLE) @SerializedName(TITLE) var title: String,
    @ColumnInfo(name = DESCRIPTION) @SerializedName(DESCRIPTION) var description: String,
    @ColumnInfo(name = DIRECTOR) @SerializedName(DIRECTOR) var director: String,
    @ColumnInfo(name = YEAR) @SerializedName(YEAR) var year: Int,
    @ColumnInfo(name = RUNTIME) @SerializedName(RUNTIME) var runtime: Int,
    @ColumnInfo(name = RATING) @SerializedName(RATING) var rating: Float,
    @ColumnInfo(name = VOTES) @SerializedName(VOTES) var votes: Int,
    @ColumnInfo(name = REVENUE) @SerializedName(REVENUE) var revenue: Float,
    @ColumnInfo(name = GENRES) @SerializedName(GENRES) var genres: List<Int>,
    @ColumnInfo(name = ACTORS) @SerializedName(ACTORS)  var actors: List<Int>
){
    companion object{
        const val ID = "id"
        const val TITLE = "title"
        const val DESCRIPTION ="description"
        const val DIRECTOR ="director"
        const val YEAR = "year"
        const val RUNTIME = "runtime"
        const val RATING = "rating"
        const val VOTES = "votes"
        const val REVENUE = "revenue"
        const val GENRES = "genres"
        const val ACTORS = "actors"
        const val TABLE_NAME = "movies"
    }
}
class IntListConverter{
    private val gson = Gson()
    @TypeConverter
    fun stringToIntList(json: String):List<Int>{
        val listType: Type = object : TypeToken<List<Int?>?>() {}.type
        return gson.fromJson(json, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<Int?>?): String? {
        return gson.toJson(someObjects)
    }
}