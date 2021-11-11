package io.kma.results.readercccd.utils

import java.security.PrivateKey
import java.security.cert.Certificate

/**
 * Encapsulates the terminal key and associated certificate chain for terminal authentication.
 */
class EACCredentials(val privateKey: PrivateKey, val chain: Array<Certificate>)
