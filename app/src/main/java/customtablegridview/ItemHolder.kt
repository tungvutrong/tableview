package customtablegridview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tablegridview.R

class ItemHolder(
    view: View,
    childViewHolder: (ViewGroup, Int, ()->Unit) -> ItemChildHolder,
    onChildScroll: (View, Int, Int, Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    private var rcv: RecyclerView = view.findViewById(R.id.rcv_item)
    private var positionCellIndex = 0
    private var positionRowIndex = 0

    init {
        rcv.layoutManager = CustomLinearLayoutManager(
            context = view.context,
            orientation = RecyclerView.HORIZONTAL,
            reverseLayout = false
        ).apply {
            horizontalItemFocus {
                positionCellIndex = it
            }
        }
        rcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutPosition == positionRowIndex) {
                    onChildScroll(rcv, dx, dy, positionCellIndex)
                }
            }
        })
        rcv.addItemDecoration(LinearItemDecoration(10, RecyclerView.HORIZONTAL, false))
        rcv.adapter = ItemChildAdapter(childViewHolder){
            positionRowIndex = layoutPosition
        }
    }

    fun availableIndex(pos: Int) {
        try {
            val lm = rcv.layoutManager as? CustomLinearLayoutManager
            lm?.scrollToPositionWithOffset(pos, 0)
            lm?.getChildAt(pos)?.requestFocus()
        } catch (_: Throwable) {
        }
    }

    fun bindHolder(data: ItemData) {
        val adapter = rcv.adapter as? ItemChildAdapter
        adapter?.submitList(data.itemData())
    }
}