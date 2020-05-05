package net.cassiolandim.kittychallenge.di

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Component
import net.cassiolandim.kittychallenge.MyApplication
import net.cassiolandim.kittychallenge.ui.main.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        KittensModule::class
    ]
)
interface AppComponent {

    val mainViewModel: MainViewModel

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}

inline fun <reified T : ViewModel> Fragment.createFragmentViewModel(crossinline factory: () -> T): T =
    T::class.java.let { clazz ->
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass == clazz) {
                    @Suppress("UNCHECKED_CAST")
                    return factory() as T
                }
                throw IllegalArgumentException("Unexpected argument: $modelClass")
            }
        }).get(clazz)
    }

fun Fragment.createMainViewModel() =
    createFragmentViewModel {
        (requireActivity().application as MyApplication).appComponent.mainViewModel
    }