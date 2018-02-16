package penna.kotarch

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.view.View
import com.google.api.services.youtube.model.SearchResult
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import penna.kotarch.utils.YoutubeSearch

/**
 * Created by danpena on 8/3/17.
 */
class SearchViewModel(app: Application) : AndroidViewModel(app) {

    private val youtubeSearch = YoutubeSearch()
    private val viewStateSubject: BehaviorSubject<ViewState> = BehaviorSubject.create()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    sealed class ViewState() {
        data class SearchCompleted(val searchResults: List<SearchResult>) : SearchViewModel.ViewState()
        data class SearchFailed(val errorMessage: String) : SearchViewModel.ViewState()
    }

    fun query(q: String) {
        compositeDisposable += Single
                .fromCallable { youtubeSearch.search(q).items; }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    searchCompleted(it)
                }, {
                    searchError(it)
                })
    }

    private fun searchError(it: Throwable?) {

    }

    private fun searchCompleted(searchResults: List<SearchResult>) {
        viewStateSubject.onNext(ViewState.SearchCompleted(searchResults))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun observeViewState(): Flowable<ViewState> {
        return viewStateSubject.toFlowable(BackpressureStrategy.LATEST)
    }

}