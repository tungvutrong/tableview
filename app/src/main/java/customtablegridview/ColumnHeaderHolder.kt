package customtablegridview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ColumnHeaderHolder(
    view: View,
    private val bind:(ColumnHeaderData,Int)->Unit
) : RecyclerView.ViewHolder(view){
    fun bindHolder(data: ColumnHeaderData, position: Int){
        bind(data,position)
    }
}