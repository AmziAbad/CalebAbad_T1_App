package edu.pe.cibertec.libromundoapp.model

data class Libro (
    val id: Int,
    val titulo: String,
    val precio: Double,
    val cantidad: Int,
    val categoria: Categoria)
{
    val subtotal: Double
        get() = precio * cantidad
}
