package edu.pe.cibertec.libromundoapp.ui.components

import androidx.compose.ui.graphics.Color

fun mapEnumColorToComposeColor(enumColor: edu.pe.cibertec.libromundoapp.viewmodel.Color): Color {
    return when (enumColor) {
        edu.pe.cibertec.libromundoapp.viewmodel.Color.GRIS -> Color.LightGray
        edu.pe.cibertec.libromundoapp.viewmodel.Color.VERDE -> Color(0xFF4CAF50)
        edu.pe.cibertec.libromundoapp.viewmodel.Color.AZUL -> Color(0xFF2196F3)
        edu.pe.cibertec.libromundoapp.viewmodel.Color.DORADO -> Color(0xFFFFC107)
        edu.pe.cibertec.libromundoapp.viewmodel.Color.ADVERTENCIA -> Color(0xFFFF9800)
        edu.pe.cibertec.libromundoapp.viewmodel.Color.INFO -> Color(0xFF03A9F4)
    }
}