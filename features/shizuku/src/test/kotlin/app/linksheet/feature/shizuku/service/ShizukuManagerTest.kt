package app.linksheet.feature.shizuku.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import org.junit.Test

internal class ShizukuManagerTest {

    @Test
    fun `sha256 of empty bytes`() {
        assertThat(sha256Hex(ByteArray(0)))
            .isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
    }

    @Test
    fun `sha256 of abc`() {
        assertThat(sha256Hex("abc".toByteArray()))
            .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad")
    }

    @Test
    fun `sha256 is lowercase hex of 64 chars`() {
        val digest = sha256Hex("abc".toByteArray())

        assertThat(digest.length).isEqualTo(64)
        assertThat(digest).isEqualTo(digest.lowercase())
    }

    @Test
    fun `no package means no selection`() {
        assertThat(readManagerSelection(null, "cert")).isNull()
        assertThat(readManagerSelection("", "cert")).isNull()
        assertThat(readManagerSelection("   ", "cert")).isNull()
    }

    @Test
    fun `package and cert are kept`() {
        assertThat(readManagerSelection("com.example.manager", "abc123"))
            .isEqualTo(ManagerSelection("com.example.manager", "abc123"))
    }

    @Test
    fun `package without cert keeps an empty fingerprint`() {
        assertThat(readManagerSelection("com.example.manager", null))
            .isEqualTo(ManagerSelection("com.example.manager", ""))
    }

    @Test
    fun `without selection the original package is used`() {
        val check = checkManager(
            selection = null,
            isInstalled = { it == ORIGINAL_MANAGER_PACKAGE },
            currentCertSha256 = { null }
        )

        assertThat(check).isEqualTo(ManagerCheck.Default)
    }

    @Test
    fun `without selection a missing original package is not installed`() {
        val check = checkManager(
            selection = null,
            isInstalled = { false },
            currentCertSha256 = { null }
        )

        assertThat(check).isEqualTo(ManagerCheck.NotInstalled)
    }

    @Test
    fun `a selected package which is not installed is not installed`() {
        var certLookups = 0

        val check = checkManager(
            selection = ManagerSelection("com.example.manager", "abc123"),
            isInstalled = { false },
            currentCertSha256 = { certLookups++; "abc123" }
        )

        assertThat(check).isEqualTo(ManagerCheck.NotInstalled)
        assertThat(certLookups).isEqualTo(0)
    }

    @Test
    fun `a matching certificate is verified`() {
        val check = checkManager(
            selection = ManagerSelection("com.example.manager", "abc123"),
            isInstalled = { true },
            currentCertSha256 = { "abc123" }
        )

        assertThat(check).isEqualTo(ManagerCheck.Verified("com.example.manager"))
    }

    @Test
    fun `a different certificate is a mismatch`() {
        val check = checkManager(
            selection = ManagerSelection("com.example.manager", "abc123"),
            isInstalled = { true },
            currentCertSha256 = { "def456" }
        )

        assertThat(check).isEqualTo(ManagerCheck.CertMismatch)
    }

    @Test
    fun `a missing certificate is a mismatch`() {
        val check = checkManager(
            selection = ManagerSelection("com.example.manager", "abc123"),
            isInstalled = { true },
            currentCertSha256 = { null }
        )

        assertThat(check).isEqualTo(ManagerCheck.CertMismatch)
    }

    @Test
    fun `an empty stored certificate is a mismatch`() {
        val check = checkManager(
            selection = ManagerSelection("com.example.manager", ""),
            isInstalled = { true },
            currentCertSha256 = { "" }
        )

        assertThat(check).isEqualTo(ManagerCheck.CertMismatch)
    }

    @Test
    fun `the selected package is checked instead of the original one`() {
        val installedPackages = mutableListOf<String>()

        checkManager(
            selection = ManagerSelection("com.example.manager", "abc123"),
            isInstalled = { installedPackages.add(it); true },
            currentCertSha256 = { "abc123" }
        )

        assertThat(installedPackages).isEqualTo(listOf("com.example.manager"))
        assertThat(installedPackages.contains(ORIGINAL_MANAGER_PACKAGE)).isFalse()
    }

    @Test
    fun `only default and verified are usable`() {
        assertThat(ManagerCheck.Default.isUsable).isTrue()
        assertThat(ManagerCheck.Verified("com.example.manager").isUsable).isTrue()
        assertThat(ManagerCheck.NotInstalled.isUsable).isFalse()
        assertThat(ManagerCheck.CertMismatch.isUsable).isFalse()
    }

    @Test
    fun `the original manager package is the shizuku one`() {
        assertThat(ORIGINAL_MANAGER_PACKAGE).isEqualTo("moe.shizuku.privileged.api")
    }
}
