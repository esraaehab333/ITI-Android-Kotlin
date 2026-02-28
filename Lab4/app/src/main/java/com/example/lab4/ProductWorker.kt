package com.example.lab4
import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking

class ProductsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            val products = runBlocking {
                RetrofitProvider.api.getProducts().products
            }.take(20)

            val packed = products.joinToString(";;") { "${it.title}|${it.price}" }

            val output = Data.Builder()
                .putString("OUTPUT_PRODUCTS", packed)
                .putInt("OUTPUT_COUNT", products.size)
                .build()

            Result.success(output)
        } catch (e: Exception) {
            Result.failure()
        }
    }
}