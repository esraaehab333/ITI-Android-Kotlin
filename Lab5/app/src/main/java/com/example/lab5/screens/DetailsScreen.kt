package com.example.lab5.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.lab5.dto.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(product: Product, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        DetailsContent(
            product = product,
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailsContent(product: Product, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        GlideImage(
            model = product.thumbnail,
            contentDescription = product.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(16.dp))
        Text(product.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("Price: $${product.price}", style = MaterialTheme.typography.bodyLarge)
        Text("Rating: ${product.rating}")
        Text("Stock: ${product.stock}")
        Text("Category: ${product.category}")
        Text("Brand: ${product.brand ?: "N/A"}")
        Spacer(Modifier.height(12.dp))
        Text("Description:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(product.description, style = MaterialTheme.typography.bodyMedium)
    }
}