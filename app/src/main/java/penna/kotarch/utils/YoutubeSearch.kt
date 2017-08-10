package penna.kotarch.utils

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse

import java.io.IOException

/**
 * Created by danpena on 8/1/17.
 */

class YoutubeSearch {

    private val youTube: YouTube = YouTube.Builder(AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(), null).build()

    private var search: YouTube.Search.List? = null

    init {
        try {
            search = youTube.search().list("id," + "snippet")
            search!!.fields = "items(id/kind," +
                    "id/videoId," +
                    "snippet/title," +
                    "snippet/thumbnails/default/url)"
            search!!.maxResults = 25L
            search!!.key = "AIzaSyATIXwq-s4xmzP0QKK1dCQDLHaT6Gdu4rU"
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun search(q: String): SearchListResponse? {
        search!!.q = q
        try {
            return search!!.execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}

