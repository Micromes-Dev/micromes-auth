package net.micromes.auth

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

fun loadKeyPair() : PrivateKey {

    val dateiPriK = File("keys/private.key")

    var fis = FileInputStream(dateiPriK)
    val encodedPrivateKey = ByteArray(dateiPriK.length().toInt())
    fis.read(encodedPrivateKey)
    fis.close()

    var keyFactory = KeyFactory.getInstance("RSA")
    val privateKeySpec = PKCS8EncodedKeySpec(encodedPrivateKey)
    val privateKey = keyFactory.generatePrivate(privateKeySpec)

    val dateiPubK = File("keys/public.key")


    fis = FileInputStream(dateiPubK)
    val encodedPublicKey = ByteArray(dateiPubK.length().toInt())
    fis.read(encodedPublicKey)
    fis.close()

    keyFactory = KeyFactory.getInstance("RSA")
    val publicKeySpec = X509EncodedKeySpec(encodedPublicKey)
    val publicKey = keyFactory.generatePublic(publicKeySpec)

    return privateKey
}

fun generateKeyPair() {

    val dir = File("keys")

    dir.mkdirs()

    val keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256)

    val privateKey = keyPair.private
    val publicKey = keyPair.public

    val x509EncodedKeySpec = X509EncodedKeySpec(publicKey.encoded)
    var fos = FileOutputStream(dir.absoluteFile.toString() + "/public.key")
    fos.write(x509EncodedKeySpec.encoded)
    fos.close()

    val pkcs8EncodedKeySpec = PKCS8EncodedKeySpec(privateKey.encoded)
    fos = FileOutputStream(dir.absoluteFile.toString() + "/private.key")
    fos.write(pkcs8EncodedKeySpec.encoded)
    fos.close()
}