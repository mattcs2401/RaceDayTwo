package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.adapter.RaceMeetingAdapter
import com.mcssoft.racedaytwo.databinding.MainFragmentBinding
import com.mcssoft.racedaytwo.interfaces.IDeleteAll
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import com.mcssoft.racedaytwo.ui.dialog.DeleteAllDialog
import com.mcssoft.racedaytwo.utiliy.RaceDayBackPressCB
import com.mcssoft.racedaytwo.utiliy.RaceDayUtilities
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(), IDeleteAll, MaterialButtonToggleGroup.OnButtonCheckedListener {

    @Inject lateinit var mainViewModel: RaceDayViewModel
    @Inject lateinit var raceAdapter: RaceMeetingAdapter
    @Inject lateinit var raceDayUtilities: RaceDayUtilities
    @Inject lateinit var raceDayPreferences: RaceDayPreferences

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d("TAG","MainFragment.onCreateView")

        return MainFragmentBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("TAG","MainFragment.onViewCreated")

        binding = MainFragmentBinding.bind(view)

        // Setup the UI and related components.
        initialise()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("TAG","MainFragment.onActivityCreated")

        // Init back press handler callback.
        raceDayBackPressCallback = RaceDayBackPressCB(true )
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG","MainFragment.onStart")

        // Add on back pressed handler.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, raceDayBackPressCallback)
    }

    override fun onStop() {
        Log.d("TAG","MainFragment.onStop")

        // Remove back press handler callback.
        raceDayBackPressCallback.removeCallback()

        // Super.
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("TAG","MainFragment.onDestroyView")
        binding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d("TAG","MainFragment.onCreateOptionsMenu")

        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.id_menu_item_settings -> {
                // Navigate to MainFragment.
                Navigation.findNavController(requireActivity(), R.id.id_nav_host_fragment)
                    .navigate(R.id.action_mainFragment_to_preferencesFragment)
            }
            R.id.id_menu_item_delete_all -> {
                val dialog = DeleteAllDialog(this)
                dialog.show(parentFragmentManager, "")
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onButtonChecked(group: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean) {
        //TODO("Not yet implemented")
        when(checkedId) {
            binding?.idBtnRace?.id -> {
                Toast.makeText(requireContext(), "Race button clicked.", Toast.LENGTH_SHORT).show()
            }
            binding?.idBtnTrots?.id -> {
                Toast.makeText(requireContext(), "Trots button clicked.", Toast.LENGTH_SHORT).show()
            }
            binding?.idBtnGreyhound?.id -> {
                Toast.makeText(requireContext(), "Greyhound button clicked.", Toast.LENGTH_SHORT).show()
            }
            else -> { val bp = "bp"}
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Interface-IDeleteAll">
    override fun deleteAll(deleteAll: Boolean) {
        when(deleteAll) {
            true -> {
                raceDayUtilities.deleteFromStorage(File(raceDayUtilities.getPrimaryStoragePath()))
                mainViewModel.clearCache()
                Navigation.findNavController(requireActivity(), R.id.id_nav_host_fragment)
                    .navigate(R.id.action_mainFragment_to_splashFragment)
            }
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    private fun initialise() {
        // Fragment title in action/tool bar.
        setUIComponents()

        setHasOptionsMenu(true)

        setRecyclerView()

        setViewModel()
    }

    private fun setViewModel() {
//        mainViewModel.meetings?.observe(viewLifecycleOwner) { mtgs ->
//            raceAdapter.submitList(mtgs)
//        }
    }

    private fun setRecyclerView() {
        // There will only be the number of meetings as parsed from the download file.
        binding?.idRecyclerView?.setHasFixedSize(true)

        // Add dividers between row items.
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding?.idRecyclerView?.addItemDecoration(decoration)

        // Set the recycler view.
        binding?.idRecyclerView?.adapter = raceAdapter

        // Hide the progress bar when the layout is complete.
        binding?.idRecyclerView?.layoutManager = object : LinearLayoutManager(requireActivity(), VERTICAL, false) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                binding?.idProgressBar2?.visibility = View.GONE
            }
        }
    }

    private fun setUIComponents() {
        // Set the title in the toolbar.
        requireActivity().findViewById<Toolbar>(R.id.id_toolbar)?.title = "Race Day"
        // Set the toggle group listener.
        binding?.idToggleGroup?.addOnButtonCheckedListener(this)
    }
    //</editor-fold>

    // UI components.
    private var binding : MainFragmentBinding? = null

    // Callback to block the user from pressing back (otherwise will reload the SplashFragment).
    private lateinit var raceDayBackPressCallback : RaceDayBackPressCB
}