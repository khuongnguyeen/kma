package io.kma.results.readercccd.model

class SessionData private constructor() {
    var inputData: InputData? = null

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