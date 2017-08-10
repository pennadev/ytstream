package penna.kotarch

import android.arch.lifecycle.ViewModel
import com.google.api.services.youtube.model.SearchResult
import io.reactivex.Observable
import penna.kotarch.utils.YoutubeSearch

/**
 * Created by danpena on 8/3/17.
 */
class SearchViewModel : ViewModel() {

    val youtubeSearch = YoutubeSearch()

    fun query(q: String): Observable<SearchResult> {
        return Observable
                .fromCallable { youtubeSearch.search(q); }
                .flatMapIterable { it.items }
    }

    override fun onCleared() {
        super.onCleared()
    }

}