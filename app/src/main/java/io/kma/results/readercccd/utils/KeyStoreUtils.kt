package io.kma.results.readercccd.utils

import org.spongycastle.jce.provider.BouncyCastleProvider
import java.io.*
import java.security.KeyStore
import java.security.Security
import java.security.cert.*
import kotlin.collections.ArrayList


class KeyStoreUtils {

    fun readKeystoreFromFile(folder:File, fileName:String="csca.ks", password:String=""):KeyStore?{
        try{
            val file = File(folder, fileName)
            val keyStore: KeyStore = KeyStore.getInstance("PKCS12")
            val fileInputStream = FileInputStream(file)
            keyStore.load(fileInputStream, password.toCharArray())
            return keyStore
        }catch (e:java.lang.Exception) {
            return null
        }
    }
    fun toList(keyStore: KeyStore):List<Certificate>{
        val aliases = keyStore.aliases()
        val list = ArrayList<Certificate>()
        for(alias in aliases) {
            val certificate = keyStore.getCertificate(alias)
            list.add(certificate)
        }
        return list
    }

    fun toCertStore(type:String="Collection", keyStore: KeyStore):CertStore{
        return CertStore.getInstance(type, CollectionCertStoreParameters(toList(keyStore)))
    }

    companion object{
        init {
            Security.insertProviderAt(BouncyCastleProvider(), 1)
        }
    }
}