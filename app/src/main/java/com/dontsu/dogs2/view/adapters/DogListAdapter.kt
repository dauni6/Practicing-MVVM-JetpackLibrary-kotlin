package com.dontsu.dogs2.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dontsu.dogs2.R
import com.dontsu.dogs2.model.DogBreed
import com.dontsu.dogs2.view.ListFragmentDirections
import kotlinx.android.synthetic.main.item_dog.view.*

class DogListAdapter(private val dogsList: ArrayList<DogBreed>): RecyclerView.Adapter<DogListAdapter.DogViewHolder>() {

    fun updateDogList(newDogList: List<DogBreed>) {
        dogsList.clear()
        dogsList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =  DogViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_dog, parent, false)
    )

    override fun getItemCount(): Int = dogsList.size

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(dogsList[position])
    }

    inner class DogViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        fun bind(dog: DogBreed) {
            view.name.text = dog.dogBreed
            view.lifespan.text = dog.lifespan
            view.setOnClickListener {
                Navigation.findNavController(it).navigate(ListFragmentDirections.actionDetailFragment())
            }
        }
    }

}