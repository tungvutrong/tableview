package customtablegridview

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import java.util.concurrent.Executors

class ItemChildAdapter(
    private val viewHolder: (ViewGroup, Int) -> ItemChildHolder
) : ListAdapter<ItemChildData, ItemChildHolder>(
    AsyncDifferConfig.Builder(ItemChildCallback())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {

    class ItemChildCallback :DiffUtil.ItemCallback<ItemChildData>(){
        override fun areItemsTheSame(oldItem: ItemChildData, newItem: ItemChildData): Boolean {
            return oldItem.itemSame(newItem)
        }

        override fun areContentsTheSame(
            oldItem: ItemChildData,
            newItem: ItemChildData
        ): Boolean {
            return oldItem.contentSame(newItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemChildHolder {
        return viewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: ItemChildHolder, position: Int) {
        holder.bindHolder(getItem(position), position)
    }

}