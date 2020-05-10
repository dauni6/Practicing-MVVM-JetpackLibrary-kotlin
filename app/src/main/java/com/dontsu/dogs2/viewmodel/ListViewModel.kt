package com.dontsu.dogs2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dontsu.dogs2.model.DogApiService
import com.dontsu.dogs2.model.DogBreed
import com.dontsu.dogs2.model.DogDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application): BaseViewModel(application) {

    private val dogsService = DogApiService()
    private val disposable = CompositeDisposable() //메모리 누수를 막기 위해

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchFromRemote()
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<DogBreed>>() {
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsInLocal(dogList) //데이터 local 저장하기, 코루틴 사용
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }
                })
        )
    }
    
    //데이터 저장하기
    private fun storeDogsInLocal(list : List<DogBreed>) {
        //코루틴 사용하기
            launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs() //두번째 불러왔을때 남아있는 데이터에 덧붙혀진 데이터를 보여주지 않기 위해서 먼저 삭제해준다
            val result = dao.insertAll(*list.toTypedArray()) //*를 쓰면 요소를 하나하나씩 보낼 수 있음, insertAll의 매개변수가 vararg 로 되어있기 때문에 toTypedArray()를 같이 씀
            var i = 0
                while (i < list.size) {
                    list[i].uuid = result[i].toInt()
                    ++i
                }
                dogRetrieved(list) //UI 변경하기
            }
    }

    //UI 변경하기
    private fun dogRetrieved(dogList : List<DogBreed>) {
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}