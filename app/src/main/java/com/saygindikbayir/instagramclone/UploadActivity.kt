package com.saygindikbayir.instagramclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload.*
import java.lang.Exception
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap

class UploadActivity : AppCompatActivity() {

    var selectedPicture : Uri? = null
    private lateinit var db :FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun uploadClicked(view: View) {

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpeg"

        val storage = FirebaseStorage.getInstance()
        val reference = storage.reference
        val imagesReference = reference.child("images").child(imageName)

        if(selectedPicture !=  null){
            imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->

                //Database lines
                val uploadedPictureReferance = FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedPictureReferance.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    val postHashMap = hashMapOf<String,Any>()
                    postHashMap.put("downloadUrl",downloadUrl)
                    postHashMap.put("userEmail",auth.currentUser!!.email.toString())
                    postHashMap.put("comment",uploadCommentText.text.toString())
                    postHashMap.put("date",com.google.firebase.Timestamp.now())

                    db.collection("Posts").add(postHashMap).addOnCompleteListener { task ->

                        if (task.isComplete && task.isSuccessful){
                            val intent = Intent(applicationContext,TimelineActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage.toString(),Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    fun selectImageClicked(view:View){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),200)
        } else{
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,201)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,201)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 201 && resultCode == RESULT_OK && data != null){
            try {
                selectedPicture = data.data

                if (selectedPicture != null) {

                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(contentResolver, selectedPicture!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        selectImage.setImageBitmap(bitmap)

                    } else {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                        selectImage.setImageBitmap(bitmap)
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}