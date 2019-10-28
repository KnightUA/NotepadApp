package com.knightua.basemodule.abstracts.view

import android.content.Context
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(), BaseView {

    override fun getContext(): Context = activity as Context
}