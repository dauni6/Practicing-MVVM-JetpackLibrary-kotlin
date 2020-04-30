package com.dontsu.dogs2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dontsu.dogs2.model.DogBreed

class DetailViewModel: ViewModel() {
    val dog = MutableLiveData<DogBreed>()

    fun fetch() {
        val dog1 = DogBreed("1", "진돗개", "15년", "breedGroup", "애완용", "충성심강함", "")
        dog.value = dog1
    }
}