package customtablegridview

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import java.util.concurrent.Executors

class RowHeaderAdapter(
    private val viewHolder: (ViewGroup, Int) -> RowViewHolder
) : ListAdapter<RowHeaderData, RowViewHolder>(
    AsyncDifferConfig.Builder(RowHeaderItemCallback())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {

    class RowHeaderItemCallback :DiffUtil.ItemCallback<RowHeaderData>(){
        override fun areItemsTheSame(oldItem: RowHeaderData, newItem: RowHeaderData): Boolean {
            return oldItem.itemSame(newItem)
        }

        override fun areContentsTheSame(
            oldItem: RowHeaderData,
            newItem: RowHeaderData
        ): Boolean {
            return oldItem.contentSame(newItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        return viewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bindHolder(getItem(position), position)
    }

}