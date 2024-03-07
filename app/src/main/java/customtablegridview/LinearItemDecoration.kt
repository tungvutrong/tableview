package customtablegridview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearItemDecoration(
    private val spacing: Int,
    private val orientation: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val size = parent.adapter?.itemCount ?: return
        if (orientation == RecyclerView.VERTICAL) {
            if (includeEdge) {
                outRect.bottom = spacing
            } else {
                if (position < size) {
                    outRect.bottom = spacing
                }
            }
        } else {
            if (includeEdge) {
                outRect.right = spacing
            } else {
                if (position < size) {
                    outRect.right = spacing
                }
            }
        }
    }
}
