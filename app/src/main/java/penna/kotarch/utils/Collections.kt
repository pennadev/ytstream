package penna.kotarch.utils

import android.util.SparseArray

/**
 * Created by danpena on 7/26/17.
 */

fun <C> convertToList(sparseArr: SparseArray<C>): List<C> {
    val arrayList = ArrayList<C>(sparseArr.size())
    for (i in 0 until sparseArr.size()) {
        val element = sparseArr.valueAt(i)
        if (element != null)
            arrayList.add(element)
    }
    return arrayList
}