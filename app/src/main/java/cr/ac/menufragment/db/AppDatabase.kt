package cr.ac.menufragment.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cr.ac.menufragment.MapsFragment
import cr.ac.menufragment.converter.Converters
import cr.ac.menufragment.dao.UbicacionDao
import cr.ac.menufragment.entity.Ubicacion

@Database(entities = [Ubicacion::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ubicacionDao(): UbicacionDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: MapsFragment): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.requireContext().applicationContext,
                        AppDatabase::class.java,
                        "ubicaciones-database"
                    ).build()
                }
            }
            return instance!!
        }
    }
}