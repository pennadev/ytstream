package penna.kotarch.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ajalt.timberkt.d
import com.google.api.services.youtube.model.SearchResult
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.search_item.view.*
import penna.kotarch.R
import penna.kotarch.SearchViewModel
import penna.kotarch.extractors.Youtube
import penna.kotarch.extractors.getBestStream
import penna.kotarch.getApp
import penna.kotarch.ui.services.PlayingState

/**
 * Created by danpena on 8/15/17.
 */

class SearchItemsAdapter(private val viewModel: SearchViewModel) : RecyclerView.Adapter<SearchItemVh>() {
    val items = ArrayList<SearchResult>()


    override fun onBindViewHolder(holder: SearchItemVh?, position: Int) {
        val searchResult = items[position]
        holder?.songTitle(searchResult.snippet.title)
        holder?.itemView?.playButton?.setOnClickListener {
            getApp(viewModel.getApplication()).playBackController.extractAndPlay(viewModel.getApplication(), searchResult.id.videoId)
        }
    }

    fun addResult(res: SearchResult) {
        items.add(res)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchItemVh {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.search_item, parent, false)
        return SearchItemVh(view)
    }

    fun setResults(it: List<SearchResult>) {
        items.clear()
        items.addAll(it)
        notifyDataSetChanged()
    }
}

class SearchItemVh(v: View) : RecyclerView.ViewHolder(v) {
    fun songTitle(s: String) {
        itemView.songTitle.text = s
    }
}
