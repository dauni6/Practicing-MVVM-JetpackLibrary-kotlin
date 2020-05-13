package com.dontsu.dogs2.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dontsu.dogs2.R
import com.dontsu.dogs2.databinding.ItemDogBinding
import com.dontsu.dogs2.model.DogBreed
import com.dontsu.dogs2.util.getProgressDrawable
import com.dontsu.dogs2.util.loadImage
import com.dontsu.dogs2.view.ListFragmentDirections
import com.dontsu.dogs2.view.listeners.DogClickListener
import kotlinx.android.synthetic.main.item_dog.view.*

class DogListAdapter(private val dogsList: ArrayList<DogBreed>): RecyclerView.Adapter<DogListAdapter.DogViewHolder>(), DogClickListener {

    fun updateDogList(newDogList: List<DogBreed>) {
        dogsList.clear()
        dogsList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        //val view = inflater.inflate(R.layout.item_dog, parent, false) data binding 안 쓸 때
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater, R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }


    override fun getItemCount(): Int = dogsList.size

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(dogsList[position])
    }

    inner class DogViewHolder(var view: ItemDogBinding): RecyclerView.ViewHolder(view.root) {
        fun bind(dog: DogBreed) {
            /*
            //data binding 안 쓸 때
            view.name.text = dog.dogBreed
            view.lifespan.text = dog.lifespan
            view.setOnClickListener {
                val action = ListFragmentDirections.actionDetailFragment()
                action.dogUuid = dog.uuid //uuid 정보를 갖고 DetailFragment로 이동
                Navigation.findNavController(it).navigate(action)
            }
            view.imageView.loadImage(dog.imageUrl, getProgressDrawable(view.imageView.context))*/
            view.dog = dog //view가 ItemDogBinding 이고 item_dog.xml에 결합되어 있어 데이터를 자동으로 바꿔줌
            view.listener = this@DogListAdapter
        }
    }

    override fun onDogClicked(view: View) {
        val uuid = view.dogId.text.toString().toInt()
        val action = ListFragmentDirections.actionDetailFragment()
        action.dogUuid = uuid //uuid 정보를 갖고 DetailFragment로 이동
        Navigation.findNavController(view).navigate(action)
    }

}