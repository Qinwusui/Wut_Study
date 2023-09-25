package me.wtuer.study

import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.wtuer.study.app.BaseApp

fun Any?.loge() = Log.e("Study!:", this.toString())
private val scope= CoroutineScope(Dispatchers.IO)
fun Any?.toast(){
    scope.launch(Dispatchers.Main) {
        Toast.makeText(BaseApp.context,this@toast.toString(),Toast.LENGTH_SHORT).show()
    }
}