package penna.kotarch.ui.activities

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_search.*
import penna.kotarch.R
import penna.kotarch.SearchViewModel
import penna.kotarch.ui.adapters.SearchItemsAdapter
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        list.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        
    }

    var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onResume() {
        super.onResume()
        val viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        val adapter = SearchItemsAdapter(viewModel)
        list.adapter = adapter

        onPauseDispose += RxTextView.afterTextChangeEvents(searchbox)
                .debounce(2, TimeUnit.SECONDS)
                .flatMap { viewModel.query(it.editable().toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .share()
                .subscribe { adapter.setResults(it) }

    }
}
