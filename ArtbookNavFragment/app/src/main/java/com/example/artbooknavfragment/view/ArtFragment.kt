package com.example.artbooknavfragment.view

import android.graphics.Color
import com.example.artbooknavfragment.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.artbooknavfragment.adapter.ArtAdapter
import com.example.artbooknavfragment.databinding.FragmentArtBinding
import com.example.artbooknavfragment.model.Art
import com.example.artbooknavfragment.roomdb.ArtDao
import com.example.artbooknavfragment.roomdb.ArtDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ArtFragment : Fragment() {
    private var _binding : FragmentArtBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: ArtDatabase
    private lateinit var artDao: ArtDao
    private val compositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            requireContext(),
            ArtDatabase::class.java,
            "Arts"
        ).build()

        artDao = database.artDao()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArtBinding.inflate(inflater , container , false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        toolbarMenu(view)
        getData()


    }


        //toolbar and menu
    private fun toolbarMenu(view : View) {

            binding.toolbar.title = "ART BOOK"
            binding.toolbar.setTitleTextColor(Color.YELLOW)
            binding.toolbar.background = null

        (requireActivity() as? MainActivity)?.setSupportActionBar(binding.toolbar)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu , menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menuArtAdd -> {
                        //Navigate and send args --> "new" and 0 id.
                        val action = ArtFragmentDirections.actionArtFragmentToUploadFragment("new" , 0)
                        view.findNavController().navigate(action)
                        true
                    }
                    else -> false
                }

            }
        }, viewLifecycleOwner)

    }

    private fun getData() {

        compositeDisposable.add(
            artDao.getArtNameAndId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )

    }

    private fun handleResponse(artList: List<Art>) {

        val artAdapter = ArtAdapter(artList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = artAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}