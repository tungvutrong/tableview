package customtablegridview

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener

class ItemHolder(
    private val view: RecyclerView,
    childViewHolder: (ViewGroup, Int) -> ItemChildHolder,
    scroll: (Int) -> Unit,
    onChildReady: (Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    init {
        view.layoutManager = CustomLinearLayoutManager(
            view.context, RecyclerView.HORIZONTAL, false
        ).apply {
            preventChildScrollAndDetectDirection(scroll)
        }
        view.addItemDecoration(LinearItemDecoration(10, RecyclerView.HORIZONTAL, false))
        view.adapter = ItemChildAdapter(childViewHolder, onChildReady)
    }

    fun bindHolder(data: ItemData, position: Int) {
        val adapter = view.adapter as? ItemChildAdapter
        adapter?.submitList(data.itemData())
    }
}