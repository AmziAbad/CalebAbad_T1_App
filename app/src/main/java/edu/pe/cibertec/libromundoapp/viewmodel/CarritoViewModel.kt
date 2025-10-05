package edu.pe.cibertec.libromundoapp.viewmodel

import androidx.lifecycle.ViewModel
import edu.pe.cibertec.libromundoapp.model.Categoria
import androidx.compose.runtime.*
import edu.pe.cibertec.libromundoapp.model.CompraLibros
import edu.pe.cibertec.libromundoapp.model.Libro

class CarritoViewModel : ViewModel(){
    private var nextLibroId: Int = 0
    data class Notificacion(
        val mensaje: String,
        val color: Color
    )

    /**
     * Carrito ViewModel
     * */
    var tituloInput by mutableStateOf("")
        private set
    var precioInput by mutableStateOf("")
        private set
    var cantidadInput by mutableStateOf("")
        private set
    var categoriaSeleccionada by mutableStateOf(Categoria.SELECCIONE)
        private set

    var librosEnCarrito by mutableStateOf(listOf<Libro>())
        private set

    var calculos by mutableStateOf(CompraLibros())
        private set

    var notificacion by mutableStateOf<Notificacion?>(null)
        private set


    var mostrarAlerta by mutableStateOf<String?>(null)
        private set

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

    fun actualizarPrecio(nuevoPrecio: String) {
        if (nuevoPrecio.matches(Regex("^\\d*\\.?\\d{0,2}$")) || nuevoPrecio.isEmpty()) {
            precioInput = nuevoPrecio
        }
    }

    fun actualizarCantidad(nuevaCantidad: String) {
        if (nuevaCantidad.matches(Regex("^\\d*$")) || nuevaCantidad.isEmpty()) {
            cantidadInput = nuevaCantidad
        }
    }

    fun actualizarCategoria(nuevaCategoria: Categoria) {
        categoriaSeleccionada = nuevaCategoria
    }

    fun limpiarNotificacion() {
        notificacion = null
    }

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

        val nuevoLibro = Libro(
            id = nextLibroId,
            titulo = tituloInput.trim(),
            precio = precio,
            cantidad = cantidad,
            categoria = categoriaSeleccionada
        )
        librosEnCarrito = librosEnCarrito + nuevoLibro

        notificacion = Notificacion("Libro agregado al carrito", Color.VERDE)

        tituloInput = ""
        precioInput = ""
        cantidadInput = ""

        if (calculos.mostrarResumenHabilitado) {
            _recalcularTotal()
        }
    }

    /**
     * BOTÓN: CALCULAR TOTAL
     **/

    fun calcularTotal() {
        if (librosEnCarrito.isEmpty()) {
            mostrarAlerta = "Debe haber al menos 1 libro en el carrito para calcular el total."
            calculos = calculos.copy(mostrarResumenHabilitado = false)
            return
        }

        notificacion = _recalcularTotal()
    }


    private fun _recalcularTotal(): Notificacion{
        val subtotalGeneral = librosEnCarrito.sumOf { it.subtotal }
        val cantidadTotalLibros = librosEnCarrito.sumOf { it.cantidad }

        val (descuentoPorcentaje, mensajeDescuento, colorDescuento) = when {
            cantidadTotalLibros >= 20 -> Triple(20, "¡Increíble! Ahorraste S/. XX", Color.DORADO)
            cantidadTotalLibros >= 10 -> Triple(15, "¡Excelente! Ahorraste S/. XX", Color.AZUL)
            cantidadTotalLibros >= 5 -> Triple(10, "¡Genial! Ahorraste S/. XX", Color.VERDE)
            else -> Triple(0, "No hay descuento aplicado", Color.GRIS)
        }

        val descuentoMonto = subtotalGeneral * (descuentoPorcentaje / 100.0)
        val totalFinal = subtotalGeneral - descuentoMonto

        calculos = CompraLibros(
            subtotalSinDescuento = subtotalGeneral,
            descuentoMonto = descuentoMonto,
            descuentoPorcentaje = descuentoPorcentaje,
            totalFinal = totalFinal,
            cantidadTotalLibros = cantidadTotalLibros,
            mostrarResumenHabilitado = true
        )

        val mensajeFinal = if (descuentoPorcentaje > 0) {
            mensajeDescuento.replace("XX", "%.2f".format(descuentoMonto))
        } else {
            mensajeDescuento
        }
        return Notificacion(mensajeFinal, colorDescuento)
    }

    /**
     * BOTÓN: LIMPIAR CARRITO
     * */

    fun iniciarConfirmacionLimpiarCarrito() {
        confirmarLimpiarCarrito = true
    }

    fun confirmarLimpiarCarrito(confirmado: Boolean) {
        confirmarLimpiarCarrito = false
        if (confirmado) {

            librosEnCarrito = emptyList()

            tituloInput = ""
            precioInput = ""
            cantidadInput = ""
            categoriaSeleccionada = Categoria.SELECCIONE

            calculos = CompraLibros(mostrarResumenHabilitado = false)

            notificacion = Notificacion("Carrito limpiado", Color.INFO)
        }
    }


    /**
     *  ELIMINAR LIBRO ESPECÍFICO
     * */

    fun iniciarEliminacionLibro(libro: Libro) {
        libroAConfirmarEliminacion = libro
    }

    fun confirmarEliminacionLibro(confirmado: Boolean) {
        val libro = libroAConfirmarEliminacion

        libroAConfirmarEliminacion = null

        if (confirmado && libro != null) {
            librosEnCarrito = librosEnCarrito.filter { it.id != libro.id }

            if (calculos.mostrarResumenHabilitado) {
                _recalcularTotal()
            } else if (librosEnCarrito.isEmpty()) {
                calculos = CompraLibros(mostrarResumenHabilitado = false)
            }

            notificacion = Notificacion("Libro eliminado del carrito", Color.ADVERTENCIA)
        }
    }
}