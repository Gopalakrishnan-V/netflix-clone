package com.netflixclone.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.netflixclone.R
import com.netflixclone.adapters.PickerItemsAdapter
import com.netflixclone.data_models.PickerItem
import com.netflixclone.databinding.FragmentItemPickerBinding

private const val OPTIONS = "OPTIONS"
private const val SELECTED_INDEX = "SELECTED_INDEX"

class ItemPickerFragment : DialogFragment() {
    private lateinit var binding: FragmentItemPickerBinding
    private lateinit var options: List<String>
    private lateinit var pickerItemsAdapter: PickerItemsAdapter
    private var selectedIndex: Int = -1
    private var itemClickCallback: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            options = it.getStringArrayList(OPTIONS)!!
            selectedIndex = it.getInt(SELECTED_INDEX)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemPickerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }

    fun setItemClickListener(callback: ((Int) -> Unit)) {
        itemClickCallback = callback
    }

    private fun handleItemClick(_item: PickerItem, position: Int) {
        itemClickCallback?.invoke(position)
        dismiss()
    }

    private fun setupUI() {
        pickerItemsAdapter = PickerItemsAdapter(selectedIndex, this::handleItemClick)
        binding.optionsList.adapter = pickerItemsAdapter
        pickerItemsAdapter.submitList(options.map { PickerItem(it) })
        pickerItemsAdapter.notifyDataSetChanged()
        binding.content.setOnClickListener { dismiss() }
        binding.closeIcon.setOnClickListener { dismiss() }
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    companion object {
        @JvmStatic
        fun newInstance(options: ArrayList<String>, selectedIndex: Int) =
                ItemPickerFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList(OPTIONS, options)
                        putInt(SELECTED_INDEX, selectedIndex)
                    }
                }

    }
}