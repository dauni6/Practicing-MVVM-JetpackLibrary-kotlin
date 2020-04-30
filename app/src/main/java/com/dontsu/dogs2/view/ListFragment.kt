package com.dontsu.dogs2.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.dontsu.dogs2.R
import com.dontsu.dogs2.view.adapters.DogListAdapter
import com.dontsu.dogs2.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val dogListAdapter = DogListAdapter(arrayListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        list_dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogListAdapter
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogs.observe(this, Observer {dogs ->
            dogs?.let {
                list_dogsList.visibility = View.VISIBLE
                dogListAdapter.updateDogList(dogs)
            }
        })
        viewModel.dogsLoadError.observe(this, Observer { error ->
            error?.let {
                list_listError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })
        viewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                list_loadingProgressBar.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    list_listError.visibility = View.GONE
                    list_dogsList.visibility = View.GONE
                }
            }
        })
    }

}
