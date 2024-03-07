package customtablegridview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.text.FieldPosition

class RowViewHolder(
    view: View,
    private val bind: (RowHeaderData, Int) -> Unit
) : RecyclerView.ViewHolder(view) {
    fun bindHolder(data: RowHeaderData, position: Int) {
        bind(data, position)
    }
}