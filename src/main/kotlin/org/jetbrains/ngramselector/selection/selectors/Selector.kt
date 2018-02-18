package org.jetbrains.ngramselector.selection.selectors

import org.jetbrains.ngramselector.selection.GramList

interface Selector {
    fun select(ngrams: GramList): GramList
}