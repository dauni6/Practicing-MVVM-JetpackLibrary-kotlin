package com.dontsu.dogs2.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dontsu.dogs2.model.DogApiService
import com.dontsu.dogs2.model.DogBreed
import com.dontsu.dogs2.model.DogDatabase
import com.dontsu.dogs2.util.NotificationHelper
import com.dontsu.dogs2.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ListViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime =  5 * 60 * 1000 * 1000 * 1000L //새로 리프래시하기 시간을 5분으로 정함, nonoseconds로 함. 이유는 updateTime()을 System.nanoseconds로 했기 때문
                    //빠르게 테스트 하고싶다면  10 * 1000 * 1000 * 1000L 하면 됨. 10초
    private val dogsService = DogApiService()
    private val disposable = CompositeDisposable() //메모리 누수를 막기 위해

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime() //SharedPreferencesHelper 클래스로부터 시간 가져오기
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime)
            fetchFromDatabase()
         else
            fetchFromRemote()
    }

    private fun checkCacheDuration() {
        val cachePreference = prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times( 1000 * 1000 * 1000L)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }
    
    //cache(Room database) 우회하고 레트로핏으로 부터 데이터 가져오기
    fun refreshBypassCache() {
        fetchFromRemote()
    }

    //Room database로부터 강아지 정보 가져오기
    private fun fetchFromDatabase() {
        loading.value = true
        //Room Database 접근할 때 코루틴 사용하기
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogRetrieved(dogs)
            Toast.makeText(getApplication(), "데이터베이스로 부터 가져옴", Toast.LENGTH_SHORT).show()
        }
    }
    
    //레트로핏으로부터 강아지 정보 가져오기
    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<DogBreed>>() {
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsInLocal(dogList) //데이터 local 저장하기, 코루틴 사용
                        Toast.makeText(getApplication(), "레트로핏 데이터로 부터 가져옴", Toast.LENGTH_SHORT).show()
                        NotificationHelper(getApplication()).createNotification()
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
        //Room Database 접근할 때 코루틴 사용하기
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
        prefHelper.saveUpdateTime(System.nanoTime())
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