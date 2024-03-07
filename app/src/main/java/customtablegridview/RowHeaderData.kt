package customtablegridview

abstract class RowHeaderData{
    abstract fun <T: RowHeaderData> itemSame( b:T) : Boolean
    abstract fun <T: RowHeaderData> contentSame( b:T) : Boolean
}