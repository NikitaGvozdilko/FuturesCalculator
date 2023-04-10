package com.hedgehog.futurescalculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Preview
@Composable
fun CalculationScreen(viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.stateFlow.collectAsState(initial = MainViewModel.State.default())

    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(16.dp))

        ButtonsRow(mode = state.mode, onModeClicked = viewModel::updateMode)

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(
                text = "Take profit\n${state.takeProfit}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Green
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Stop loss\n${state.stopLoss}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red
            )
        }

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = state.profitPercent.toString(),
                onValueChange = viewModel::updateProfitPercent,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                label = { Text(text = "Profit") },
                placeholder = { Text(text = "Enter TP") }
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp),
                value = state.lossPercent.toString(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                onValueChange = viewModel::updateLossPercent,
                label = { Text(text = "Loss") },
                placeholder = { Text(text = "Enter SL") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        LeverageRow(
            leverage = state.leverage,
            onPlusClicked = viewModel::onAddLeverageClicked,
            onMinusClicked = viewModel::onMinusLeverageClicked
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            value = state.inputValue,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            onValueChange = viewModel::updateInput,
            label = {
                Text(text = "Entry price")
            })

    }

}

@Composable
private fun ColumnScope.ButtonsRow(
    mode: MainViewModel.State.Mode,
    onModeClicked: (MainViewModel.State.Mode) -> Unit
) {
    val longColor = if (mode == MainViewModel.State.Mode.LONG) Color.Green else Color.LightGray
    val shortColor = if (mode == MainViewModel.State.Mode.SHORT) Color.Red else Color.LightGray
    val longColorAnim = animateColorAsState(targetValue = longColor)
    val shortColorAnim = animateColorAsState(targetValue = shortColor)
    Row(modifier = Modifier.Companion.align(Alignment.CenterHorizontally)) {
        Surface(
            modifier = Modifier.clickable { onModeClicked(MainViewModel.State.Mode.LONG) },
            shape = RoundedCornerShape(4.dp),
            color = longColorAnim.value,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 10.dp),
                text = "Long",
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            modifier = Modifier.clickable { onModeClicked(MainViewModel.State.Mode.SHORT) },
            shape = RoundedCornerShape(4.dp),
            color = shortColorAnim.value
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 10.dp),
                text = "Short",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ColumnScope.LeverageRow(
    leverage: Int,
    onPlusClicked: () -> Unit,
    onMinusClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .align(Alignment.CenterHorizontally)
    ) {

        Text(text = "Leverage", style = MaterialTheme.typography.labelLarge)

//        Surface(
//
//        ) {
        Row {
            Icon(
                modifier = Modifier.clickable(onClick = onMinusClicked),
                painter = painterResource(id = R.drawable.ic_minus), contentDescription = ""
            )
            Text(
                modifier = Modifier.weight(1f),
                text = leverage.toString() + "x",
                textAlign = TextAlign.Center
            )
            Icon(
                modifier = Modifier.clickable(onClick = onPlusClicked),
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = ""
            )
        }
//        }
    }
}