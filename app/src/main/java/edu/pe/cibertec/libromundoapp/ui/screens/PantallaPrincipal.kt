package edu.pe.cibertec.libromundoapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.pe.cibertec.libromundoapp.model.Libro
import edu.pe.cibertec.libromundoapp.ui.components.Formulario
import edu.pe.cibertec.libromundoapp.ui.components.LibroItemCard
import edu.pe.cibertec.libromundoapp.ui.components.TotalDisplay
import edu.pe.cibertec.libromundoapp.viewmodel.CarritoViewModel
import edu.pe.cibertec.libromundoapp.viewmodel.Color
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    viewModel: CarritoViewModel = viewModel()
) {
    // Lectura de estados
    val librosEnCarrito = viewModel.librosEnCarrito
    val calculos = viewModel.calculos
    val notificacion = viewModel.notificacion

    // Estados para Hoisting del Formulario
    val tituloInput = viewModel.tituloInput
    val precioInput = viewModel.precioInput
    val cantidadInput = viewModel.cantidadInput
    val categoriaSeleccionada = viewModel.categoriaSeleccionada

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (calculos.mostrarResumenHabilitado) {
                FloatingActionButton(
                    onClick = { /* Aquí iría la lógica para mostrar un Dialog con el Resumen Final */ },
                    modifier = Modifier.size(70.dp) // Tamaño grande como en la imagen
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Mostrar Resumen")
                    Text("S/.%.2f".format(calculos.totalFinal), fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Permite el scroll de toda la columna
                .padding(paddingValues)
        ) {

            // 1. FORMULARIO DE ENTRADA (Hoisting)
            Formulario(
                titulo = tituloInput, onTituloChange = viewModel::actualizarTitulo,
                precio = precioInput, onPrecioChange = viewModel::actualizarPrecio,
                cantidad = cantidadInput, onCantidadChange = viewModel::actualizarCantidad,
                categoriaSeleccionada = categoriaSeleccionada, onCategoriaChange = viewModel::actualizarCategoria,
                onAgregarClick = viewModel::agregarLibroAlCarrito
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // 2. LISTA DE LIBROS EN EL CARRITO
            Text(
                text = "Libros en el Carrito",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (librosEnCarrito.isEmpty()) {
                Text(
                    text = "El carrito está vacío. Agrega un libro.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // Usamos LazyColumn para la lista (aunque la pantalla es Scrolleable, es mejor para listas grandes)
                // Nota: Podrías usar Column directamente si la lista es pequeña, pero LazyColumn es más eficiente.
                // Si usas LazyColumn, asegúrate de darle un alto fijo o un alto que se ajuste al contenido
                LazyColumn(
                    modifier = Modifier.height(200.dp) // Altura fija para la lista dentro del scroll vertical
                ) {
                    items(librosEnCarrito, key = { it.id }) { libro ->
                        LibroItemCard(libro = libro, viewModel = viewModel)
                    }
                }
            }

            // 3. SECCIÓN DE TOTALES Y BOTONES
            TotalDisplay(viewModel = viewModel)

        }
    }

    // --- 4. MANEJO DE ALERT DIALOGS (Validación y Confirmación) ---
    MostrarAlertDialogs(viewModel = viewModel)

    // --- 5. MANEJO DE SNACKBAR (Notificaciones) ---
    // Si hay una notificación pendiente, la muestra
    if (notificacion != null) {
        LaunchedEffect(notificacion) {
            val snackbarResult = scope.launch {
                snackbarHostState.showSnackbar(
                    message = notificacion.mensaje,
                    actionLabel = "OK",
                    duration = SnackbarDuration.Short
                )
            }
            // Limpia la notificación después de mostrarla o al hacer dismiss
            snackbarResult.invokeOnCompletion {
                viewModel.limpiarNotificacion()
            }
        }
    }
}

// Composable para gestionar todos los AlertDialogs de la ViewModel
@Composable
fun MostrarAlertDialogs(viewModel: CarritoViewModel) {
    val libroAConfirmar = viewModel.libroAConfirmarEliminacion
    val mensajeAlerta = viewModel.mostrarAlerta
    val confirmarLimpiar = viewModel.confirmarLimpiarCarrito

    // 1. Alerta de Validación/Error
    if (mensajeAlerta != null) {
        AlertDialog(
            onDismissRequest = viewModel::limpiarAlerta,
            title = { Text("Error de Validación") },
            text = { Text(mensajeAlerta) },
            confirmButton = { Button(onClick = viewModel::limpiarAlerta) { Text("Aceptar") } }
        )
    }

    // 2. Confirmación de Eliminación de Libro
    if (libroAConfirmar != null) {
        AlertDialog(
            onDismissRequest = { viewModel.confirmarEliminacionLibro(false) },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar el libro \"${libroAConfirmar.titulo}\" del carrito?") },
            confirmButton = {
                Button(onClick = { viewModel.confirmarEliminacionLibro(true) }) { Text("Sí, Eliminar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.confirmarEliminacionLibro(false) }) { Text("Cancelar") }
            }
        )
    }

    // 3. Confirmación de Limpiar Carrito
    if (confirmarLimpiar) {
        AlertDialog(
            onDismissRequest = { viewModel.confirmarLimpiarCarrito(false) },
            title = { Text("Confirmar Limpieza") },
            text = { Text("¿Estás seguro de que deseas limpiar completamente el carrito?") },
            confirmButton = {
                Button(onClick = { viewModel.confirmarLimpiarCarrito(true) }) { Text("Sí, Limpiar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.confirmarLimpiarCarrito(false) }) { Text("Cancelar") }
            }
        )
    }
}