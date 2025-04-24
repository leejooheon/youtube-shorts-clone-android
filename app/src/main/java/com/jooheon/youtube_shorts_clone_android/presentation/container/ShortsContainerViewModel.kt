//package com.jooheon.youtube_shorts_clone_android.presentation.container
//
//import android.content.Context
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.jooheon.youtube_shorts_clone_android.model.ShortsModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONException
//import org.json.JSONObject
//import java.io.BufferedReader
//import kotlin.random.Random
//import kotlin.random.nextUInt
//
//class ShortsContainerViewModel: ViewModel() {
//    private val _modelListState = MutableStateFlow<List<ShortsModel>>(emptyList())
//    val modelListState = _modelListState.asStateFlow()
//
//    internal fun loadData(context: Context) = viewModelScope.launch {
//        val models = withContext(Dispatchers.IO) {
////            delay(1234) // call some api
//            loadFromAssets(context)
//        }
//        _modelListState.emit(models)
//    }
//
//    private fun loadFromAssets(context: Context): List<ShortsModel> {
//        val raw = context.assets
//            .open("catalog.json")
//            .bufferedReader()
//            .use(BufferedReader::readText)
//        val jsonObject = JSONObject(raw)
//        val mediaList = jsonObject.getJSONArray("media")
//
//        val models = mutableListOf<ShortsModel>()
//        for(i in 0 until mediaList.length()) {
//            val mediaObject =  mediaList.getJSONObject(i)
//            val source = mediaObject.getString("source")
//
//            if(!source.contains(".mp3")) {
//                parseModel(mediaObject)?.let {
//                    models.add(it)
//                }
//            }
//        }
//
//        return models
//    }
//
//
//    private fun parseModel(mediaObject: JSONObject): ShortsModel? {
//        try {
//            val id = mediaObject.getString("id")
//            val title = mediaObject.getString("title")
//            val artist = mediaObject.getString("artist")
//            val image = mediaObject.getString("image")
//            val source = mediaObject.getString("source")
//
//            return ShortsModel(
//                id = id,
//                mediaUrl = source,
//                creator = artist,
//                title = title,
//                thumbnail = image,
//            )
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        return null
//    }
//}