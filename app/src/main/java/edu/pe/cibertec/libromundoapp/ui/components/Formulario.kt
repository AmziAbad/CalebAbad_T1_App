package edu.pe.cibertec.libromundoapp.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExposedDropdownMenuBox
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
    onAgregarClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Agregar Libro", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = onTituloChange,
            label = { Text("Título del Libro") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row() {
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

        // --- Categoría ---
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
                            onCategoriaChange(categoria)
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // --- Botón: AGREGAR LIBRO ---
        Button(
            onClick = onAgregarClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text("Agregar Libro", style = MaterialTheme.typography.titleMedium)
        }
    }
}