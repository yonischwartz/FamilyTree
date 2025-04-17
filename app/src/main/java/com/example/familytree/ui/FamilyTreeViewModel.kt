package com.example.familytree.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.io.File
import androidx.compose.runtime.State
import android.content.Context
import com.example.familytree.data.dataManagement.DatabaseManager

/**
 * ViewModel for managing the state of the family tree image file.
 *
 * This ViewModel stores the downloaded image of the family tree so it can
 * be accessed and displayed by Composables across the app. It survives
 * configuration changes like screen rotations.
 */
class FamilyTreeViewModel : ViewModel() {

    // Backing field for the family tree image file state
    private val _imageFile = mutableStateOf<File?>(null)

    /**
     * Public read-only access to the current image file state.
     * Observers (e.g. Composables) can use this to reactively update the UI.
     */
    val imageFile: State<File?> = _imageFile

    /**
     * Updates the stored image file in the ViewModel.
     *
     * @param file The [File] object representing the downloaded family tree image.
     */
    fun setImageFile(file: File) {
        _imageFile.value = file
    }

    /**
     * Initiates downloading the family tree image using the [DatabaseManager].
     * If the download succeeds or a cached file exists, the image is stored in the ViewModel state.
     *
     * @param context The application context used to access file storage.
     */
    fun downloadImage(context: Context) {
        DatabaseManager.downloadFamilyTreeImageToCache(context) { file ->
            if (file != null) {
                setImageFile(file)
            }
        }
    }
}
