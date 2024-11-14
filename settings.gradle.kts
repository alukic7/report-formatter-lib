plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "report-formatter-lib-master"
include("SPI")
include("CSVFormatterImpl")
include("test")
include("TXTFormatterImpl")
include("testApp")
include("PDFFormatterImpl")
include("XLSXFormatterImpl")
include("StyleConfig")
include("CalculationConfig")
