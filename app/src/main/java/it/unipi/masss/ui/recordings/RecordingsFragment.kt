package it.unipi.masss.ui.recordings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import it.unipi.masss.project.R
import it.unipi.masss.project.databinding.FragmentRecordingsBinding
import java.io.File
import AudioItem

import android.media.MediaPlayer
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton


class RecordingsFragment : Fragment() {

    private var _binding: FragmentRecordingsBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlaying: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecordingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val audio_path = context?.filesDir?.path + "/"
        Log.d("Recordings", "audiopath: $audio_path")


        val directory = File(audio_path)
        val audioListLayout = view.findViewById<LinearLayout>(R.id.audioList)
        val scrollViewLayout = view.findViewById<ScrollView>(R.id.scrollPage)
        val audioItems = mutableMapOf<String, AudioItem>()


        // Get an array of files in the directory
        val files = directory.listFiles()
        Log.d("Recordings", "FILES: $files")

        // Iterate over the files and print their names
        files?.forEach { file ->
            if (file.isFile && file.name.startsWith("recording_")) {
                val audioItem = AudioItem(requireContext(), file.name, file.name)
                audioListLayout.addView(audioItem)
                audioListLayout.requestLayout()
                scrollViewLayout.requestLayout()

                audioItems[file.name] = audioItem

                audioItem.findViewById<FloatingActionButton>(R.id.playButton).setOnClickListener {
                    // If the audio is playing, stop it
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null

                        val playingAudioItem = audioItems[currentlyPlaying]
                        val playingButton = playingAudioItem?.findViewById<FloatingActionButton>(R.id.playButton)
                        playingButton?.setImageResource(android.R.drawable.ic_media_play)
                    }
                    if(currentlyPlaying != file.name){
                        // Otherwise, start playing the audio
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(audio_path + file.name)
                            prepare()
                            start()

                            setOnCompletionListener {
                                val playingAudioItem = audioItems[currentlyPlaying]
                                val playingButton = playingAudioItem?.findViewById<FloatingActionButton>(R.id.playButton)
                                playingButton?.setImageResource(android.R.drawable.ic_media_play)
                                currentlyPlaying = null
                            }
                        }
                        currentlyPlaying = file.name

                        Log.d("Recordings", "PEFFORZA")
                        val my_id = file.name.hashCode()
                        Log.d("Recordings", "id: $my_id")


                        val currentAudioItem = audioItems[file.name]
                        Log.d("Recordings", "currentAudioItem: $currentAudioItem")

                        val playButton = currentAudioItem?.findViewById<FloatingActionButton>(R.id.playButton)

                        Log.d("Recordings", "PLAYBUTTON : $playButton")
                        playButton?.setImageResource(android.R.drawable.ic_media_pause)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
