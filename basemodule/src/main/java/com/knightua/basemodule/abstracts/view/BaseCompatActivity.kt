package com.knightua.basemodule.abstracts.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

abstract class BaseCompatActivity : AppCompatActivity(), BaseView {

    override fun getContext(): Context = this
}