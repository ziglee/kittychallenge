package net.cassiolandim.kittychallenge.repository

import net.cassiolandim.kittychallenge.di.KittenDomainModel

interface KittensRepository {

    suspend fun search(page: Int): List<KittenDomainModel>
}