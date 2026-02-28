package com.example.lab4
import ProductDto
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.*
import com.example.lab4.ui.theme.Lab4Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val workManager = WorkManager.getInstance(this)

        val request: WorkRequest = OneTimeWorkRequestBuilder<ProductsWorker>()
            .addTag("products-tag")
            .build()

        // Compose state
        val productsState = mutableStateOf<List<ProductDto>>(emptyList())
        val statusState = mutableStateOf<WorkInfo.State?>(null)
        val countState = mutableStateOf(0)

        // Enqueue work
        workManager.enqueue(request)

        // Observe work status
        workManager.getWorkInfoByIdLiveData(request.id)
            .observe(this) { info ->
                if (info == null) return@observe
                statusState.value = info.state

                if (info.state == WorkInfo.State.SUCCEEDED) {
                    val packed = info.outputData.getString("OUTPUT_PRODUCTS").orEmpty()
                    countState.value = info.outputData.getInt("OUTPUT_COUNT", 0)
                    productsState.value = unpackProducts(packed)
                }
            }

        // Compose UI
        setContent {
            Lab4Theme {
                ProductsScreen(
                    status = statusState.value,
                    count = countState.value,
                    products = productsState.value
                )
            }
        }
    }

    // Helper to unpack products from Worker
    private fun unpackProducts(packed: String): List<ProductDto> {
        if (packed.isBlank()) return emptyList()

        return packed.split(";;").mapNotNull { item ->
            val parts = item.split("|", limit = 2)
            if (parts.size < 2) return@mapNotNull null

            val title = parts[0]
            val price = parts[1].toDoubleOrNull() ?: return@mapNotNull null
            ProductDto(title, price)
        }
    }
}

@Composable
private fun ProductsScreen(
    status: WorkInfo.State?,
    count: Int,
    products: List<ProductDto>
) {
    Scaffold{ paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val statusText = when (status) {
                WorkInfo.State.ENQUEUED -> "Enqueued"
                WorkInfo.State.RUNNING -> "Running"
                WorkInfo.State.SUCCEEDED -> "Succeeded"
                WorkInfo.State.FAILED -> "Failed"
                else -> "Idle"
            }
            val statusColor = when (status) {
                WorkInfo.State.SUCCEEDED -> MaterialTheme.colorScheme.primary
                WorkInfo.State.RUNNING -> MaterialTheme.colorScheme.secondary
                WorkInfo.State.FAILED -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            }

            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Status: $statusText",
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Progress indicator
            if (status == WorkInfo.State.RUNNING || status == WorkInfo.State.ENQUEUED) {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(12.dp))
            }
            if (status == WorkInfo.State.SUCCEEDED) {
                Text(
                    "Fetched $count products",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { p ->
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = p.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "$${p.price}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}