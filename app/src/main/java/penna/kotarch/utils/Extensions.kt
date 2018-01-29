package penna.kotarch.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import penna.kotarch.ui.activities.BaseActivity

inline fun <reified VM : ViewModel> BaseActivity.viewModel(): VM {
    return ViewModelProviders.of(this).get(VM::class.java)
}

