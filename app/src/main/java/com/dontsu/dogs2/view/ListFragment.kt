package com.dontsu.dogs2.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
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
        setHasOptionsMenu(true) //옵션 만들어주기 위해 true
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh() //프래그먼트가 새로 켜지면 조건에따라 레트로핏이나 데이터베이스로부터 강아지정보 가져옴

        list_dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogListAdapter
        }

        list_refreshLayout.setOnRefreshListener {
            list_dogsList.visibility = View.GONE
            list_listError.visibility = View.GONE
            list_loadingProgressBar.visibility =View.VISIBLE
            viewModel.refreshBypassCache() //리프래시하면 무조건 레트로핏으로부터 강아지정보 가져옴. bypass: 우회하다 라는 뜻
            list_refreshLayout.isRefreshing = false
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId) {
           R.id.actionSettings -> {
               view?.let {Navigation.findNavController(it).navigate(ListFragmentDirections.actionSettings())  }
           }
       }
        return super.onOptionsItemSelected(item)
    }

}
