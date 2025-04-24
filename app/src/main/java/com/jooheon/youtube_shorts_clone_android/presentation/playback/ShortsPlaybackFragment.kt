//package com.jooheon.youtube_shorts_clone_android.presentation.playback
//
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.animation.AnimatorSet
//import android.animation.ObjectAnimator
//import android.graphics.drawable.Drawable
//import android.os.Build
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.annotation.OptIn
//import androidx.core.os.bundleOf
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.media3.common.Player
//import androidx.media3.common.util.UnstableApi
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.target.CustomTarget
//import com.bumptech.glide.request.transition.Transition
//import com.jooheon.youtube_shorts_clone_android.R
//import com.jooheon.youtube_shorts_clone_android.databinding.FragmentShortsPlaybackBinding
//import com.jooheon.youtube_shorts_clone_android.model.ShortsModel
//import com.jooheon.youtube_shorts_clone_android.player.ShortsPlaybackManager
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//class ShortsPlaybackFragment: Fragment() {
//    private var _binding: FragmentShortsPlaybackBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var viewModel: ShortsPlaybackViewModel
//    private lateinit var shortsPlaybackManager: ShortsPlaybackManager
//
//    companion object {
//        private const val ARG_MODEL = "model"
//        private const val FADE_ANIMATION_DURATION = 300L
//
//        fun newInstance(shortsModel: ShortsModel): ShortsPlaybackFragment {
//            return ShortsPlaybackFragment().apply {
//                arguments = bundleOf(
//                    ARG_MODEL to shortsModel
//                )
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        shortsPlaybackManager = ShortsPlaybackManager(
//            context = requireContext(),
//            lifecycle = lifecycle
//        ).also {
//            it.addListener(playerListener)
//        }
//
//        val model = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requireArguments().getParcelable(ARG_MODEL, ShortsModel::class.java)
//        } else {
//            (requireArguments().getParcelable(ARG_MODEL) as? ShortsModel)
//        }
//
//        viewModel = ViewModelProvider(this)[ShortsPlaybackViewModel::class.java].apply {
//            init(model)
//        }
//    }
//
//    @OptIn(UnstableApi::class)
//    private val playerListener = object : Player.Listener{
//        override fun onIsPlayingChanged(isPlaying: Boolean) {
//            super.onIsPlayingChanged(isPlaying)
//
//            if(isPlaying) binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
//            else binding.ivPlayPause.setImageResource(R.drawable.ic_play)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentShortsPlaybackBinding.inflate(inflater)
//        binding.lifecycleOwner = viewLifecycleOwner
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initListener()
//        observeViewModel()
//    }
//
//    private fun observeViewModel() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch {
//                    viewModel.shortFormModel.collectLatest {
//                        if(it == ShortsModel.default) return@collectLatest
//
//                        prepareVideo(it)
//                        setData(it)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun prepareVideo(model: ShortsModel) {
//        if(binding.videoFrame.player?.mediaItemCount.defaultZero() > 0)
//            return
//
//        val player = shortsPlaybackManager.getPlayer()
//
//        binding.videoFrame.player = player
//        binding.videoFrame.player?.apply {
//            setMediaItem(model.toMediaItem())
//            playWhenReady = false
//            prepare()
//        }
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun setData(model: ShortsModel) {
//        binding.content.tvTitle.text = model.title
//        binding.content.btPage.text = "See more (${model.creator})"
//
//        Glide.with(requireContext())
//            .load(model.thumbnail)
//            .into(
//                object : CustomTarget<Drawable>() {
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        transition: Transition<in Drawable>?
//                    ) {
//                        binding.videoFrame.defaultArtwork = resource
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) { /** nothing **/ }
//                }
//            )
//    }
//
//    private fun initListener() {
//        binding.btPlayPauseFull.setOnClickListener {
//            onPlayPauseAction()
//        }
//        binding.content.btPage.setOnClickListener {
//            // do something
//        }
//    }
//
//    private fun onPlayPauseAction() = viewLifecycleOwner.lifecycleScope.launch {
//        val isPlaying = binding.videoFrame.player?.isPlaying.defaultFalse()
//        val playWhenReady = binding.videoFrame.player?.playWhenReady.defaultFalse()
//
//        if(isPlaying || playWhenReady) binding.videoFrame.player?.pause()
//        else binding.videoFrame.player?.play()
//
//        fadeAnimation()
//    }
//
//    private fun fadeAnimation() {
//        binding.ivPlayPause.apply {
//            animate().cancel()
//            clearAnimation()
//
//            alpha = 0f
//            visibility = View.VISIBLE
//            val showAnim = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
//                duration = FADE_ANIMATION_DURATION
//                addListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationStart(animation: Animator) {
//                        // make ripple effect
//                        binding.btPlayPauseSmall.isPressed = true
//                        binding.btPlayPauseSmall.isPressed = false
//                    }
//                })
//            }
//            val hideAnim = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
//                duration = FADE_ANIMATION_DURATION
//            }
//
//            AnimatorSet().run {
//                playSequentially(showAnim, hideAnim)
//                start()
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        _binding = null
//        super.onDestroy()
//    }
//
//    private fun Boolean?.defaultFalse(): Boolean {
//        return this.default(false)
//    }
//
//    private fun Int?.defaultZero(): Int {
//        return this.default(0)
//    }
//
//    private fun Boolean?.default(default: Boolean): Boolean {
//        return this ?: default
//    }
//
//    fun Int?.default(default: Int): Int {
//        return this ?: default
//    }
//}