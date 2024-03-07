package customtablegridview

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import java.util.concurrent.Executors

class ColumnHeaderAdapter(
    private val viewHolder: (ViewGroup, Int) -> ColumnHeaderHolder
) : ListAdapter<ColumnHeaderData, ColumnHeaderHolder>(
    AsyncDifferConfig.Builder(ColumnHeaderItemCallback())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    class ColumnHeaderItemCallback :DiffUtil.ItemCallback<ColumnHeaderData>(){
        override fun areItemsTheSame(oldItem: ColumnHeaderData, newItem: ColumnHeaderData): Boolean {
            return oldItem.itemSame(newItem)
        }

        override fun areContentsTheSame(
            oldItem: ColumnHeaderData,
            newItem: ColumnHeaderData
        ): Boolean {
            return oldItem.contentSame(newItem)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColumnHeaderHolder {
        return viewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: ColumnHeaderHolder, position: Int) {
        holder.bindHolder(getItem(position), position)
    }

}