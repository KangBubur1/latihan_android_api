package com.example.submission_awal.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.submission_awal.databinding.FragmentDetailEventBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailEventFragment : Fragment() {

    private var _binding: FragmentDetailEventBinding? = null
    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DetailEventFragmentArgs by navArgs()
        val selectedEvent = args.event

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
            val doc = selectedEvent.description?.let { Jsoup.parse(it) }
            val descriptionText = doc?.body()?.text()

            withContext(Dispatchers.Main) {
                binding.tvTitle.text = selectedEvent.name
                binding.tvClock.text = "Date & Time: ${selectedEvent.beginTime}"
                binding.tvOwnerName.text = "Held By: ${selectedEvent.ownerName}"
                binding.tvQuota.text = "Quota: ${selectedEvent.quota?.minus(selectedEvent.registrants!!)}"
                binding.tvDescription.text = descriptionText

                val eventDate = selectedEvent.beginTime?.let { parseDate(it) }
                val currDate = Date()

                if(eventDate != null && currDate.after(eventDate)) {
                    binding.btnRegister.text = "Expired"
                    binding.btnRegister.isEnabled = false
                } else {
                    binding.btnRegister.text = "Register"
                    binding.btnRegister.isEnabled = true

                }

                Glide.with(this@DetailEventFragment)
                    .load(selectedEvent.mediaCover)
                    .into(binding.ivBanner)

                binding.btnRegister.setOnClickListener {
                    val url = selectedEvent.link

                    if (!url.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        try {
                            startActivity(intent) // Attempt to start the activity
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No application to handle this request", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Registration URL is not available", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}