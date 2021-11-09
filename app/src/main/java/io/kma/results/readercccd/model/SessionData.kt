package io.kma.results.readercccd.model

class SessionData private constructor() {
    var docData: DocData? = null
    var inputData: InputData? = null
    fun clean() {
        instance = null
    }

    companion object {
        private var instance: SessionData? = null
        @JvmStatic
        fun getInstance(): SessionData? {
            if (instance == null) {
                instance = SessionData()
            }
            return instance
        }
    }
}