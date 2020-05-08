package com.dontsu.dogs2.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//https://developer.android.com/training/data-storage/room 안드로이드 공식 API Room 참고할 것
@Database(entities = arrayOf(DogBreed::class), version = 1)
abstract class DogDatabase: RoomDatabase() { //RoomDatabase 는 리소스를 굉장히 많이 잡아먹으므로 반드시 싱글톤 패턴으로 만들어야 함
    abstract fun dogDao(): DogDao

    companion object {
        //스레드 사용시 값을 읽거나 쓰면 데이터가 일치하지 않기 때문에 이것을 방지하기 위해 데이터를 캐시에 넣지 않도록 하기 위하여 Volatile 키워드를 씀. 또한 원자성까지 보호하기 위해 synchronized를 같이 사용함
        @Volatile private var instance: DogDatabase? = null
        private val LOCK = Any()
        //코틀린에서 invoke 라는 이름으로 만들어진 함수는 이름없이 실행될 수 있다. 또한 엄밀히 말하면 함수처럼 보이지만 연산자(operator)이다
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) { //데이터 원자성(Atomicity) 보존을 위해 synchronized를 같이 사용
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        //생성한 데이터베이스 인스턴스 가져오기
       private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            DogDatabase::class.java,
            "dogdatabase"
        ).build()
    }
}