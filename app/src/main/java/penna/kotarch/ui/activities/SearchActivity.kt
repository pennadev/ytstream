package penna.kotarch.ui.activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search.*
import penna.kotarch.R
import penna.kotarch.SearchViewModel
import penna.kotarch.ui.adapters.SearchItemsAdapter
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = SearchItemsAdapter(viewModel)
        list.adapter = adapter
        RxTextView.afterTextChangeEvents(searchbox)
                .throttleLast(2, TimeUnit.SECONDS)
                .flatMap { viewModel.query(it.editable().toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { adapter.setResults(it) }
    }
}
