package penna.kotarch.ui.activities

import android.support.v7.app.AppCompatActivity
import com.github.ajalt.timberkt.Timber.d
import io.reactivex.disposables.CompositeDisposable

open class BaseActivity : AppCompatActivity() {

    open val onPauseDispose = CompositeDisposable()

    open fun getViewModel() {

    }

    override fun onPause() {
        super.onPause()
        d { "Disposed ${onPauseDispose.size()} subscriptions" }
        onPauseDispose.clear()
    }
}