package com.example.socialastro.dialog

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.socialastro.R
import com.example.socialastro.model.ManeiraFilter
import com.example.socialastro.model.ModelFilter
import com.example.socialastro.model.OrderByFilter
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class DialogFilter(context: Context) : Dialog(context) {

    private lateinit var callback: (filter: ModelFilter) -> Unit
    private lateinit var modelFilter: ModelFilter
    private lateinit var filterCurrent: ModelFilter
    private lateinit var dateFilterAlfabetica: TextView
    private lateinit var dateFilterData: TextView
    private lateinit var dateFilterCrescente: TextView
    private lateinit var dateFilterDescrecente: TextView
    private lateinit var buttonAplicar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_filter)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        dateFilterAlfabetica = findViewById(R.id.dateFilterAlfabetica)
        dateFilterData = findViewById(R.id.dateFilterData)
        dateFilterCrescente = findViewById(R.id.dateFilterCrescente)
        dateFilterDescrecente = findViewById(R.id.dateFilterDescrecente)
        buttonAplicar = findViewById(R.id.buttonAplicar)
        modelFilter = filterCurrent
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setupButtons()
        setupInitialConfiguration()
    }

    private fun setupInitialConfiguration() {
        when (filterCurrent.order) {
            ManeiraFilter.DESCRECENTE -> {
                this.dateFilterDescrecente.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.blue
                    )
                )
            }
            ManeiraFilter.CRESCENTE -> {
                this.dateFilterCrescente.setTextColor(ContextCompat.getColor(context, R.color.blue))
            }
        }
        when (filterCurrent.tipo) {
            OrderByFilter.ALFABETICA -> {
                this.dateFilterAlfabetica.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.blue
                    )
                )
            }
            OrderByFilter.DATA -> {
                this.dateFilterData.setTextColor(ContextCompat.getColor(context, R.color.blue))
            }
        }
    }

    fun setInitialConfiguration(filter: ModelFilter) {
        this.filterCurrent = filter
    }

    fun setOnClickListener(callback: (filter: ModelFilter) -> Unit) {
        this.callback = callback
    }

    private fun setupButtons() {
        dateFilterAlfabetica.setOnClickListener {
            dateFilterAlfabetica.setTextColor(ContextCompat.getColor(context, R.color.blue))
            dateFilterData.setTextColor(ContextCompat.getColor(context, R.color.black))
            modelFilter.tipo = OrderByFilter.ALFABETICA
        }
        dateFilterData.setOnClickListener {
            dateFilterAlfabetica.setTextColor(ContextCompat.getColor(context, R.color.black))
            dateFilterData.setTextColor(ContextCompat.getColor(context, R.color.blue))
            modelFilter.tipo = OrderByFilter.DATA
        }
        dateFilterCrescente.setOnClickListener {
            dateFilterCrescente.setTextColor(ContextCompat.getColor(context, R.color.blue))
            dateFilterDescrecente.setTextColor(ContextCompat.getColor(context, R.color.black))
            modelFilter.order = ManeiraFilter.CRESCENTE
        }
        dateFilterDescrecente.setOnClickListener {
            dateFilterCrescente.setTextColor(ContextCompat.getColor(context, R.color.black))
            dateFilterDescrecente.setTextColor(ContextCompat.getColor(context, R.color.blue))
            modelFilter.order = ManeiraFilter.DESCRECENTE
        }
        buttonAplicar.setOnClickListener {
            callback(modelFilter)
            dismiss()
        }
    }
}