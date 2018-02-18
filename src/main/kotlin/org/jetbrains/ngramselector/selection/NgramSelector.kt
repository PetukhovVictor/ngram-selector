package org.jetbrains.ngramselector.selection

import org.jetbrains.ngramselector.selection.selectors.Selector


typealias GramsStatistic = MutableMap<String, Int>
typealias GramStatisticList = MutableList<Pair<String, Int>>
typealias GramList = MutableList<String>

class NgramSelector {
    companion object {
        fun statisticToSortedList(ngrams: GramsStatistic): GramStatisticList {
            val ngramList: GramStatisticList = mutableListOf()

            ngrams.map { ngramList.add(Pair(it.key, it.value)) }

            return ngramList.sortedWith(compareByDescending({ it.second })) as GramStatisticList
        }

        fun run(ngrams: GramStatisticList, selectors: List<Selector>): GramList {
            var ngramsSelected = ngrams
            val ngramsSelectedList: GramList = mutableListOf()

            selectors.forEach {
                ngramsSelected = it.select(ngramsSelected)
            }

            ngramsSelected.forEach {
                ngramsSelectedList.add(it.first)
            }

            return ngramsSelectedList
        }
    }
}