package com.dontsu.dogs2.view

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.dontsu.dogs2.R
import com.dontsu.dogs2.databinding.FragmentDetailBinding
import com.dontsu.dogs2.databinding.SendSmsDialogBinding
import com.dontsu.dogs2.model.DogBreed
import com.dontsu.dogs2.model.DogPalette
import com.dontsu.dogs2.model.SmsInfo
import com.dontsu.dogs2.util.getProgressDrawable
import com.dontsu.dogs2.util.loadImage
import com.dontsu.dogs2.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var dataBinding: FragmentDetailBinding

    private var dogUuid = 0
    private var sendSmsStarted = false
    private var currentDog: DogBreed? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate<FragmentDetailBinding>(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid //DogListAdapter로 부터 받아온 uuid
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUuid)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogLiveData.observe(this, Observer { dog: DogBreed ->
            dog?.let {
                currentDog = dog
                /* data biding 쓰기전 코드
                detail_dogName.text = dog.dogBreed
                 detail_dogLifespan.text = dog.lifespan
                 detail_dogTemperament.text = dog.temperament
                 detail_dogPurpose.text= dog.bredFor
                 context?.let { detail_dogImage.loadImage(dog.imageUrl, getProgressDrawable(it)) }*/
                dataBinding.dog = dog

                it.imageUrl?.let {
                    setupBackgroundColor(it)
                }
            }
        })
    }

    private fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette: Palette? ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            dataBinding.palette = myPalette
                        }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> { //메세지 보내기
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> { //공유하기
                val intent = Intent(Intent.ACTION_SEND) //공유하고 싶을 때 쓰는 falg가 ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "강아지 정보 구경하세요!")
                intent.putExtra(Intent.EXTRA_TEXT,"강아지 이름 : ${currentDog?.dogBreed} \r\n 강아지 특성 : ${currentDog?.bredFor} \r\n ${currentDog?.imageUrl}")
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent, "공유하기"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo(
                    "",
                    "강아지 이름 : ${currentDog?.dogBreed} \r\n 강아지 특성 : ${currentDog?.bredFor}",
                    currentDog?.imageUrl
                )
                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )
                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("메세지 보내기") {dialog, which ->
                        if (!dialogBinding.smsDestination.text.isNullOrEmpty()) {
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("취소") {dialog, which ->  }
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }
}
