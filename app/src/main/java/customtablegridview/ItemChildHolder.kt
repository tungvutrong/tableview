package customtablegridview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemChildHolder(
    view: View,
    private val bind:(ItemChildData,Int)->Unit
) : RecyclerView.ViewHolder(view){

    fun bindHolder(data: ItemChildData, position: Int){
        bind(data,position)
    }
}