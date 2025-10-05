package edu.pe.cibertec.libromundoapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.pe.cibertec.libromundoapp.viewmodel.CarritoViewModel

@Composable
fun TotalDisplay(
    viewModel: CarritoViewModel
) {
    val calculos = viewModel.calculos // Lee el estado de cálculos

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // --- Sección de Totales ---
        TotalRow(label = "Subtotal", value = "S/.%.2f".format(calculos.subtotalSinDescuento))
        TotalRow(label = "Descuento (${calculos.descuentoPorcentaje}%)", value = "-S/.%.2f".format(calculos.descuentoMonto), isDiscount = true)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        // Fila del Total Final
        TotalRow(label = "Total", value = "S/.%.2f".format(calculos.totalFinal), isTotal = true)

        Spacer(modifier = Modifier.height(20.dp))

        // --- Botones Limpiar y Calcular ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                // Inicia el proceso de confirmación de limpieza (AlertDialog)
                onClick = viewModel::iniciarConfirmacionLimpiarCarrito,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text("Limpiar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                // Llama a la función de cálculo
                onClick = viewModel::calcularTotal,
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("Calcular")
            }
        }
    }
}

// Componente de fila reutilizable para Subtotal, Descuento y Total
@Composable
fun TotalRow(
    label: String,
    value: String,
    isTotal: Boolean = false,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = when {
                isTotal -> MaterialTheme.colorScheme.primary
                isDiscount -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            },
            fontWeight = if (isTotal) FontWeight.ExtraBold else FontWeight.Normal
        )
    }
}