package customtablegridview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.tablegridview.R
import java.util.concurrent.Executors

class ItemAdapter(
    private val childViewHolder: (ViewGroup, Int, ()->Unit) -> ItemChildHolder,
    private val onChildScroll: (View, Int, Int, Int) -> Unit,
    private val onViewComplete: (Int, Int) -> Unit
) : ListAdapter<ItemData, ItemHolder>(
    AsyncDifferConfig.Builder(ItemCallback())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    class ItemCallback : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem.itemSame(newItem)
        }

        override fun areContentsTheSame(
            oldItem: ItemData,
            newItem: ItemData
        ): Boolean {
            return oldItem.contentSame(newItem)
        }
    }

    override fun onViewAttachedToWindow(holder: ItemHolder) {
        super.onViewAttachedToWindow(holder)
        holder.availableIndex(currentIndex)
    }

    private var currentIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_table_data_row, parent, false)
        view.post {
            onViewComplete.invoke(view.width, view.height)
        }
        return ItemHolder(view, childViewHolder) { v, dx, dy, pos ->
            currentIndex = pos
            onChildScroll(v, dx, dy, pos)
        }
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bindHolder(getItem(position))
    }
}