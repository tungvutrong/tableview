package customtablegridview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemChildHolder(
    view: View,
    private val bind:(ItemChildData,Int)->Unit,
    private val onChildFocus:()->Unit
) : RecyclerView.ViewHolder(view){

    init {
        view.setOnFocusChangeListener { _, b ->
            if(b){
                onChildFocus()
            }
        }
    }

    fun bindHolder(data: ItemChildData, position: Int){
        bind(data,position)
    }
}