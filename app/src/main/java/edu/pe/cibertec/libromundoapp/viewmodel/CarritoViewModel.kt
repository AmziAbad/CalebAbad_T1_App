package edu.pe.cibertec.libromundoapp.viewmodel

import androidx.lifecycle.ViewModel
import edu.pe.cibertec.libromundoapp.model.Categoria
import androidx.compose.runtime.*
import edu.pe.cibertec.libromundoapp.model.Libro
import java.util.*

class CarritoViewModel : ViewModel(){
    private var nextLibroId: Int = 0
    data class CarritoCalculos(

        val subtotalSinDescuento: Double = 0.00,
        val descuentoMonto: Double = 0.00,
        val descuentoPorcentaje: Int = 0,
        val totalFinal: Double = 0.00,
        val cantidadTotalLibros: Int = 0,
        val mostrarResumenHabilitado: Boolean = false
    )

    data class Notificacion(
        val mensaje: String,
        val color: Color
    )

    // Colores simplificados para la Snackbar (deberías usar androidx.compose.ui.graphics.Color)
    enum class Color { GRIS, VERDE, AZUL, DORADO, ADVERTENCIA, INFO }

    /**
     * Carrito ViewModel
     * */
    var tituloInput by mutableStateOf("")
        private set
    var precioInput by mutableStateOf("")
        private set // Usamos String para TextField, lo parsearemos a Double para el Libro
    var cantidadInput by mutableStateOf("")
        private set // Usamos String para TextField, lo parsearemos a Int para el Libro
    var categoriaSeleccionada by mutableStateOf(Categoria.SELECCIONE)
        private set

    // ----------------------------------------------------------------------
    // ESTADOS DEL CARRITO Y CÁLCULOS
    // ----------------------------------------------------------------------

    // Estado del Carrito: Lista de Libros (LazyColumn)
    var librosEnCarrito by mutableStateOf(listOf<Libro>())
        private set

    // Cálculos
    var calculos by mutableStateOf(CarritoCalculos())
        private set

    // Notificación (Snackbar)
    var notificacion by mutableStateOf<Notificacion?>(null)
        private set

    // Diálogo (AlertDialog)
    var mostrarAlerta by mutableStateOf<String?>(null)
        private set

    // Estado para la confirmación de limpieza (AlertDialog de confirmación)
    var libroAConfirmarEliminacion by mutableStateOf<Libro?>(null)
        private set
    var confirmarLimpiarCarrito by mutableStateOf(false)
        private set

/**
 * FUNCIONES PARA MANEJAR ESTADO DE DATOS
 * */

    fun actualizarTitulo(nuevoTitulo: String) {
        tituloInput = nuevoTitulo
    }

    // Utilice el Regex para permitir solo números y decimales en precio
    fun actualizarPrecio(nuevoPrecio: String) {
        if (nuevoPrecio.matches(Regex("^\\d*\\.?\\d{0,2}$")) || nuevoPrecio.isEmpty()) {
            precioInput = nuevoPrecio
        }
    }

    // Usar Regex para permitir solo números enteros en cantidad
    fun actualizarCantidad(nuevaCantidad: String) {
        if (nuevaCantidad.matches(Regex("^\\d*$")) || nuevaCantidad.isEmpty()) {
            cantidadInput = nuevaCantidad
        }
    }

    //opcional
    fun actualizarCategoria(nuevaCategoria: Categoria) {
        categoriaSeleccionada = nuevaCategoria
    }

    // Limpia la notificación después de mostrarla
    fun limpiarNotificacion() {
        notificacion = null
    }

    // Limpia la alerta después de mostrarla
    fun limpiarAlerta() {
        mostrarAlerta = null
    }

    /**
     * BOTÓN: AGREGAR AL CARRITO :)
     * */

    fun agregarLibroAlCarrito() {
        nextLibroId++

        val precio = precioInput.toDoubleOrNull() ?: 0.0
        val cantidad = cantidadInput.toIntOrNull() ?: 0

        // 1. Validaciones
        if (tituloInput.isBlank()) {
            mostrarAlerta = "Debe ingresar el título del libro."
            return
        }
        if (precio <= 0.0 && cantidad <= 0) {
            mostrarAlerta = "Precio y Cantidad deben ser mayores a 0."
            return
        }
        if (categoriaSeleccionada == Categoria.SELECCIONE) {
            mostrarAlerta = "Debe seleccionar una categoría."
            return
        }

        // 2. Acción: Agregar el libro a la lista
        val nuevoLibro = Libro(
            id = nextLibroId,
            titulo = tituloInput.trim(),
            precio = precio,
            cantidad = cantidad,
            categoria = categoriaSeleccionada
        )
        librosEnCarrito = librosEnCarrito + nuevoLibro // el array list

        // 3. Mostrar Snackbar de ÉXITO
        notificacion = Notificacion("Libro agregado al carrito", Color.VERDE)

        // 4. Limpiar los campos de entrada
        tituloInput = ""
        precioInput = ""
        cantidadInput = ""

        // Nota: El cálculo del subtotal del libro (precio x cantidad) ya está
        // en la data class Libro como propiedad 'subtotal'.

        // Recalcular el total después de agregar un libro
        if (calculos.mostrarResumenHabilitado) {
            _recalcularTotal()
        }
    }

    // ----------------------------------------------------------------------
    // LÓGICA DEL BOTÓN: CALCULAR TOTAL
    // ----------------------------------------------------------------------

    fun calcularTotal() {
        // 1. Validación: Verificar que haya al menos 1 libro en el carrito
        if (librosEnCarrito.isEmpty()) {
            mostrarAlerta = "Debe haber al menos 1 libro en el carrito para calcular el total."
            // Deshabilitar botón MOSTRAR RESUMEN si estaba habilitado
            calculos = calculos.copy(mostrarResumenHabilitado = false)
            return
        }

        _recalcularTotal() // Llamada a la función privada de cálculo
    }

    // Función privada para el cálculo, usada por calcularTotal() y eliminarLibro()
    private fun _recalcularTotal() {
        // Cálculo: Sumar todos los subtotales y cantidad total
        val subtotalGeneral = librosEnCarrito.sumOf { it.subtotal }
        val cantidadTotalLibros = librosEnCarrito.sumOf { it.cantidad }

        // Aplicar descuento según tabla
        val (descuentoPorcentaje, mensajeDescuento, colorDescuento) = when {
            cantidadTotalLibros >= 20 -> Triple(20, "¡Increíble! Ahorraste S/. XX", Color.DORADO)
            cantidadTotalLibros >= 10 -> Triple(15, "¡Excelente! Ahorraste S/. XX", Color.AZUL)
            cantidadTotalLibros >= 5 -> Triple(10, "¡Genial! Ahorraste S/. XX", Color.VERDE)
            else -> Triple(0, "No hay descuento aplicado", Color.GRIS)
        }

        val descuentoMonto = subtotalGeneral * (descuentoPorcentaje / 100.0)
        val totalFinal = subtotalGeneral - descuentoMonto

        // 4. Mostrar: Actualizar el estado de cálculos
        calculos = CarritoCalculos(
            subtotalSinDescuento = subtotalGeneral,
            descuentoMonto = descuentoMonto,
            descuentoPorcentaje = descuentoPorcentaje,
            totalFinal = totalFinal,
            cantidadTotalLibros = cantidadTotalLibros,
            mostrarResumenHabilitado = true // Habilitar MOSTRAR RESUMEN
        )

        // Notificación según descuento
        val mensajeFinal = if (descuentoPorcentaje > 0) {
            mensajeDescuento.replace("XX", "%.2f".format(descuentoMonto))
        } else {
            mensajeDescuento
        }
        notificacion = Notificacion(mensajeFinal, colorDescuento)
    }

    // ----------------------------------------------------------------------
    // LÓGICA DEL BOTÓN: LIMPIAR CARRITO
    // ----------------------------------------------------------------------

    fun iniciarConfirmacionLimpiarCarrito() {
        // Abre el AlertDialog de confirmación
        confirmarLimpiarCarrito = true
    }

    fun confirmarLimpiarCarrito(confirmado: Boolean) {
        confirmarLimpiarCarrito = false
        if (confirmado) {
            // Eliminar todos los libros del carrito
            librosEnCarrito = emptyList()

            // Limpiar campos de entrada (por si no estaban limpios)
            tituloInput = ""
            precioInput = ""
            cantidadInput = ""
            categoriaSeleccionada = Categoria.SELECCIONE

            // Resetear cálculos a S/. 0.00 y deshabilitar botón MOSTRAR RESUMEN
            calculos = CarritoCalculos(mostrarResumenHabilitado = false)

            // Mostrar Snackbar de INFO
            notificacion = Notificacion("Carrito limpiado", Color.INFO)
        }
    }


    /**
     *  ÍCONO EN UN LIBRO: ELIMINAR LIBRO ESPECÍFICO
     * */

    fun iniciarEliminacionLibro(libro: Libro) {
        libroAConfirmarEliminacion = libro
    }

    fun confirmarEliminacionLibro(confirmado: Boolean) {
        val libro = libroAConfirmarEliminacion

        libroAConfirmarEliminacion = null // Limpiamos el estado del libro

        if (confirmado && libro != null) {
            // Eliminar el libro específico
            librosEnCarrito = librosEnCarrito.filter { it.id != libro.id }

            // Recalcular automáticamente el total (solo si ya se había calculado antes)
            if (calculos.mostrarResumenHabilitado) {
                _recalcularTotal()
            } else if (librosEnCarrito.isEmpty()) {
                // Si se elimina el último libro, resetea los cálculos
                calculos = CarritoCalculos(mostrarResumenHabilitado = false)
            }

            // Mostrar Snackbar de ADVERTENCIA
            notificacion = Notificacion("Libro eliminado del carrito", Color.ADVERTENCIA)
        }
    }
}