package cr.ac.menufragment.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cr.ac.menufragment.entity.Ubicacion


@Dao
interface UbicacionDao {
    @Insert
    fun insert(entity: Ubicacion)

    @Query("SELECT * FROM ubicacion")
    fun getAll(): List<Ubicacion?>?
}