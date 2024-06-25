package com.project.mycloudgalleryapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UploadActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var buttonChooseImage: Button
    private lateinit var buttonUpload: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        imageView = findViewById(R.id.imageView)
        buttonChooseImage = findViewById(R.id.button_choose_image)
        buttonUpload = findViewById(R.id.button_upload)
        progressBar = findViewById(R.id.progress_bar)

        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads")

        buttonChooseImage.setOnClickListener {
            openFileChooser()
        }

        buttonUpload.setOnClickListener {
            uploadImage()
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
            buttonUpload.visibility = android.view.View.VISIBLE
        }
    }

    private fun uploadImage() {
        if (imageUri != null) {
            progressBar.visibility = android.view.View.VISIBLE
            val fileReference = storageReference.child("uploads/" + System.currentTimeMillis() + ".jpg")
            fileReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val upload = ImageData(uri.toString(), System.currentTimeMillis())
                        val uploadId = databaseReference.push().key
                        if (uploadId != null) {
                            databaseReference.child(uploadId).setValue(upload)
                        }
                        progressBar.visibility = android.view.View.GONE
                        buttonUpload.visibility = android.view.View.GONE
                        Toast.makeText(this, "Successfully uploaded", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = android.view.View.GONE
                }
        }
    }


    private fun redirectToMainActivity() {
        val intent = Intent(this@UploadActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
