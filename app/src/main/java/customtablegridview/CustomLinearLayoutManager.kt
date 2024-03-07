package customtablegridview

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

open class CustomLinearLayoutManager(
    val context: Context? = null,
    @RecyclerView.Orientation
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    private var lastPrevent: Int = 2,
    private val smoothScrollTime: Float = 25f
) : LinearLayoutManager(context, orientation, reverseLayout) {

    private var horizontalItemFocusCallback: ((Int) -> Unit)? = null
    private var verticalItemFocusCallbackUp: ((Int) -> Unit)? = null
    private var verticalItemFocusCallbackDown: ((Int) -> Unit)? = null

    var verticalOffset = 0
    private var view: RecyclerView?=null
    fun horizontalItemFocus(callback: (Int) -> Unit) {
        horizontalItemFocusCallback = callback
    }

    fun verticalItemFocus(callbackUp: (Int) -> Unit, callbackDown: (Int) -> Unit) {
        verticalItemFocusCallbackUp = callbackUp
        verticalItemFocusCallbackDown = callbackDown
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        this.view =  view
    }

    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        val position = findItemPosition(focused)
        position ?: return null
        if (position < 0) return null
        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                if (position == 0 && direction == View.FOCUS_LEFT) {
                    return focused
                } else if (position == itemCount - lastPrevent - 1 && direction == View.FOCUS_RIGHT) {
                    return focused
                } else {
                    if (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
                        val nextPosition = if (direction == View.FOCUS_LEFT) {
                            position - 1
                        } else {
                            position + 1
                        }
                        horizontalItemFocusCallback?.invoke(nextPosition)
                        return if (nextPosition !in findFirstVisibleItemPosition()..findLastVisibleItemPosition()) {
                            null
                        } else {
                            smoothScrollToPosition(nextPosition)
                            null
                        }
                    }
                }
            }

            RecyclerView.VERTICAL -> {
                if ((position == 0 && direction == View.FOCUS_UP) || (position == itemCount - lastPrevent - 1 && direction == View.FOCUS_DOWN)) {
                    return focused
                } else {
                    val nextPosition = if (direction == View.FOCUS_UP) {
                        position - 1
                    } else {
                        position + 1
                    }
                    if (nextPosition in findFirstVisibleItemPosition()..findLastVisibleItemPosition()) {
                        return null
                    }
                    if (direction == View.FOCUS_DOWN) {
                        if (nextPosition !in findFirstVisibleItemPosition()..findLastVisibleItemPosition()) {
                            verticalItemFocusCallbackDown?.invoke(nextPosition)
                            view?.smoothScrollBy(0, verticalOffset)
                            return null
                        }
                        return null
                    }
                    verticalItemFocusCallbackUp?.invoke(nextPosition)
                    smoothScrollToPosition(nextPosition)
                    return focused
                }

            }
        }
        return null
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
                        smoothScrollTime / it
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