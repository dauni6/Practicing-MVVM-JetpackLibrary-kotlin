package com.dontsu.dogs2.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogDao {
    @Insert
    suspend fun insertAll(vararg dogs: DogBreed): List<Long> //Long인 이유는 Model.kt 에서 만든 primary key를 리턴해서 데이터 몇 개 입력됐는지 확인하기 위함

    @Query("SELECT * FROM dogbreed") //room의 SQLite는 모두 소문자
    suspend fun getAllDogs(): List<DogBreed>

    @Query("SELECT * FROM dogbreed where uuid = :dogId") //uuid에 의해 하나의 객체만 찾기
    suspend fun getDog(dogId: Int): DogBreed

    @Query("DELETE FROM dogbreed") //삭제하기
    suspend fun deleteAllDogs()
}