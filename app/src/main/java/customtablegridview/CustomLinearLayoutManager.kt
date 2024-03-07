package customtablegridview

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CustomLinearLayoutManager(
    val context: Context? = null,
    @RecyclerView.Orientation
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    private var preventScroll = false
    private var callbackPreventScroll: ((Int) -> Unit)? = null

    fun preventChildScrollAndDetectDirection(callback: (Int) -> Unit) {
        preventScroll = true
        callbackPreventScroll = callback
    }

    override fun canScrollHorizontally(): Boolean {
        return super.canScrollHorizontally() && !preventScroll
    }

    override fun canScrollVertically(): Boolean {
        return super.canScrollVertically() && !preventScroll
    }

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        val position =
            findItemPosition(focused) ?: return super.onInterceptFocusSearch(focused, direction)
        if (position < 0) return super.onInterceptFocusSearch(focused, direction)

        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                if (position == 0 && direction == View.FOCUS_LEFT) {
                    return focused
                } else if (position == itemCount - 1 && direction == View.FOCUS_RIGHT) {
                    return focused
                } else {
                    if (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
                        Log.i("aaaaaaaaaaaa", "onInterceptFocusSearch: $preventScroll $direction")
                        if (preventScroll) {
                            callbackPreventScroll?.invoke(direction)
                            return super.onInterceptFocusSearch(focused, direction)
                        }
                        val nextPosition =
                            if (direction == View.FOCUS_LEFT) position - 1 else position + 1
                        smoothScrollToPosition(nextPosition)
                    }
                }
            }

            RecyclerView.VERTICAL -> {
                if (position == 0 && direction == View.FOCUS_UP) {
                    return focused
                } else if (position == itemCount - 1 && direction == View.FOCUS_DOWN) {
                    return focused
                } else {
                    if (direction == View.FOCUS_UP || direction == View.FOCUS_DOWN) {
                        Log.i("aaaaaaaaaaaa", "onInterceptFocusSearch: $preventScroll $direction")
                        if (preventScroll) {
                            callbackPreventScroll?.invoke(direction)
                            return super.onInterceptFocusSearch(focused, direction)
                        }
                        val nextPosition =
                            if (direction == View.FOCUS_UP) position - 1 else position + 1
                        smoothScrollToPosition(nextPosition)
                    }
                }
            }
        }
        return super.onInterceptFocusSearch(focused, direction)
    }

    override fun scrollToPosition(position: Int) {
        smoothScrollToPosition(position)
    }

    private fun smoothScrollToPosition(position: Int) {
        try {
            val smoothScroller = object : LinearSmoothScroller(context) {
                override fun getHorizontalSnapPreference() =
                    if (orientation == RecyclerView.HORIZONTAL) SNAP_TO_START else super.getHorizontalSnapPreference()

                override fun getVerticalSnapPreference() =
                    if (orientation == RecyclerView.VERTICAL) SNAP_TO_START else super.getVerticalSnapPreference()

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                    return displayMetrics?.densityDpi?.let {
                        10f / it
                    } ?: super.calculateSpeedPerPixel(displayMetrics)
                }
            }.apply {
                targetPosition = position
            }

            startSmoothScroll(smoothScroller)
        } catch (_: Exception) {
        }
    }

    private fun findItemPosition(view: View?): Int? {
        if (view?.layoutParams is RecyclerView.LayoutParams) {
            return if (view.parent is RecyclerView && (view.parent as RecyclerView).layoutManager == this) {
                (view.layoutParams as? RecyclerView.LayoutParams)?.viewAdapterPosition
            } else {
                findItemPosition(view.parent as? View)
            }
        }

        if (view?.parent == view?.rootView) {
            return null
        }

        return if (view?.parent != null) {
            findItemPosition(view.parent as? View)
        } else {
            null
        }
    }
}