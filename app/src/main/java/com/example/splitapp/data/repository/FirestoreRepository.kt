package com.example.splitapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore

abstract class FirestoreRepository {
    protected val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
}
