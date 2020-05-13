package com.dontsu.dogs2.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.dontsu.dogs2.R
import com.dontsu.dogs2.databinding.FragmentDetailBinding
import com.dontsu.dogs2.model.DogBreed
import com.dontsu.dogs2.util.getProgressDrawable
import com.dontsu.dogs2.util.loadImage
import com.dontsu.dogs2.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var dataBinding: FragmentDetailBinding

    private var dogUuid = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate<FragmentDetailBinding>(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid //DogListAdapter로 부터 받아온 uuid
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUuid)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogLiveData.observe(this, Observer {dog: DogBreed ->
            dog?.let {
               /* data biding 쓰기전 코드
               detail_dogName.text = dog.dogBreed
                detail_dogLifespan.text = dog.lifespan
                detail_dogTemperament.text = dog.temperament
                detail_dogPurpose.text= dog.bredFor
                context?.let { detail_dogImage.loadImage(dog.imageUrl, getProgressDrawable(it)) }*/
                dataBinding.dog = dog
            }
        })
    }
}
