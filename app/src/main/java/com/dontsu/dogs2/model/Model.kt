package com.dontsu.dogs2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * 1. @Entity annotation 을 사용하면 SQLite database 내에 테이블이 생성되어 data class field variables 를 포함한다
 * 만약 테이블이름을 따로 정한다면 @Entity(tableName = "dog_breed") 와 같이 정의할 수 있다. 정의하지 않으면 data class 이름 그대로 테이블이 만들어진다.
 * 주의할건 SQLite의 테이블 이름은 대소문자를 구분하지 않는다.
 * 2. tableName 속성과 마찬가지로 Room은 필드 이름을 SQLite의 열 이름으로 사용한다. 이름을 다르게 지정하고 싶다면 field variable에 @ColumnInfo를 설정한다
 * 3. primary key는 반드시 하나 설정해야 된다.
 * */
@Entity
data class DogBreed(
    @ColumnInfo(name = "breed_id")
    @SerializedName("id")
    val breedId: String?,

    @ColumnInfo(name = "dog_name")
    @SerializedName("name")
    val dogBreed: String?,

    @ColumnInfo(name = "life_span")
    @SerializedName("life_span")
    val lifespan: String?,

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    val breedGroup: String?,

    @ColumnInfo(name = "bred_for")
    @SerializedName("bred_for")
    val bredFor: String?,

    @SerializedName("temperament")
    val temperament: String?,

    @ColumnInfo(name = "dog_url")
    @SerializedName("url")
    val imageUrl: String?
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0 //default 값 0
}

data class DogPalette(var color: Int)

data class SmsInfo(
    var to: String,
    var text: String,
    var imageurl: String?
)