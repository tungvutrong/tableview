package customtablegridview

abstract class ItemData{
    abstract fun <T: ItemData> itemSame(b:T) : Boolean
    abstract fun <T: ItemData> contentSame(b:T) : Boolean
    abstract fun <T: ItemChildData> itemData(): List<T>
}