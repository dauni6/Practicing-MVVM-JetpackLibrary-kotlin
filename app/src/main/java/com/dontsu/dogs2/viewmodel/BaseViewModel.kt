package com.dontsu.dogs2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

//데이터 베이스에 접근할 때 context가 필요하기 때문에 application이 필요함 또한 AndroidViewModel은 ViewModel을 포함하고 있다.
abstract class BaseViewModel(application: Application): AndroidViewModel(application), CoroutineScope {
    private val job  = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main //Main thread에 정의된 Coroutine dispatcher

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}