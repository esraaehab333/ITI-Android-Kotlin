package com.example.lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab.ui.theme.LabTheme
class MainActivity : ComponentActivity() {
    val cakes = listOf(
        Cake(R.drawable.cake,"One","Chocolate cake"),
        Cake(R.drawable.cake,"Two","Strawberry cake"),
        Cake(R.drawable.cake,"Three","Vanilla cake"),
        Cake(R.drawable.cake,"Four","Caramel cake"),
        Cake(R.drawable.cake,"Five","Oreo cake"),
        Cake(R.drawable.cake,"Six","Red velvet cake"),
        Cake(R.drawable.cake,"Seven","Fruit cake"),
        Cake(R.drawable.cake,"Eight","Coffee cake"),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ListOfCakes(
                        cakes = cakes,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ListOfCakes(
    cakes: List<Cake>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(cakes) { cake ->
            CakeItemRow(cake)
        }
    }
}

@Composable
fun CakeItemRow(cake: Cake) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = cake.image),
            contentDescription = cake.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = cake.title,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = cake.description,
                fontSize = 16.sp
            )
        }
    }
}
