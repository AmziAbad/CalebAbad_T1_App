package edu.pe.cibertec.libromundoapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.pe.cibertec.libromundoapp.model.Categoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Formulario(
    titulo: String,
    onTituloChange: (String) -> Unit,
    precio: String,
    onPrecioChange: (String) -> Unit,
    cantidad: String,
    onCantidadChange: (String) -> Unit,
    categoriaSeleccionada: Categoria,
    onCategoriaChange: (Categoria) -> Unit,
    onAgregarClick: () -> Unit // Callback para el botón principal
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Agregar Libro", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(10.dp))

        // --- Campo: Título del Libro ---
        OutlinedTextField(
            value = titulo, // Lee el estado pasado
            onValueChange = onTituloChange, // Llama al callback pasado
            label = { Text("Título del Libro") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        // --- Campos: Precio y Cantidad ---
        Row( /* ... Controles de precio y cantidad similares ... */ ) {
            OutlinedTextField(
                value = precio,
                onValueChange = onPrecioChange,
                label = { Text("Precio Unitario") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = cantidad,
                onValueChange = onCantidadChange,
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        // --- Selector: Categoría ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                value = categoriaSeleccionada.nombre,
                onValueChange = { /* ... */ },
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                Categoria.entries.filter { it != Categoria.SELECCIONE }.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.nombre) },
                        onClick = {
                            onCategoriaChange(categoria) // Llama al callback de categoría
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // --- Botón: AGREGAR LIBRO ---
        Button(
            onClick = onAgregarClick, // Llama al callback de clic
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text("Agregar Libro", style = MaterialTheme.typography.titleMedium)
        }
    }
}