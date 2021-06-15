package com.example.mysoundclassification

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {
    var TAG = "MainActivity"

    // TODO 2.1: defines the model to be used
     var modelPath = "lite-model_yamnet_classification_tflite_1.tflite"

    // TODO 2.2: defining the minimum threshold
     var probabilityThreshold: Float = 0.3f

    val listofcommands = arrayListOf<String>()

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val REQUEST_RECORD_AUDIO = 1337
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)

        textView = findViewById<TextView>(R.id.output)
        val recorderSpecsTextView = findViewById<TextView>(R.id.textViewAudioRecorderSpecs)
        val appInfo = findViewById<TextView>(R.id.textViewInfo)
        val appDes = findViewById<TextView>(R.id.textViewDescription)

        // TODO 2.3: Loading the model from the assets folder
         val classifier = AudioClassifier.createFromFile(this, modelPath)

        // TODO 3.1: Creating an audio recorder
         val tensor = classifier.createInputTensorAudio()

        // TODO 3.2: showing the audio recorder specification
         val format = classifier.requiredTensorAudioFormat
         val recorderSpecs = "Number Of Channels: ${format.channels}\n" +
                "Sample Rate: ${format.sampleRate}"
         recorderSpecsTextView.text = recorderSpecs

        // TODO 3.3: Creating
         val record = classifier.createAudioRecord()
         record.startRecording()

        Timer().scheduleAtFixedRate(1, 500) {

            // TODO 4.1: Classifing audio data
             val numberOfSamples = tensor.load(record)
             val output = classifier.classify(tensor)
             //Log.d("mytag", numberOfSamples.toString())


            // TODO 4.2: Filtering out classifications with low probability
             val filteredModelOutput = output[0].categories.filter {
                 it.score > probabilityThreshold
             }

            // TODO 4.3: Creating a multiline string with the filtered results
            val outputStr =
                filteredModelOutput.sortedBy { -it.score }
                    .joinToString(separator = "\n") { "${it.label} -> ${it.score} " }

            // TODO 4.4: Updating the UI
            if (outputStr.isNotEmpty())
                runOnUiThread {
                    textView.text = outputStr
                }

            // TODO 5: Making open the browser with 2 finger snapping

            // Check if it's a finger snapping sound
            val filteredModelOutput2 = output[0].categories.filter {
                it.label.contains("Finger snapping") && it.score > probabilityThreshold
                        //|| it.label.contains("Clapping") && it.score > probabilityThreshold
            }

            if (filteredModelOutput.isNotEmpty())
                runOnUiThread {
                    if (filteredModelOutput[0].label.toString() == "Finger snapping")
                        if (listofcommands.size < 2)
                            listofcommands.add(filteredModelOutput[0].label.toString())
                        else
                            if (listofcommands[0] == "Finger snapping" && listofcommands[1] == "Finger snapping")
                                runOnUiThread{
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/")))
                                    listofcommands.clear()
                                }
                            else
                                runOnUiThread {
                                    listofcommands.clear()
                                }
                    else
                        //runOnUiThread{
                            listofcommands.clear()
                        //}
                }

            // TODO 6: showing the basic application info
            val infoAppRes = "TEC_ID: 2016005542\n" +
                    "Name of creator: Emmanuel Ledezma H \n" +
                    "Basic info: 2 finger snapping to open browser"
            appInfo.text = infoAppRes

            val appDescriptionRes =
                "If you finger snapping ones and then you do another sound the browser do not open. " +
                        "You need to finger snapping 2 times (do not FS to fast)"
            appDes.text = appDescriptionRes

        }
    }


    //CODIGO PARA TOMAR UNA FOTO
    /*val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent1() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    lateinit var currentPhotoPath: String

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    val REQUEST_TAKE_PHOTO = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }*/
}