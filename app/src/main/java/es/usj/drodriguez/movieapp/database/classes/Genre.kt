package es.usj.drodriguez.movieapp.database.classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity (tableName = Genre.TABLE_NAME)
data class Genre (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) @SerializedName(ID)val id: Long,
    @ColumnInfo (name = NAME) @SerializedName(NAME)var name: String,
    @ColumnInfo(name = FAVORITE) @Transient var favorite: Boolean): Serializable{
    companion object{
        const val ID = "id"
        const val NAME = "name"
        const val TABLE_NAME = "genres"
        const val FAVORITE = "favorite"
    }
}