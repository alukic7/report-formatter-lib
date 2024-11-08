plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "KomponenteProjekatPrvi"
include("SPI")
include("CSVFormatterImpl")
include("test")
include("TXTFormatterImpl")
include("drugaImplm")
include("testApp")
include("PDFFormatterImpl")
include("XLSXFormatterImpl")
