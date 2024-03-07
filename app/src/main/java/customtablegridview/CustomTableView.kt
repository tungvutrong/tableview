package customtablegridview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tablegridview.R
import java.lang.Exception

class CustomTableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var rowHeaderSizePrefer: Float = 0.3f
    private var rowHeaderSizeFix: Int = 0
    private var rowHeaderScrollAble: Boolean = true

    private var columnHeaderSizePrefer: Float = 0.1f
    private var columnHeaderSizeFix: Int = 0
    private var columnHeaderScrollAble: Boolean = true


    private lateinit var mainLayout: ConstraintLayout
    private lateinit var rowHeaderView: RecyclerView
    private lateinit var columnHeaderView: RecyclerView
    private lateinit var dataView: RecyclerView

    private var initUiComponent = false

    private fun rowHeaderAvailable(): Boolean {
        return (rowHeaderSizePrefer > 0f && rowHeaderSizePrefer < 1f) || rowHeaderSizeFix > 0
    }

    private fun columnHeaderAvailable(): Boolean {
        return (columnHeaderSizePrefer > 0f && columnHeaderSizePrefer < 1f) || columnHeaderSizeFix > 0
    }

    private var currentIndex = 0
    private var currentItemHeight = 0

    fun config(
        rowData: Triple<Boolean?, Float?, Int?>? = null,
        rowViewHolder: ((ViewGroup, Int) -> RowViewHolder)? = null,
        columnData: Triple<Boolean?, Float?, Int?>? = null,
        columnViewHolder: ((ViewGroup, Int) -> ColumnHeaderHolder)? = null,
        itemViewHolder: (ViewGroup, Int, ()->Unit) -> ItemChildHolder
    ) {
        if (initUiComponent) {
            throw Exception("UI available")
        }

        val inflater = LayoutInflater.from(context)
        val mainView = inflater.inflate(R.layout.item_table_base_view, this, false)
        addView(
            mainView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mainLayout = mainView.findViewById(R.id.ctl_main)
        dataView = mainView.findViewById<RecyclerView>(R.id.rcv_item).apply {
            itemAnimator = null
            var waitingScrollUpAction = false
            var waitingScrollDownAction = false
            var waitingNextItem = -1
            var onAction = false

            val viewLayoutManager = CustomLinearLayoutManager(
                context = context,
                orientation = RecyclerView.VERTICAL,
                reverseLayout = false,
                lastPrevent = 0,
                smoothScrollTime = 50f
            )
            this.layoutManager = viewLayoutManager
            viewLayoutManager.verticalItemFocus({
                if(waitingScrollUpAction || onAction ||waitingScrollDownAction) return@verticalItemFocus
                waitingNextItem = it
                waitingScrollUpAction = true
            }, {
//                if(waitingScrollDownAction || onAction ||waitingScrollUpAction) return@verticalItemFocus
//                waitingScrollDownAction = true
//                waitingNextItem = it
//                rowHeaderView.smoothScrollBy(0, currentItemHeight)
            })

            isFocusable = false
            addItemDecoration(LinearItemDecoration(10, RecyclerView.VERTICAL, false))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy != 0) {
                        if (rowHeaderAvailable()) {
                            rowHeaderView.scrollBy(0, dy)
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (waitingScrollUpAction && waitingNextItem >= 0 && !onAction) {
                            onAction = true
                            val view = (dataView.layoutManager as? LinearLayoutManager)
                                ?.findViewByPosition(waitingNextItem)
                            val child = (view as? ViewGroup)?.getChildAt(currentIndex)
                            child?.requestFocus()
                            waitingScrollUpAction = false
                            onAction = false
                        }
                        if (waitingScrollDownAction && waitingNextItem >= 0 && !onAction) {
                            onAction = true
                            Handler(Looper.getMainLooper()).postDelayed({
                                val view = (dataView.layoutManager as? LinearLayoutManager)
                                    ?.findViewByPosition(waitingNextItem)
                                val child = (view as? ViewGroup)?.getChildAt(currentIndex)
                                child?.requestFocus()
                                waitingScrollDownAction = false
                                onAction = false
                            },100)
                        }
                    }
                }
            })
            adapter = ItemAdapter(itemViewHolder, { v, dx, _, pos ->
                currentIndex = pos
                if (columnHeaderAvailable()) {
                    columnHeaderView.scrollBy(dx, 0)
                }
                dataView.forEach {
                    if (it is RecyclerView && it != v) {
                        it.scrollBy(dx, 0)
                    }
                }
            }, { _, h ->
                currentItemHeight = h
                viewLayoutManager.verticalOffset = h
            })
        }

        rowHeaderSizePrefer = rowData?.second ?: 0f
        rowHeaderSizeFix = rowData?.third ?: 0
        rowHeaderScrollAble = rowData?.first ?: rowHeaderScrollAble

        columnHeaderSizePrefer = columnData?.second ?: 0f
        columnHeaderSizeFix = columnData?.third ?: 0
        columnHeaderScrollAble = columnData?.first ?: columnHeaderScrollAble
        if (rowHeaderAvailable()) {
            rowHeaderView = mainView.findViewById<RecyclerView>(R.id.rcv_row_header).apply {
                layoutManager = LinearLayoutManager(
                    context, RecyclerView.VERTICAL, false
                )
                isFocusable = false
                addItemDecoration(LinearItemDecoration(10, RecyclerView.VERTICAL, false))
                adapter = rowViewHolder?.let { RowHeaderAdapter(it) }
                itemAnimator = null
            }
        }
        if (columnHeaderAvailable()) {
            columnHeaderView = mainView.findViewById<RecyclerView>(R.id.rcv_column_header).apply {
                layoutManager = LinearLayoutManager(
                    context, RecyclerView.HORIZONTAL, false
                )
                isFocusable = false
                addItemDecoration(LinearItemDecoration(10, RecyclerView.HORIZONTAL, false))
                adapter = columnViewHolder?.let { ColumnHeaderAdapter(it) }
                itemAnimator = null
            }
        }

        if (rowHeaderAvailable()) {
            val rowSet = ConstraintSet()
            rowSet.clone(mainLayout)
            if (rowHeaderSizePrefer > 0f) {
                rowSet.constrainPercentWidth(rowHeaderView.id, rowHeaderSizePrefer)
            } else {
                rowSet.constrainWidth(rowHeaderView.id, rowHeaderSizeFix)
            }
            rowSet.applyTo(mainLayout)
        }
        if (columnHeaderAvailable()) {
            val columnSet = ConstraintSet()
            columnSet.clone(mainLayout)
            if (rowHeaderSizePrefer > 0f) {
                columnSet.constrainPercentHeight(columnHeaderView.id, columnHeaderSizePrefer)
            } else {
                columnSet.constrainHeight(columnHeaderView.id, columnHeaderSizeFix)
            }
            columnSet.applyTo(mainLayout)
        }

        initUiComponent = true
    }

    fun updateData(
        rowHeaderData: List<RowHeaderData>,
        columnHeaderData: List<ColumnHeaderData>,
        itemData: List<ItemData>
    ) {
        if (!initUiComponent) {
            throw Exception("UI not available, please call config before")
        }
        if (rowHeaderAvailable()) {
            val rowHeaderAdapter = rowHeaderView.adapter as? RowHeaderAdapter
            rowHeaderAdapter?.submitList(rowHeaderData)
        }
        if (columnHeaderAvailable()) {
            val columnHeaderAdapter = columnHeaderView.adapter as? ColumnHeaderAdapter
            columnHeaderAdapter?.submitList(columnHeaderData)
        }
        val itemAdapter = dataView.adapter as? ItemAdapter
        itemAdapter?.submitList(itemData)
    }
}