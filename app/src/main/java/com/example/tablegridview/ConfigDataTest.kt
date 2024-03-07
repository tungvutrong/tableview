package com.example.tablegridview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import customtablegridview.ColumnHeaderData
import customtablegridview.ColumnHeaderHolder
import customtablegridview.ItemChildData
import customtablegridview.ItemChildHolder
import customtablegridview.ItemData
import customtablegridview.RowHeaderData
import customtablegridview.RowViewHolder
import java.util.UUID


object ConfigDataTest {

    data class ChannelModel(
        val id: Int,
        val name: String = "ABCDEFGHIJKLMNOPQRSTUV".random().toString(),
        val url: String = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1024px-Cat03.jpg",
        val epg: List<EpgModel>,
    ) : ItemData() {
        override fun <T : ItemData> itemSame(b: T): Boolean {
            return b is ChannelModel && this.id == b.id
        }

        override fun <T : ItemData> contentSame(b: T): Boolean {
            return b == this
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ItemChildData> itemData(): List<T> {
            return epg as List<T>
        }

    }

    data class EpgModel(
        val id: String,
        val index: Int,
        val name: String
    ) : ItemChildData() {
        override fun <T : ItemChildData> itemSame(b: T): Boolean {
            return b is EpgModel && this.id == b.id
        }

        override fun <T : ItemChildData> contentSame(b: T): Boolean {
            return b == this
        }
    }

    private const val channelTest = 55
    const val epgTest = 30
    val dataTest = (0 until channelTest).map { a ->
        ChannelModel(id = a, epg = (0 until epgTest).map { b ->
            EpgModel(id = "$a", index = b, name = "$a-$b")
        })
    }

    data class EpgRowHeaderModel(
        val id: String,
        val url: String
    ) : RowHeaderData() {
        override fun <T : RowHeaderData> itemSame(b: T): Boolean {
            return b is EpgRowHeaderModel && this.id == b.id
        }

        override fun <T : RowHeaderData> contentSame(b: T): Boolean {
            return b == this
        }

    }

    data class EpgColumnHeaderModel(
        val id: String = UUID.randomUUID().toString(),
        val name: String = "ABCDEFGHIJKLMNOPQRSTUV".random().toString()
    ) : ColumnHeaderData() {
        override fun <T : ColumnHeaderData> itemSame(b: T): Boolean {
            return b is EpgColumnHeaderModel && this.id == b.id
        }

        override fun <T : ColumnHeaderData> contentSame(b: T): Boolean {
            return b == this
        }

    }

    fun epgRowHeaderCreator(parent: ViewGroup, type: Int): RowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_table_header_row, parent, false)
        return RowViewHolder(view) { data, _ ->
            val img = view.findViewById<ImageView>(R.id.iv_row_header)
            val tv = view.findViewById<TextView>(R.id.tv_row_header)
            val uri = (data as EpgRowHeaderModel).url
            Glide.with(parent).load(uri).into(img)
            tv.text = data.id
        }
    }

    fun epgColumnHeaderCreator(parent: ViewGroup, type: Int): ColumnHeaderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_table_header_column, parent, false)
        return ColumnHeaderHolder(view) { data, _ ->
            val nameView = view.findViewById<TextView>(R.id.ctv_data)
            val text = (data as EpgColumnHeaderModel).name
            nameView.text = text
        }
    }

    fun epgChildCreator(parent: ViewGroup, type: Int): ItemChildHolder {
        val inflater = LayoutInflater.from(parent.context)
        val child = inflater.inflate(R.layout.item_table_data_cell, parent, false)
        return ItemChildHolder(child) { a, b ->
            val nameView = child.findViewById<TextView>(R.id.ctv_data)
            val text = (a as EpgModel).name
            nameView.text = text
        }
    }
}