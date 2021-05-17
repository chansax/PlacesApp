package com.chansax.places.util

import android.Manifest

/**
 *  Created by chandan on 5/3/21
 **/


const val REQUEST_LOCATION_PERM = 0xabcd

val REQUIRED_LOC_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

fun Boolean.toInt() = if (this) 1 else 0
fun Int.toBoolean() = this != 0

sealed class FavoriteStatus {
    object Uninitialized : FavoriteStatus()
    object Regular : FavoriteStatus()
    object Favoured : FavoriteStatus()
}


fun FavoriteStatus.nextStatus(): FavoriteStatus {
    return when (this) {
        is FavoriteStatus.Uninitialized, is FavoriteStatus.Regular -> FavoriteStatus.Favoured
        is FavoriteStatus.Favoured -> FavoriteStatus.Regular
    }
}

fun FavoriteStatus.toBoolean(): Boolean {
    return when (this) {
        is FavoriteStatus.Uninitialized, is FavoriteStatus.Regular -> false
        is FavoriteStatus.Favoured -> true
    }
}

fun Boolean.toFavoriteStatus(): FavoriteStatus {
    return when (this) {
        false -> FavoriteStatus.Regular
        true -> FavoriteStatus.Favoured
    }
}
