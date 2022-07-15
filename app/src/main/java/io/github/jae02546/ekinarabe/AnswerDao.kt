package io.github.jae02546.ekinarabe

import androidx.room.*

@Dao
interface AnswerDao {

    //戻り値 RowId、-1なら失敗
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(answerTable: AnswerTable): Long

    //戻り値 update件数、0なら失敗
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(answerTable: AnswerTable): Int

    //戻り値 delete件数、0なら失敗
    @Delete
    fun delete(answerTable: AnswerTable): Int

    @Query("SELECT * FROM AnswerTable WHERE playerNo = :playerNo AND datasetNo = :datasetNo AND lineNo = :lineNo")
    fun getAnswer(playerNo: Int, datasetNo: Int, lineNo: Int): AnswerTable?

    @Query("SELECT * FROM AnswerTable WHERE playerNo = :playerNo AND datasetNo = :datasetNo")
    fun getAnswer(playerNo: Int, datasetNo: Int): List<AnswerTable>?

    @Query("SELECT * FROM AnswerTable")
    fun getAll(): List<AnswerTable>?
}

@Dao
interface RecordDao {

    //戻り値 RowId、-1なら失敗
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(recordTable: RecordTable): Long

    //戻り値 update件数、0なら失敗
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(recordTable: RecordTable): Int

    //戻り値 delete件数、0なら失敗
    @Delete
    fun delete(recordTable: RecordTable): Int

    @Query("SELECT * FROM RecordTable WHERE datasetNo = :datasetNo AND lineNo = :lineNo")
    fun getRecord(datasetNo: Int, lineNo: Int): RecordTable?

    @Query("SELECT * FROM RecordTable WHERE datasetNo = :datasetNo")
    fun getRecord(datasetNo: Int): List<RecordTable>?

    @Query("SELECT * FROM RecordTable")
    fun getAll(): List<RecordTable>?
}

@Dao
interface PlayStateDao {

    //戻り値 RowId、-1なら失敗
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playStateTable: PlayStateTable): Long

    //戻り値 update件数、0なら失敗
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(playStateTable: PlayStateTable): Int

    //戻り値 delete件数、0なら失敗
    @Delete
    fun delete(playStateTable: PlayStateTable): Int

    @Query("SELECT * FROM PlayStateTable WHERE playerNo = :playerNo AND datasetNo = :datasetNo")
    fun getPlayState(playerNo: Int, datasetNo: Int): PlayStateTable?

    @Query("SELECT * FROM PlayStateTable WHERE playerNo = :playerNo")
    fun getPlayState(playerNo: Int): List<PlayStateTable>?

    @Query("SELECT * FROM PlayStateTable")
    fun getAll(): List<PlayStateTable>?
}
