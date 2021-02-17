package com.bd.firebasedb

data class Artist(var artistID: String, var artistName: String, var artistgenre: String) {
    constructor() : this("", "", "") {}
}
