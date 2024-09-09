package com.muedsa.agetv.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            // This block defines the app's critical user journey. Here we are interested in
            // optimizing for app startup. But you can also navigate and scroll through your most important UI.

            // Start default activity for your app
            pressHome()
            startActivityAndWait()

            device.waitForIdle()
            device.run {
                // 等待首页加载
                wait(
                    Until.findObject(By.res("mainScreen_row_1")),
                    INITIAL_WAIT_TIMEOUT
                )
                // 浏览首页
                repeat(2 + 7) {
                    pressDPadDown(); waitForIdle(WAIT_TIMEOUT)
                    repeat(8) { pressDPadRight(); waitForIdle(WAIT_TIMEOUT) }
                    repeat(8) { pressDPadLeft(); waitForIdle(WAIT_TIMEOUT) }
                }
                repeat(2 + 7 + 1) { pressDPadUp(); waitForIdle(WAIT_TIMEOUT) }

                // 导航到排行
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                pressDPadCenter(); waitForIdle(WAIT_TIMEOUT)
                // 等待排行页面加载完成
                wait(
                    Until.findObject(By.res("rankScreen_col_week")),
                    INITIAL_WAIT_TIMEOUT
                )
                // 浏览排行
                repeat(3) { pressDPadDown(); waitForIdle(WAIT_TIMEOUT) }
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                repeat(2) { pressDPadDown(); waitForIdle(WAIT_TIMEOUT) }
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                repeat(2) { pressDPadDown(); waitForIdle(WAIT_TIMEOUT) }
                repeat(3 + 2 + 2) { pressDPadUp(); waitForIdle(WAIT_TIMEOUT) }

                // 导航到更新
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                pressDPadCenter(); waitForIdle(WAIT_TIMEOUT)
                // 等待更新页面加载完成
                wait(
                    Until.findObject(By.res("latestUpdateScreen_grid")),
                    INITIAL_WAIT_TIMEOUT
                )
                // 浏览更新
                repeat(3) { pressDPadDown(); waitForIdle(WAIT_TIMEOUT) }
                repeat(3) { pressDPadUp(); waitForIdle(WAIT_TIMEOUT) }

                // 导航到推荐
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                pressDPadCenter(); waitForIdle(WAIT_TIMEOUT)
                // 等待推荐页面加载完成
                wait(
                    Until.findObject(By.res("recommendScreen_grid")),
                    INITIAL_WAIT_TIMEOUT
                )
                // 浏览推荐
                repeat(3) { pressDPadDown(); waitForIdle(WAIT_TIMEOUT) }
                repeat(3) { pressDPadUp(); waitForIdle(WAIT_TIMEOUT) }


                // 导航到搜索
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                pressDPadCenter(); waitForIdle(WAIT_TIMEOUT)
                wait(
                    Until.findObject(By.res("searchScreen_searchButton")),
                    INITIAL_WAIT_TIMEOUT
                )

                // 导航到收藏
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                pressDPadCenter(); waitForIdle(WAIT_TIMEOUT)
                // 等待收藏页面加载完成
                wait(
                    Until.findObject(By.res("favoritesScreen_deleteModeButton")),
                    INITIAL_WAIT_TIMEOUT
                )
                // 浏览收藏
                pressDPadDown(); waitForIdle(WAIT_TIMEOUT)
                pressDPadUp(); waitForIdle(WAIT_TIMEOUT)

                // 导航到目录
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                pressDPadCenter(); waitForIdle(WAIT_TIMEOUT)
                // 等待目录加载完成
                wait(
                    Until.findObject(By.res("catalogScreen_grid")),
                    INITIAL_WAIT_TIMEOUT
                )
                // 浏览目录
                pressDPadDown(); waitForIdle(WAIT_TIMEOUT)
                pressDPadDown(); waitForIdle(WAIT_TIMEOUT)
                pressDPadRight(); waitForIdle(WAIT_TIMEOUT)
                pressDPadLeft(); waitForIdle(WAIT_TIMEOUT)

                // 进入视频详情
                pressDPadCenter(); waitForIdle(WAIT_TIMEOUT)
                // 等待视频详情页面加载完成
                wait(
                    Until.findObject(By.res("animeDetailScreen_episodeListWidget")),
                    INITIAL_WAIT_TIMEOUT
                )
                repeat(2) { pressDPadDown(); waitForIdle(WAIT_TIMEOUT) }
                repeat(2) { pressDPadUp(); waitForIdle(WAIT_TIMEOUT) }
            }
        }
    }
}

private const val INITIAL_WAIT_TIMEOUT = 5000L
private const val WAIT_TIMEOUT = 2000L