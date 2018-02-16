package penna.kotarch.ui.activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.afterTextChangeEvents
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import penna.kotarch.R
import penna.kotarch.SearchViewModel
import penna.kotarch.ui.adapters.SearchItemsAdapter
import penna.kotarch.utils.viewModel
import java.util.concurrent.TimeUnit

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.hide()

        list.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        val viewModel = viewModel<SearchViewModel>()
        val adapter = SearchItemsAdapter(viewModel)
        list.adapter = adapter

        disposable += searchbox.afterTextChangeEvents().subscribe { viewModel.query(it.editable().toString()) }
        disposable += viewModel
                .observeViewState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is SearchViewModel.ViewState.SearchCompleted -> {
                            adapter.setResults(it.searchResults)
                        }
                        is SearchViewModel.ViewState.SearchFailed -> TODO()
                    }
                }
    }



    override fun onResume() {
        super.onResume()

    }

    var disposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
