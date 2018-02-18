package org.jetbrains.ngramselector.selection.selectors

import org.jetbrains.ngramgenerator.helpers.TimeLogger
import org.jetbrains.ngramselector.selection.GramList

enum class EndsSelectorSide {
    HEAD, TAIL
}

enum class EndsSelectorTypes {
    ORDER, VALUE
}

class EndsSelectors(private val side: EndsSelectorSide, private val type: EndsSelectorTypes, private val bound: Int): Selector {
    private fun selectByOrder(ngrams: GramList): GramList {
        when (side) {
            EndsSelectorSide.HEAD -> return ngrams.subList(bound, ngrams.size - 1)
            EndsSelectorSide.TAIL -> return ngrams.subList(0, toIndex = bound)
        }
    }

    private fun selectByValue(ngrams: GramList): GramList {
        when (side) {
            EndsSelectorSide.HEAD -> return ngrams.filter { it.second <= bound } as GramList
            EndsSelectorSide.TAIL -> return ngrams.filter { it.second >= bound } as GramList
        }
    }

    override fun select(ngrams: GramList): GramList {
        val timeLogger = TimeLogger(task_name = "N-grams selection via EndsSelectors (from $side, by $type, bound: $bound)")
        var ngramsSelected: GramList? = null

        when (type) {
            EndsSelectorTypes.ORDER -> ngramsSelected = selectByOrder(ngrams)
            EndsSelectorTypes.VALUE -> ngramsSelected = selectByValue(ngrams)
        }

        timeLogger.finish()
        println("${ngramsSelected.size} out of ${ngrams.size} n-grams selected (${ngrams.size - ngramsSelected.size} excluded)")
        println("--------------------------------")

        return ngramsSelected
    }
}