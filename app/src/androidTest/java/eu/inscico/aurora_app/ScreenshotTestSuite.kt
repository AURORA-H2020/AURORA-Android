package eu.inscico.aurora_app

import eu.inscico.aurora_app.languageTests.*
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    GermanScreenshotTest::class,
    EnglishScreenshotTest::class,
    SpanishScreenshotTest::class,
    PortugueseScreenshotTest::class,
    DanishScreenshotTest::class,
    SlovenianScreenshotTest::class,
    GermanScreenshotTest::class
)

class ComposeTestSuite
