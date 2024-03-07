package customtablegridview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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

    private var childItem = 0

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var rowHeaderView: RecyclerView
    private lateinit var columnHeaderView: RecyclerView
    private lateinit var dataView: RecyclerView
    private lateinit var hsvView: CustomHorizontalScrollView

    private var initUiComponent = false

    private fun rowHeaderAvailable(): Boolean {
        return (rowHeaderSizePrefer > 0f && rowHeaderSizePrefer < 1f) || rowHeaderSizeFix > 0
    }

    private fun columnHeaderAvailable(): Boolean {
        return (columnHeaderSizePrefer > 0f && columnHeaderSizePrefer < 1f) || columnHeaderSizeFix > 0
    }

    fun config(
        rowData: Triple<Boolean?, Float?, Int?>? = null,
        rowViewHolder: ((ViewGroup, Int) -> RowViewHolder)? = null,
        columnData: Triple<Boolean?, Float?, Int?>? = null,
        columnViewHolder: ((ViewGroup, Int) -> ColumnHeaderHolder)? = null,
        itemViewHolder: (ViewGroup, Int) -> ItemChildHolder
    ) {
        if (initUiComponent) {
            throw Exception("UI available")
        }

        val inflater = LayoutInflater.from(context)
        val mainView = inflater.inflate(R.layout.item_base_view, this, false)
        addView(
            mainView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mainLayout = mainView.findViewById(R.id.ctl_main)
        hsvView = mainView.findViewById<CustomHorizontalScrollView?>(R.id.hsv_item).apply {
            listener = object : CustomHorizontalScrollView.Listener {
                override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                    val dx = l - oldl
                    Log.i("aaaaaaaaaaaaaa", "onScrollChanged: $dx")

                    if (dx != 0) {
                        columnHeaderView.scrollBy(dx,0)
                    }
                }
            }
        }
        dataView = mainView.findViewById<RecyclerView>(R.id.rcv_item).apply {
            layoutManager = LinearLayoutManager(
                context, RecyclerView.VERTICAL, false
            )
            isFocusable = false
            addItemDecoration(LinearItemDecoration(10, RecyclerView.VERTICAL, false))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    Log.i("aaaaaaaaaaaaaa", "onScrolled 1: $dy")
                    if (dy != 0) {
                        rowHeaderView.scrollBy(0, dy)
                    }
                }
            })
            adapter = ItemAdapter(itemViewHolder, { direction ->
                if (childItem != 0 && direction == View.FOCUS_LEFT || direction == FOCUS_RIGHT) {
                    val prefix = if (direction == View.FOCUS_LEFT) -1 else 1
                    hsvView.scrollBy(prefix * childItem, 0)
                }
            }, {
                if (childItem == 0) childItem = it
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