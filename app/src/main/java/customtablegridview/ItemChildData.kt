package customtablegridview

abstract class ItemChildData{
    abstract fun <T: ItemChildData> itemSame(b:T) : Boolean
    abstract fun <T: ItemChildData> contentSame(b:T) : Boolean
}