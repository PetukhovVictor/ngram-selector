package org.jetbrains.ngramselector

import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.ngramgenerator.helpers.TimeLogger
import org.jetbrains.ngramselector.io.FileWriter
import org.jetbrains.ngramselector.io.JsonFilesReader
import org.jetbrains.ngramselector.selection.GramList
import org.jetbrains.ngramselector.selection.GramsStatistic
import org.jetbrains.ngramselector.selection.NgramSelector
import org.jetbrains.ngramselector.selection.selectors.DerivativeBoundsSelector
import org.jetbrains.ngramselector.selection.selectors.EndsSelectorSide
import org.jetbrains.ngramselector.selection.selectors.EndsSelectorTypes
import org.jetbrains.ngramselector.selection.selectors.EndsSelectors
import java.io.File

class Runner {
    companion object {
        private const val NGRAMS_SELECTED_PATH = "./selected_ngrams.json"

        fun ngramSelect(allNgramsPath: String): GramList {
            val timeLogger = TimeLogger(task_name = "N-grams selection")

            val gramsStatisticReference = object: TypeReference<GramsStatistic>() {}

            val allNgrams = JsonFilesReader.readFile<GramsStatistic>(File(allNgramsPath), gramsStatisticReference)
            val allNgramsList = NgramSelector.statisticToSortedList(allNgrams)

            val headSelector = EndsSelectors(side = EndsSelectorSide.HEAD, type = EndsSelectorTypes.VALUE, bound = 10000)
            val tailSelector = EndsSelectors(side = EndsSelectorSide.TAIL, type = EndsSelectorTypes.VALUE, bound = 200)
            val derivativeBoundsSelector = DerivativeBoundsSelector(point = Math.tan(Math.PI / 4) - 0.1, deviation = 2.0)

            val ngramsSelected = NgramSelector.run(allNgramsList, listOf(derivativeBoundsSelector, headSelector, tailSelector))

            FileWriter.write(this.NGRAMS_SELECTED_PATH, ngramsSelected)

            timeLogger.finish(fullFinish = true)
            println("${ngramsSelected.size} out of ${allNgramsList.size} n-grams selected (${allNgramsList.size - ngramsSelected.size} excluded)")

            return ngramsSelected
        }

        fun ngramSelectByFiles(cstVectorsPath: String, cstVectorsWithSelectedNgramsPath: String, ngramsSelected: GramList) {
            val cstNodeReference = object: TypeReference<GramList>() {}

            JsonFilesReader<GramList>(cstVectorsPath, "json", cstNodeReference).run(true) { content: GramList, file: File ->
                val ngramsInCurrentFileSelected = content.intersect(ngramsSelected)

                FileWriter.write(file, cstVectorsPath, cstVectorsWithSelectedNgramsPath, ngramsInCurrentFileSelected)

                println("$file: ${ngramsInCurrentFileSelected.size} out of ${content.size} n-grams selected (${content.size - ngramsInCurrentFileSelected.size} excluded)")
            }
        }

        fun run(cstVectorsPath: String, cstVectorsWithSelectedNgramsPath: String, allNgramsPath: String) {
            val ngramsSelected = this.ngramSelect(allNgramsPath)

            this.ngramSelectByFiles(cstVectorsPath, cstVectorsWithSelectedNgramsPath, ngramsSelected)
        }
    }
}