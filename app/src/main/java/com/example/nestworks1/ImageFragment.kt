package com.example.nestworks1

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ImageFragment : Fragment() {

    private lateinit var chooseImg: Button
    private lateinit var uploadImg: Button
    private lateinit var retrieveImg: Button
    private lateinit var imageView: ImageView
    private var fileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)

        chooseImg = view.findViewById(R.id.choose_image)
        uploadImg = view.findViewById(R.id.upload_image)
        retrieveImg = view.findViewById(R.id.retrive_image)
        imageView = view.findViewById(R.id.image_view)

        chooseImg.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Image to Upload"), 0
            )
        }

        uploadImg.setOnClickListener {
            if (fileUri != null) {
                uploadImage()
            } else {
                Toast.makeText(
                    requireContext(), "Please Select Image to Upload",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        retrieveImg.setOnClickListener {
            retrieveImage()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileUri)
                imageView.setImageBitmap(bitmap)

            } catch (e: Exception) {
                Log.e("Exception", "Error: " + e)
            }
        }
    }

    private fun uploadImage() {
        if (fileUri != null) {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Uploading Image...")
            progressDialog.setMessage("Processing...")
            progressDialog.show()

            val ref: StorageReference = FirebaseStorage.getInstance().getReference()
                .child("images")
            ref.putFile(fileUri!!).addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "File Uploaded Successfully", Toast.LENGTH_LONG)
                    .show()
            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "File Upload Failed...", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun retrieveImage() {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val imageReference: StorageReference = storageReference.child("images")

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Retrieving Image...")
        progressDialog.setMessage("Processing...")
        progressDialog.show()

        imageReference.downloadUrl.addOnSuccessListener { uri: Uri ->

            Glide.with(this)
                .load(uri)
                .into(imageView)

            progressDialog.dismiss()
            Toast.makeText(requireContext(),"Image Retrieved Successfully",Toast.LENGTH_LONG).show()
        }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(requireContext(),"Image Retrieve Failed: "+exception.message,Toast.LENGTH_LONG).show()

            }
    }
}
