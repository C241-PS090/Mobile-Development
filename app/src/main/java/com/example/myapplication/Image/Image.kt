package com.example.myapplication.Image

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.*


fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${System.currentTimeMillis()}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}


fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    contentResolver.openInputStream(selectedImg)?.use { inputStream ->
        FileOutputStream(myFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return myFile
}

