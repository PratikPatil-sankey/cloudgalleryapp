package com.project.mycloudgalleryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.project.mycloudgalleryapp.databinding.ActivityFullscreenImageBinding

class FullscreenImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenImageBinding
    private lateinit var images: ArrayList<ImageData>
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        images = intent.getParcelableArrayListExtra<ImageData>("images") ?: ArrayList()
        currentPosition = intent.getIntExtra("position", 0)

        val adapter = ViewPagerAdapter()
        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = currentPosition
        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        // Add dots dynamically
        addDotsIndicator(currentPosition)
    }

    private val viewPagerPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            addDotsIndicator(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun addDotsIndicator(currentPosition: Int) {
        binding.layoutDots.removeAllViews()

        val dots = arrayOfNulls<ImageView>(images.size)
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dots[i]?.apply {
                setImageResource(if (i == currentPosition) R.drawable.dot_active else R.drawable.dot_inactive)
                layoutParams = params
                setPadding(8, 0, 8, 0)
                binding.layoutDots.addView(this)
            }
        }
    }

    inner class ViewPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = LayoutInflater.from(container.context)
            val view = layoutInflater.inflate(R.layout.item_fullscreen_image, container, false)

            val imageView = view.findViewById<ImageView>(R.id.fullscreen_image_view)
            Glide.with(container.context)
                .load(images[position].imageUrl)
                .into(imageView)

            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
