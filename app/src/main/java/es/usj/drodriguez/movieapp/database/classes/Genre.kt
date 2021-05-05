package es.usj.drodriguez.movieapp.database.classes

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity (tableName = Genre.TABLE_NAME)
data class Genre (
    @PrimaryKey @ColumnInfo(name = ID) @SerializedName(ID)val id: Int,
    @ColumnInfo (name = NAME) @SerializedName(NAME)var name: String){
    companion object{
        const val ID = "id"
        const val NAME = "name"
        const val TABLE_NAME = "genres"
    }
}