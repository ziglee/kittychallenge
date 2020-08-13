package net.cassiolandim.kittychallenge

import dagger.hilt.android.testing.CustomTestApplication

@CustomTestApplication(BaseApplication::class)
interface HiltTestApplication {
}