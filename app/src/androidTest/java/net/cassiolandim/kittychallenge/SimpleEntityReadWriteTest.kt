package net.cassiolandim.kittychallenge

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import net.cassiolandim.kittychallenge.database.AppDatabase
import net.cassiolandim.kittychallenge.database.FavoriteDatabaseModel
import net.cassiolandim.kittychallenge.database.FavoritesDao
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SimpleEntityReadWriteTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var favoritesDao: FavoritesDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        favoritesDao = db.favoritesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        testScope.runBlockingTest {
            val model = FavoriteDatabaseModel(
                id = "id1",
                imageId = "imgid1"
            )

            favoritesDao.insert(model)

            val list = favoritesDao.findAll()
            Assert.assertEquals(1, list.size)
            list.first().run {
                Assert.assertEquals("id1", id)
                Assert.assertEquals("imgid1", imageId)
            }
        }
    }
}