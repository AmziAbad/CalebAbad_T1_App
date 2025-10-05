package edu.pe.cibertec.libromundoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.pe.cibertec.libromundoapp.model.Libro
import edu.pe.cibertec.libromundoapp.viewmodel.CarritoViewModel

@Composable
fun LibroItemCard(
    libro: Libro,
    viewModel: CarritoViewModel // Necesita la ViewModel para la acción de eliminar
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Título y detalles del libro
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Precio: S/.%.2f, Cantidad: %d".format(libro.precio, libro.cantidad),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Categoría: ${libro.categoria.nombre}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Ícono de Eliminar
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar libro",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        // Llama a la función de la ViewModel para iniciar la confirmación
                        viewModel.iniciarEliminacionLibro(libro)
                    },
                tint = MaterialTheme.colorScheme.error // Color rojo para acción peligrosa
            )
        }
    }
}