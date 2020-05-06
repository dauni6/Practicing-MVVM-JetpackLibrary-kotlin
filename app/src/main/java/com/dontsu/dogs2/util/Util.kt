package com.dontsu.dogs2.util

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dontsu.dogs2.R

fun getProgressDrawable(context: Context): CircularProgressDrawable { //이미지 로딩시 스피너 보여주기
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable) { //ImageView 메서드 확장
    val options = RequestOptions()
        .placeholder(progressDrawable) //resource가 loading되는 동안 보여줄 drawable, 나는 progressDrawable로 함
        .error(R.mipmap.ic_dog_icon) //이미지 불러오기 실패시 보여줄 이미지
    Glide.with(context) //여기서 context는 ImageView(ImageView를 확장중이기 때문에 내부적으로 context 사용가능)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this) //this는 현재 ImageView
}