package penna.kotarch.ui.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.graphics.Palette
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.api.services.youtube.model.SearchResult
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.search_item.view.*
import penna.kotarch.R
import penna.kotarch.SearchViewModel
import penna.kotarch.getApp
import penna.kotarch.ui.activities.NowPlayingActivity

/**
 * Created by danpena on 8/15/17.
 */

class SearchItemsAdapter(private val viewModel: SearchViewModel) : RecyclerView.Adapter<SearchItemVh>() {
    val items = ArrayList<SearchResult>()

    val compositeDisposable = CompositeDisposable()

    override fun onViewDetachedFromWindow(holder: SearchItemVh?) {
        super.onViewDetachedFromWindow(holder)
        compositeDisposable.clear()
    }

    override fun onBindViewHolder(holder: SearchItemVh?, position: Int) {
        val searchResult = items[position]
        holder?.songTitle(searchResult.snippet.title)
        holder?.itemView?.playButton?.setOnClickListener {
            getApp(viewModel.getApplication()).playBackController.extractAndPlay(viewModel.getApplication(), searchResult.id.videoId)
        }

        holder?.itemView?.let {
            val bitmapListener: RequestListener<Drawable> = object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    compositeDisposable += Single.fromCallable { mutedColor(resource) }
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                        (holder.itemView as CardView?)?.setCardBackgroundColor(it)
                    }, {

                    })

                    return false
                }

                private fun mutedColor(resource: Drawable?): Int {
                    return Palette.from((resource as BitmapDrawable).bitmap)
                            .generate()
                            .getMutedColor(0)
                }

            }

            Glide.with(it)
                    .load(searchResult.snippet.thumbnails.medium.url)
                    .apply(RequestOptions.centerCropTransform())
                    .listener(bitmapListener)
                    .into(it.thumbImageView)

        }

        holder?.itemView?.setOnClickListener {
            val intent = Intent(it.context, NowPlayingActivity::class.java)

            intent.putExtra("url", searchResult.snippet.thumbnails.medium.url)
            intent.putExtra("title", searchResult.snippet.title)

            val create: Pair<View, String> = Pair.create(holder.itemView.thumbImageView, "content_image")
            val create2: Pair<View, String> = Pair.create(holder.itemView.songTitle, "content_title")

            val options = ActivityOptions.makeSceneTransitionAnimation(it.context as Activity,
                    create, create2)

            it.context.startActivity(intent, options.toBundle())
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
