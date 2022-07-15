package io.github.jae02546.ekinarabe

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.Duration
import java.util.*

@Database(
    entities = [AnswerTable::class, RecordTable::class, PlayStateTable::class],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AnswerDatabase : RoomDatabase() {

    abstract fun answerDao(): AnswerDao
    abstract fun recordDao(): RecordDao
    abstract fun playStateDao(): PlayStateDao

    companion object {

        private var INSTANCE: AnswerDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): AnswerDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AnswerDatabase::class.java, "PlayerAnswer.db"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}

private class Converters {
    //よくわからないが、これでnullのエラーはでない?
    private val listType: Type = object : TypeToken<List<Int>>() {}.type

    @TypeConverter
    fun fromAnswerListJson(answerListJson: String): List<Int> =
        Gson().fromJson(answerListJson, listType)

    @TypeConverter
    fun answerListToJson(answerList: List<Int>): String = Gson().toJson(answerList)

    @TypeConverter
    fun fromDuration(charSequence: String): Duration =
        Duration.parse(charSequence)

    @TypeConverter
    fun durationToCharSequence(duration: Duration): String =
        duration.toString()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
