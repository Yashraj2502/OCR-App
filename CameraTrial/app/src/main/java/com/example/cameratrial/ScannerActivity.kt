//package com.example.cameratrial
//
//import android.Manifest
//import android.content.ContentValues
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.res.ColorStateList
//import android.graphics.Bitmap
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import com.bumptech.glide.Glide
//import com.example.cameratrial.OCRModelExecutor.Companion.TAG
//import com.google.android.material.chip.Chip
//import com.google.android.material.chip.ChipGroup
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.text.TextRecognition
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions
//import kotlinx.coroutines.*
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//import org.tensorflow.lite.Interpreter
//import org.tensorflow.lite.support.common.FileUtil
//import java.util.concurrent.Executors
//
//class ScannerActivity : AppCompatActivity() {
////    private lateinit var captureImg: ImageView
////    private var resultText: TextView? = null
////    private var snapButton: Button? = null
////    private var detectButton: Button? = null
////    private var imageBitmap: Bitmap? = null
////
////    private lateinit var viewModel: MLExecutionViewModel
////
////    private lateinit var chipsGroup: ChipGroup
////    private var useGPU = false
////    private var selectedImageName = "tensorflow.jpg"
////    private var ocrModel: OCRModelExecutor? = null
////    private val inferenceThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
////    private val mainScope = MainScope()
////    private val mutex = Mutex()
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_scanner)
////
////        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(MLExecutionViewModel::class.java)
////        viewModel.resultingBitmap.observe(
////            this,
////            Observer { resultImage ->
////                if (resultImage != null) {
////                    updateUIWithResults(resultImage)
////                }
////                enableControls(true)
////            }
////        )
////
//////        captureImg = findViewById(R.id.captureImage);
//////        resultText = findViewById(R.id.detectedText);
////        captureImg = findViewById(R.id.captureImage)
////        resultText = findViewById(R.id.detectedText)
////        snapButton = findViewById(R.id.BtnSnap)
////        detectButton = findViewById(R.id.BtnDetect)
////        detectButton?.setOnClickListener(View.OnClickListener { detectText() })
////        snapButton?.setOnClickListener(View.OnClickListener {
////            if (checkPermission()) {
//////                captureImage()
////                enableControls(false)
////
////                mainScope.async(inferenceThread) {
////                    mutex.withLock {
////                        if (ocrModel != null) {
////                            viewModel.onApplyModel(baseContext, selectedImageName, ocrModel, inferenceThread)
////                        } else {
////                            Log.d(
////                                ContentValues.TAG,
////                                "Skipping running OCR since the ocrModel has not been properly initialized ..."
////                            )
////                        }
////                    }
////                }
////            } else {
////                requestPermission()
////            }
////        })
////    }
////
////    private fun checkPermission(): Boolean {
////        val cameraPermission =
////            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
////        return cameraPermission == PackageManager.PERMISSION_GRANTED
////    }
////
////    private fun requestPermission() {
////        val PERMISSION_CODE = 200
////        ActivityCompat.requestPermissions(
////            this,
////            arrayOf(Manifest.permission.CAMERA),
////            PERMISSION_CODE
////        )
////    }
////
////    private fun captureImage() {
////        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////        if (intent.resolveActivity(packageManager) != null) {
////            startActivityForResult(intent, REQUEST_CAMERA_CODE)
////            //            startActivity(intent);
//////            captureImg
////        }
////    }
////
////    override fun onRequestPermissionsResult(
////        requestCode: Int,
////        permissions: Array<String>,
////        grantResults: IntArray
////    ) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
////        if (grantResults.size > 0) {
////            val cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
////            if (cameraPermission) {
////                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
////                captureImage()
////            } else {
////                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
////
////    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////        super.onActivityResult(requestCode, resultCode, data)
////        if (requestCode == REQUEST_CAMERA_CODE || requestCode == RESULT_OK) {
////            val extras = data!!.extras
////            imageBitmap = extras!!["data"] as Bitmap?
////            captureImg!!.setImageBitmap(imageBitmap)
////        }
////    }
////
////    private fun detectText() {
////        val image = InputImage.fromBitmap(imageBitmap!!, 0)
////        //        TextRecognizer recognizer = TextRecognizer.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
////        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
////        if (textRecognizer != null) {
//////            val result = textRecognizer.process(image).addOnSuccessListener { text ->
//////                val result = StringBuilder()
//////                for (block in text.textBlocks) {
//////                    val blockText = block.text
//////                    val blockCornerPoint = block.cornerPoints
//////                    val blockFrame = block.boundingBox
//////                    for (line in block.lines) {
//////                        val lineText = line.text
//////                        val lineCornerPoint = line.cornerPoints
//////                        val lineRect = line.boundingBox
//////                        for (element in line.elements) {
//////                            val elementText = element.text
//////                            result.append(elementText)
//////                        }
//////                        resultText!!.text = blockText
//////                    }
//////                }
//////            }.addOnFailureListener { e ->
//////                Toast.makeText(
//////                    this@ScannerActivity,
//////                    "Failed To Detect Text From Image! " + e.message,
//////                    Toast.LENGTH_SHORT
//////                ).show()
//////            }
////        } else {
////            Toast.makeText(this, "TextRecognizer is Null", Toast.LENGTH_SHORT).show()
////        }
////    }
////
////    companion object {
////        private const val REQUEST_CAMERA_CODE = 100
////    }
////
////
////
////
////    //  private val tfImageName = "tensorflow.jpg"
//////  private val androidImageName = "android.jpg"
//////  private val chromeImageName = "chrome.jpg"
//////  private lateinit var viewModel: MLExecutionViewModel
//////  private lateinit var resultImageView: ImageView
//////  private lateinit var tfImageView: ImageView
//////  private lateinit var androidImageView: ImageView
//////  private lateinit var chromeImageView: ImageView
//////  private lateinit var chipsGroup: ChipGroup
//////  private lateinit var runButton: Button
//////  private lateinit var textPromptTextView: TextView
//////
//////  private var useGPU = false
//////  private var selectedImageName = "tensorflow.jpg"
//////  private var ocrModel: OCRModelExecutor? = null
//////  private val inferenceThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
//////  private val mainScope = MainScope()
//////  private val mutex = Mutex()
//////
//////  override fun onCreate(savedInstanceState: Bundle?) {
//////    super.onCreate(savedInstanceState)
//////    setContentView(R.layout.tfe_is_activity_main)
//////
//////    val toolbar: Toolbar = findViewById(R.id.toolbar)
//////    setSupportActionBar(toolbar)
//////    supportActionBar?.setDisplayShowTitleEnabled(false)
//////
//////    tfImageView = findViewById(R.id.tf_imageview)
//////    androidImageView = findViewById(R.id.android_imageview)
//////    chromeImageView = findViewById(R.id.chrome_imageview)
//////
//////    val candidateImageViews = arrayOf<ImageView>(tfImageView, androidImageView, chromeImageView)
//////
//////    val assetManager = assets
//////    try {
//////      val tfInputStream: InputStream = assetManager.open(tfImageName)
//////      val tfBitmap = BitmapFactory.decodeStream(tfInputStream)
//////      tfImageView.setImageBitmap(tfBitmap)
//////      val androidInputStream: InputStream = assetManager.open(androidImageName)
//////      val androidBitmap = BitmapFactory.decodeStream(androidInputStream)
//////      androidImageView.setImageBitmap(androidBitmap)
//////      val chromeInputStream: InputStream = assetManager.open(chromeImageName)
//////      val chromeBitmap = BitmapFactory.decodeStream(chromeInputStream)
//////      chromeImageView.setImageBitmap(chromeBitmap)
//////    } catch (e: IOException) {
//////      Log.e(TAG, "Failed to open a test image")
//////    }
//////
//////    for (iv in candidateImageViews) {
//////      setInputImageViewListener(iv)
//////    }
//////
//////    resultImageView = findViewById(R.id.result_imageview)
//////    chipsGroup = findViewById(R.id.chips_group)
//////    textPromptTextView = findViewById(R.id.text_prompt)
//////    val useGpuSwitch: Switch = findViewById(R.id.switch_use_gpu)
//////
//////    viewModel = AndroidViewModelFactory(application).create(MLExecutionViewModel::class.java)
//////    viewModel.resultingBitmap.observe(
//////      this,
//////      Observer { resultImage ->
//////        if (resultImage != null) {
//////          updateUIWithResults(resultImage)
//////        }
//////        enableControls(true)
//////      }
//////    )
//////
//////    mainScope.async(inferenceThread) { createModelExecutor(useGPU) }
//////
//////    useGpuSwitch.setOnCheckedChangeListener { _, isChecked ->
//////      useGPU = isChecked
//////      mainScope.async(inferenceThread) { createModelExecutor(useGPU) }
//////    }
//////
//////    runButton = findViewById(R.id.rerun_button)
//////    runButton.setOnClickListener {
//////      enableControls(false)
//////
//////      mainScope.async(inferenceThread) {
//////        mutex.withLock {
//////          if (ocrModel != null) {
//////            viewModel.onApplyModel(baseContext, selectedImageName, ocrModel, inferenceThread)
//////          } else {
//////            Log.d(
//////              TAG,
//////              "Skipping running OCR since the ocrModel has not been properly initialized ..."
//////            )
//////          }
//////        }
//////      }
//////    }
//////
//////    setChipsToLogView(HashMap<String, Int>())
//////    enableControls(true)
//////  }
////
//////  @SuppressLint("ClickableViewAccessibility")
//////  private fun setInputImageViewListener(iv: ImageView) {
//////    iv.setOnTouchListener(
//////      object : View.OnTouchListener {
//////        override fun onTouch(v: View, event: MotionEvent?): Boolean {
//////          if (v.equals(tfImageView)) {
//////            selectedImageName = tfImageName
//////            textPromptTextView.setText(getResources().getString(R.string.tfe_using_first_image))
//////          } else if (v.equals(androidImageView)) {
//////            selectedImageName = androidImageName
//////            textPromptTextView.setText(getResources().getString(R.string.tfe_using_second_image))
//////          } else if (v.equals(chromeImageView)) {
//////            selectedImageName = chromeImageName
//////            textPromptTextView.setText(getResources().getString(R.string.tfe_using_third_image))
//////          }
//////          return false
//////        }
//////      }
//////    )
//////  }
////
////    private suspend fun createModelExecutor(useGPU: Boolean) {
////        mutex.withLock {
////            if (ocrModel != null) {
////                ocrModel!!.close()
////                ocrModel = null
////            }
////            try {
////                ocrModel = OCRModelExecutor(this, useGPU)
////            } catch (e: Exception) {
////                Log.e(ContentValues.TAG, "Fail to create OCRModelExecutor: ${e.message}")
////                val logText: TextView = findViewById(R.id.detectedText)
////                logText.text = e.message
////            }
////        }
////    }
////
////    private fun setChipsToLogView(itemsFound: Map<String, Int>) {
////        chipsGroup.removeAllViews()
////
////        for ((word, color) in itemsFound) {
////            val chip = Chip(this)
////            chip.text = word
////            chip.chipBackgroundColor = getColorStateListForChip(color)
////            chip.isClickable = false
////            chipsGroup.addView(chip)
////        }
////        chipsGroup.parent.requestLayout()
////    }
////
////    private fun getColorStateListForChip(color: Int): ColorStateList {
////        val states =
////            arrayOf(
////                intArrayOf(android.R.attr.state_enabled), // enabled
////                intArrayOf(android.R.attr.state_pressed) // pressed
////            )
////
////        val colors = intArrayOf(color, color)
////        return ColorStateList(states, colors)
////    }
////
////    private fun setImageView(imageView: ImageView, image: Bitmap) {
////        Glide.with(baseContext).load(image).override(250, 250).fitCenter().into(imageView)
////    }
////
////    private fun updateUIWithResults(modelExecutionResult: ModelExecutionResult) {
////        setImageView(captureImg, modelExecutionResult.bitmapResult)
////        val logText: TextView = findViewById(R.id.detectedText)
////        logText.text = modelExecutionResult.executionLog
////
////        setChipsToLogView(modelExecutionResult.itemsFound)
////        enableControls(true)
////    }
////
////    private fun enableControls(enable: Boolean) {
////        detectButton?.isEnabled = enable
////    }
//
//    private lateinit var captureImg: ImageView
//    private lateinit var detectButton: Button
//    private lateinit var chipsGroup: ChipGroup
//
//    private lateinit var ocrModel: OCRModelExecutor
//
//    private lateinit var ocrInterpreter: Interpreter
//
//
//    private lateinit var resultText: TextView
//    private lateinit var snapButton: Button
//    private lateinit var imageBitmap: Bitmap
//
//    private lateinit var viewModel: MLExecutionViewModel
//
//
////    private var useGPU = false
////    private val inferenceThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
////    private val mainScope = MainScope()
////    private val mutex = Mutex()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_scanner)
////
////        captureImg = findViewById(R.id.captureImage)
////        detectButton = findViewById(R.id.BtnDetect)
////        resultText = findViewById(R.id.detectedText)
////        snapButton = findViewById(R.id.BtnSnap)
////
////        detectButton.setOnClickListener {
////            enableControls(false)
////            detectText()
////        }
////
////        // Initialize OCR model executor
////        lifecycleScope.launch(Dispatchers.IO) {
////            createModelExecutor(useGPU = true)
////        }
//
//            super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_scanner)
//
//            // Load the OCR model using TensorFlow Lite
//            try {
//                val modelFile = FileUtil.loadMappedFile(this, "ocr_model.tflite")
//                ocrInterpreter = Interpreter(modelFile, Interpreter.Options().setNumThreads(4))
//            } catch (e: Exception) {
//                Log.e(TAG, "Failed to load OCR model: ${e.message}")
//                Toast.makeText(this, "Failed to load OCR model", Toast.LENGTH_SHORT).show()
//                finish()
//                return
//            }
//
//            // Find views by ID
//            captureImg = findViewById(R.id.captureImage)
//            detectButton = findViewById(R.id.BtnDetect)
//            resultText = findViewById(R.id.detectedText)
//            snapButton = findViewById(R.id.BtnSnap)
//
//            // Set up click listener for button
//            detectButton.setOnClickListener {
//                enableControls(false)
//                detectText()
//            }
//
//            // Initialize OCR model executor
//            lifecycleScope.launch(Dispatchers.IO) {
//                createModelExecutor(useGPU = true)
//            }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
//            data?.extras?.get("data")?.let { capturedImage ->
//                val imageBitmap = capturedImage as Bitmap
//                detectText(imageBitmap)
//            }
//        }
//    }
//
//    private fun detectText() {
//        val image = InputImage.fromBitmap(imageBitmap!!, 0)
//        val recognizer = TextRecognition.getClient()
//
//        recognizer.process(image)
//            .addOnSuccessListener { visionText ->
//                val result = StringBuilder()
//                for (block in visionText.textBlocks) {
//                    val blockText = block.text
//                    val blockCornerPoint = block.cornerPoints
//                    val blockFrame = block.boundingBox
//                    for (line in block.lines) {
//                        val lineText = line.text
//                        val lineCornerPoint = line.cornerPoints
//                        val lineRect = line.boundingBox
//                        for (element in line.elements) {
//                            val elementText = element.text
//                            result.append(elementText)
//                        }
//                    }
//                }
//                resultText!!.text = result.toString()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(
//                    this@ScannerActivity,
//                    "Failed To Detect Text From Image! " + e.message,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
//
//    private fun extractTextFromBitmap(bitmap: Bitmap): String {
//        val inputTensor = getInputTensor(bitmap)
//        val outputTensor = getOutputTensor()
//        ocrInterpreter.run(inputTensor.buffer, outputTensor.buffer.rewind())
//
//        val scores = outputTensor.buffer.rewind().asFloatBuffer()
//        val text = StringBuilder()
//
//        for (i in 0 until scores.limit() step CHARSET_SIZE) {
//            val slice = scores.slice()
//            slice.limit(CHARSET_SIZE)
//            val charIndex = argmax(slice)
//            if (charIndex != 0) {
//                text.append(CHARSET[charIndex])
//            }
//        }
//
//        return text.toString()
//    }
//
//    private fun getInputTensor(bitmap: Bitmap): TensorImage {
//        val tensorImage = TensorImage(DataType.FLOAT32)
//        tensorImage.load(bitmap)
//        tensorImage.invertColors()
//        tensorImage.scale(1f / 255f, 1f / 255f)
//        tensorImage.cropCenter()
//        tensorImage.resize(INPUT_SIZE, INPUT_SIZE)
//        return tensorImage
//    }
//
//    private fun getOutputTensor(): TensorBuffer {
//        return TensorBuffer.createFixedSize(intArrayOf(OUTPUT_SIZE), DataType.FLOAT32)
//    }
//
//    private fun argmax(array: FloatBuffer): Int {
//        var maxIndex = -1
//        var maxValue = Float.MIN_VALUE
//        for (i in 0 until array.limit()) {
//            val value = array.get(i)
//            if (value > maxValue) {
//                maxIndex = i
//                maxValue = value
//            }
//        }
//        return maxIndex
//    }
//
//}