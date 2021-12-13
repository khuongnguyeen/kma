package org.jmrtd

import android.os.Parcel
import android.os.Parcelable

class FeatureStatus : Parcelable {

    private var hasSAC: Verdict? = null
    private var hasBAC: Verdict? = null
    private var hasAA: Verdict? = null
    private var hasEAC: Verdict? = null
    private var hasCA: Verdict? = null

    enum class Verdict {
        UNKNOWN,
        PRESENT,
        NOT_PRESENT
    }

    constructor() {
        this.hasSAC = Verdict.UNKNOWN
        this.hasBAC = Verdict.UNKNOWN
        this.hasAA = Verdict.UNKNOWN
        this.hasEAC = Verdict.UNKNOWN
        this.hasCA = Verdict.UNKNOWN
    }

    fun setSAC(hasSAC: Verdict) {
        this.hasSAC = hasSAC
    }

    fun hasSAC(): Verdict? {
        return hasSAC
    }


    fun setBAC(hasBAC: Verdict) {
        this.hasBAC = hasBAC
    }

    fun hasBAC(): Verdict? {
        return hasBAC
    }

    fun setAA(hasAA: Verdict) {
        this.hasAA = hasAA
    }

    fun hasAA(): Verdict? {
        return hasAA
    }

    fun setEAC(hasEAC: Verdict) {
        this.hasEAC = hasEAC
    }

    fun hasEAC(): Verdict? {
        return hasEAC
    }

    fun setCA(hasCA: Verdict) {
        this.hasCA = hasCA
    }

    fun hasCA(): Verdict? {
        return hasCA
    }

    constructor(parcel: Parcel) {
        this.hasSAC = if(parcel.readInt() == 1){ Verdict.valueOf(parcel.readString()!!) } else { null }
        this.hasBAC = if(parcel.readInt() == 1){Verdict.valueOf(parcel.readString()!!) } else { null }
        this.hasAA = if(parcel.readInt() == 1){Verdict.valueOf(parcel.readString()!!) } else { null }
        this.hasEAC = if(parcel.readInt() == 1){Verdict.valueOf(parcel.readString()!!) } else { null }
        this.hasCA = if(parcel.readInt() == 1){Verdict.valueOf(parcel.readString()!!) } else { null }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if(this.hasSAC!=null) 1 else 0)
        if(this.hasSAC!=null) {
            dest.writeString(this.hasSAC?.name)
        }
        dest.writeInt(if(this.hasBAC!=null) 1 else 0)
        if(this.hasBAC!=null) {
            dest.writeString(this.hasBAC?.name)
        }
        dest.writeInt(if(this.hasAA!=null) 1 else 0)
        if(this.hasAA!=null) {
            dest.writeString(this.hasAA?.name)
        }
        dest.writeInt(if(this.hasEAC!=null) 1 else 0)
        if(this.hasEAC!=null) {
            dest.writeString(this.hasEAC?.name)
        }
        dest.writeInt(if(this.hasCA!=null) 1 else 0)
        if(this.hasCA!=null) {
            dest.writeString(this.hasCA?.name)
        }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<*> = object : Parcelable.Creator<FeatureStatus> {
            override fun createFromParcel(pc: Parcel): FeatureStatus {
                return FeatureStatus(pc)
            }

            override fun newArray(size: Int): Array<FeatureStatus?> {
                return arrayOfNulls(size)
            }
        }
    }
}
