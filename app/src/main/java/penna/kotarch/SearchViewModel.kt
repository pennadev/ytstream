package penna.kotarch

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.google.api.services.youtube.model.SearchResult
import io.reactivex.Observable
import penna.kotarch.extractors.Stream
import penna.kotarch.utils.YoutubeSearch

/**
 * Created by danpena on 8/3/17.
 */
class SearchViewModel(app: Application) : AndroidViewModel(app) {


    private val youtubeSearch = YoutubeSearch()


    fun query(q: String): Observable<List<SearchResult>> {
        return Observable
                .fromCallable { youtubeSearch.search(q)?.items; }
    }

    fun play(stream: Stream) {
        Bus.playStream.onNext(stream)
    }

    override fun onCleared() {
        super.onCleared()
    }
}