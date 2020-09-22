package com.metropolia.sensorproject.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class UserModel(application: Application): AndroidViewModel(application) {
    private val users: LiveData<List<User>> = UserDB.get(getApplication()).userDao().getAll()
    private val count: LiveData<Int> = UserDB.get(getApplication()).userDao().getDataCount()
    fun getUsers() = users
    fun getCount() = count
}