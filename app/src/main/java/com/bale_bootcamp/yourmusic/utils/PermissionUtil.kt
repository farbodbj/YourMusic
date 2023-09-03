package com.bale_bootcamp.yourmusic.utils

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

object PermissionUtil {
    fun Fragment.checkAndAskPermission(permission: String, onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit) {
        if(requireActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) onPermissionGranted()
                else onPermissionDenied()
            }.launch(permission)
        }
        else {
            onPermissionGranted()
        }
    }
}