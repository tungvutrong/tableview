package customtablegridview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tablegridview.R
import java.util.concurrent.Executors

class ItemAdapter(
    private val childViewHolder: (ViewGroup, Int) -> ItemChildHolder,
    private val scroll: (Int) -> Unit,
    private val onChildReady: (Int) -> Unit
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_data_parent, parent, false)
        return ItemHolder(view as RecyclerView, childViewHolder, scroll, onChildReady)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bindHolder(getItem(position), position)
    }

}