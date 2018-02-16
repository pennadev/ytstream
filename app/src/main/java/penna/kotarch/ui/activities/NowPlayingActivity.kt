package penna.kotarch.ui.activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_now_playing.*
import penna.kotarch.R

class NowPlayingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_playing)

        Glide.with(this)
                .load(intent.getStringExtra("url"))
                .apply(RequestOptions.centerCropTransform())
                .into(content_image)
//        supportActionBar?.setBackgroundDrawable(ColorDrawable(intent.getIntExtra()))
    }
}
