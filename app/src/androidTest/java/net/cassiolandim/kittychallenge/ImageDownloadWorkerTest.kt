package net.cassiolandim.kittychallenge

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import net.cassiolandim.kittychallenge.work.ImageDownloadWorker
import org.junit.*
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ImageDownloadWorkerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var context: Context
    private lateinit var outputDirectory: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        outputDirectory = context.getOutputDirectory()
    }

    @Test
    fun given_a_valid_url_When_executing_work_Should_exist_the_file() {
        val favoriteId = "1"
        val file = File(outputDirectory, "$favoriteId.jpg").apply {
            delete()
        }

        val worker = TestListenableWorkerBuilder<ImageDownloadWorker>(context,
            inputData = workDataOf(
                ImageDownloadWorker.KEY_FAVORITE_ID to favoriteId,
                ImageDownloadWorker.KEY_URL to "https://commons.wikimedia.org/wiki/File:Cat03.jpg"
            ))
            .build()

        runBlocking {
            Assert.assertFalse(file.exists())
            val result = worker.doWork()
            Assert.assertEquals(ListenableWorker.Result.success(), result)
            Assert.assertTrue(file.exists())
            file.delete()
        }
    }

    @Test
    fun given_a_invalid_url_When_executing_work_Should_return_failure() {
        val favoriteId = "1"
        val file = File(outputDirectory, "$favoriteId.jpg").apply {
            delete()
        }

        val worker = TestListenableWorkerBuilder<ImageDownloadWorker>(context,
            inputData = workDataOf(
                ImageDownloadWorker.KEY_FAVORITE_ID to favoriteId,
                ImageDownloadWorker.KEY_URL to "https://invalid.url"
            ))
            .build()

        runBlocking {
            Assert.assertFalse(file.exists())
            val result = worker.doWork()
            Assert.assertEquals(ListenableWorker.Result.failure(), result)
            Assert.assertFalse(file.exists())
            file.delete()
        }
    }
}