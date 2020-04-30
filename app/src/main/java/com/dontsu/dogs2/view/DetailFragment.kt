package com.dontsu.dogs2.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.dontsu.dogs2.R
import com.dontsu.dogs2.model.DogBreed
import com.dontsu.dogs2.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel

    private var dogUuid = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch()

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dog.observe(this, Observer {dog: DogBreed ->
            dog?.let {
                detail_dogName.text = dog.dogBreed
                detail_dogLifespan.text = dog.lifespan
                detail_dogTemperament.text = dog.temperament
                detail_dogPurpose.text= dog.bredFor
            }
        })
    }
}
