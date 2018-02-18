package org.jetbrains.ngramselector.selection

import org.jetbrains.ngramselector.selection.selectors.Selector


typealias GramsStatistic = MutableMap<String, Int>
typealias GramList = MutableList<Pair<String, Int>>

class NgramSelector {
    companion object {
        fun statisticToSortedList(ngrams: GramsStatistic): GramList {
            val ngramList: GramList = mutableListOf()

            ngrams.map { ngramList.add(Pair(it.key, it.value)) }

            return ngramList.sortedWith(compareByDescending({ it.second })) as GramList
        }

        fun run(ngrams: GramList, selectors: List<Selector>): GramList {
            var ngramsSelected = ngrams

            selectors.forEach {
                ngramsSelected = it.select(ngramsSelected)
            }

            return ngramsSelected
        }
    }
}