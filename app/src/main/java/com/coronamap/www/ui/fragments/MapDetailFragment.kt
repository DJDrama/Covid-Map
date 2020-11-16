package com.coronamap.www.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.coronamap.www.R
import com.coronamap.www.databinding.FragmentMapDetailBinding
import okhttp3.internal.http.RequestLine

class MapDetailFragment : Fragment(R.layout.fragment_map_detail) {
    private val args: MapDetailFragmentArgs by navArgs()

    private var _binding: FragmentMapDetailBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapDetailBinding.bind(view)

        binding.apply {
            name.text = args.locationItem.name
            address.text = args.locationItem.address
            phone.text = args.locationItem.phone
            phone.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel: " + args.locationItem.phone)
                startActivity(intent)
            }
            if (args.locationItem.image!!.isEmpty())
                imageView.visibility = View.GONE
            else
                Glide.with(this@MapDetailFragment)
                    .load(args.locationItem.image)
                    .centerCrop()
                    .listener(object: RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            imageView.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    })
                    .into(imageView)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}