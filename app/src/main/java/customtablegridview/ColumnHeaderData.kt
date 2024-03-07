package customtablegridview

abstract class ColumnHeaderData{
    abstract fun <T: ColumnHeaderData> itemSame( b:T) : Boolean
    abstract fun <T: ColumnHeaderData> contentSame( b:T) : Boolean
}