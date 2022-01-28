package com.example.socialastro.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.socialastro.R

class DialogYesNo(context: Context) : Dialog(context) {

    private lateinit var callback: (button: String) -> Unit
    private lateinit var yes: CardView
    private lateinit var no: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_yes_no)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        yes = findViewById(R.id.buttonYes)
        no = findViewById(R.id.buttonNo)

        setupButtons()
    }

    fun setOnClickListener(callback : (button:String) -> Unit) {
        this.callback = callback
    }

    private fun setupButtons() {
        yes.setOnClickListener {
            callback("yes")
        }

        no.setOnClickListener {
            callback("no")
        }
    }
}