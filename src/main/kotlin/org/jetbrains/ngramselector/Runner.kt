package org.jetbrains.ngramselector

import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.ngramgenerator.helpers.TimeLogger
import org.jetbrains.ngramselector.io.JsonFilesReader
import org.jetbrains.ngramselector.selection.GramsStatistic
import org.jetbrains.ngramselector.selection.NgramSelector
import org.jetbrains.ngramselector.selection.selectors.DerivativeBoundsSelector
import org.jetbrains.ngramselector.selection.selectors.EndsSelectorSide
import org.jetbrains.ngramselector.selection.selectors.EndsSelectorTypes
import org.jetbrains.ngramselector.selection.selectors.EndsSelectors
import java.io.File

class Runner {
    companion object {
        fun run(cstVectorsPath: String, cstVectorsWithSelectedNgramsPath: String, allNgramsPath: String) {
            val timeLogger = TimeLogger(task_name = "N-grams selection")

            val gramsStatisticReference = object: TypeReference<GramsStatistic>() {}

            val allNgrams = JsonFilesReader.readFile<GramsStatistic>(File(allNgramsPath), gramsStatisticReference)
            val allNgramsList = NgramSelector.statisticToSortedList(allNgrams)

            val headSelector = EndsSelectors(side = EndsSelectorSide.HEAD, type = EndsSelectorTypes.VALUE, bound = 10000)
            val tailSelector = EndsSelectors(side = EndsSelectorSide.TAIL, type = EndsSelectorTypes.VALUE, bound = 200)
            val derivativeBoundsSelector = DerivativeBoundsSelector(point = Math.tan(Math.PI / 4) - 0.1, deviation = 2.0)

            val ngramsSelected = NgramSelector.run(allNgramsList, listOf(derivativeBoundsSelector, headSelector, tailSelector))

            timeLogger.finish(fullFinish = true)
            println("${ngramsSelected.size} out of ${allNgramsList.size} n-grams selected (${allNgramsList.size - ngramsSelected.size} excluded)")
        }
    }
}