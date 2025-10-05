package edu.pe.cibertec.libromundoapp.model

data class CompraLibros (
    val subtotalSinDescuento: Double = 0.00,
    val descuentoMonto: Double = 0.00,
    val descuentoPorcentaje: Int = 0,
    val totalFinal: Double = 0.00,
    val cantidadTotalLibros: Int = 0,
    val mostrarResumenHabilitado: Boolean = false
){

}