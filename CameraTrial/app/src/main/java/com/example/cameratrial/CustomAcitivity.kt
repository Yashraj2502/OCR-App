package com.example.cameratrial

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.chip.ChipGroup
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CustomAcitivity: AppCompatActivity() {
//    private lateinit var imageView: ImageView
//    private lateinit var btnSelectImage: Button
//    private lateinit var btnExtractText: Button
//    private lateinit var tvExtractedText: TextView

    private lateinit var ocrInterpreter: Interpreter
    private lateinit var detectionInterpreter: Interpreter
    private var detectionInputBuffer: TensorImage? = null
    private var detectionOutputBuffer: TensorBuffer? = null
    private var recognitionInputBuffer: TensorImage? = null
    private var recognitionOutputBuffer: TensorBuffer? = null
    private var width = 0
    private var height = 0
    private var modelInputSize = 0

    private lateinit var captureImg: ImageView
    private lateinit var detectButton: Button
    private lateinit var resultText: TextView
    private lateinit var snapButton: Button
    private lateinit var imageBitmap: Bitmap

    private lateinit var chipsGroup: ChipGroup
    private lateinit var ocrModel: OCRModelExecutor
//    private lateinit var ocrInterpreter: Interpreter
    private lateinit var viewModel: MLExecutionViewModel

    private val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 1
    private val REQUEST_CODE_PICK_IMAGE = 2


    companion object {
        private const val TAG = "MainActivity"
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        captureImg = findViewById(R.id.captureImage)
        snapButton = findViewById(R.id.BtnSnap)
        detectButton = findViewById(R.id.BtnDetect)
        resultText = findViewById(R.id.detectedText)

        snapButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        detectButton.setOnClickListener {
            extractTextFromImage()
        }

        // Find views by ID
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

        try {
            ocrInterpreter = Interpreter(
                FileUtil.loadMappedFile(this, "text_recognition.tflite"),
                Interpreter.Options().setNumThreads(4)
            )

            detectionInterpreter = Interpreter(
                FileUtil.loadMappedFile(this, "text_detection.tflite"),
                Interpreter.Options().setNumThreads(4)
            )

            val inputShape = detectionInterpreter.getInputTensor(0).shape()
            height = inputShape[1]
            width = inputShape[2]
            modelInputSize = inputShape[1]

            detectionInputBuffer = TensorImage(DataType.FLOAT32)
            detectionOutputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 6400, 4), DataType.FLOAT32)

            recognitionInputBuffer = TensorImage(DataType.FLOAT32)
            recognitionOutputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 25, 37), DataType.FLOAT32)

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing TensorFlow Lite interpreter.", e)
        }
    }

    private fun extractTextFromImage(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        // Load text detection model
        val textDetector = TextRecognition.getClient()

        // Run text detection
        textDetector.process(inputImage)
            .addOnSuccessListener { visionText ->
                val text = StringBuilder()
                for (block in visionText.textBlocks) {
                    val blockText = block.text
                    text.append(blockText)
                }

                // If text is found, run text recognition
                if (text.isNotEmpty()) {
                    // Load text recognition model
                    val ocrInterpreter = Interpreter(
                        FileUtil.loadMappedFile(this, "text_recognition.tflite"),
                        Interpreter.Options().setNumThreads(4)
                    )

                    // Preprocess image for text recognition
                    val processedBitmap = preprocessImage(bitmap)

                    // Run text recognition
                    val result = recognizeText(ocrInterpreter, processedBitmap)

                    // Update UI with recognized text
                    updateUI(result)
                } else {
                    Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Text detection failed: ${e.message}")
                Toast.makeText(this, "Text detection failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun recognizeText(interpreter: Interpreter, bitmap: Bitmap): String {
        // Preprocess image for text recognition
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(128, 128, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
        val tfImage = TensorImage(DataType.FLOAT32)
        tfImage.load(bitmap)
        val processedImage = imageProcessor.process(tfImage)

        // Run text recognition
        val input = arrayOf(processedImage.buffer)
        val output = Array(1) { ByteArray(MAX_TEXT_LENGTH) }
        interpreter.runForMultipleInputsOutputs(input, output)

        // Convert recognized text to string
        var result = ""
        for (i in output[0].indices) {
            if (output[0][i] == 0.toByte()) {
                break
            }
            result += output[0][i].toChar()
        }

        return result.trim()
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        // Preprocess image for text recognition
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(128, 128, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
        val tfImage = TensorImage(DataType.FLOAT32)
        tfImage.load(bitmap)
        return imageProcessor.process(tfImage).bitmap
    }

    private fun updateUI(text: String) {
        binding.textView.text = text
    }

}