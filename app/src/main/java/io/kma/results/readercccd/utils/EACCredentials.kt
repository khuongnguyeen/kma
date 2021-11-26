package io.kma.results.readercccd.utils

import java.security.PrivateKey
import java.security.cert.Certificate

class EACCredentials(val privateKey: PrivateKey, val chain: Array<Certificate>)
