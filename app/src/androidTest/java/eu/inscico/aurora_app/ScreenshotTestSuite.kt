package eu.inscico.aurora_app

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    GermanScreenshotTest::class,
    EnglishScreenshotTest::class,
    SpanishScreenshotTest::class,
    PortugueseScreenshotTest::class,
    DanishScreenshotTest::class,
    SlovenianScreenshotTest::class
)

class ComposeTestSuite
