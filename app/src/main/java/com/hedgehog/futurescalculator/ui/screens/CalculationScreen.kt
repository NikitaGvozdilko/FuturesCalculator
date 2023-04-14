@file:OptIn(ExperimentalComposeUiApi::class)

package com.hedgehog.futurescalculator.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hedgehog.futurescalculator.R
import com.hedgehog.futurescalculator.domain.model.Settings
import com.hedgehog.futurescalculator.ui.model.HistoryItemModel
import com.hedgehog.futurescalculator.ui.theme.ColorGreen
import com.hedgehog.futurescalculator.ui.theme.ColorRed

@Preview
@Composable
fun CalculationScreen(viewModel: MainViewModel = hiltViewModel()) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val state by viewModel.stateFlow.collectAsState(initial = MainViewModel.State.default())

    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(16.dp))

        ButtonsRow(position = state.position, onModeClicked = viewModel::updatePosition)

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Column {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Take profit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ColorGreen
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = state.takeProfit.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = ColorGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Stop loss",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ColorRed
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = state.stopLoss.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = ColorRed
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            OutlinedTextField(
                modifier = Modifier.width(130.dp),
                value = state.profitPercent.toString(),
                onValueChange = viewModel::updateProfitPercent,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = remember {
                    KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                },
                singleLine = true,
                label = { Text(text = "Profit") },
                placeholder = { Text(text = "Enter TP") },
                trailingIcon = {
                    Text(text = "%")
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                modifier = Modifier
                    .width(130.dp),
                value = state.lossPercent.toString(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = remember {
                    KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                },
                singleLine = true,
                onValueChange = viewModel::updateLossPercent,
                label = { Text(text = "Loss") },
                placeholder = { Text(text = "Enter SL") },
                trailingIcon = {
                    Text(text = "%")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        LeverageRow(
            leverage = state.leverage,
            onPlusClicked = viewModel::onAddLeverageClicked,
            onMinusClicked = viewModel::onMinusLeverageClicked
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                value = state.inputValue,
                keyboardOptions = remember {
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                },
                keyboardActions = remember {
                    KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    )
                },
                onValueChange = viewModel::updateInput,
                trailingIcon = remember {
                    {
                        Icon(
                            modifier = Modifier.clickable(onClick = { viewModel.updateInput("") }),
                            painter = painterResource(id = R.drawable.ic_clear),
                            contentDescription = ""
                        )
                    }
                },
                label = remember {
                    {
                        Text(text = "Entry price")
                    }
                })

            Button(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { viewModel.onSaveClicked() }) {
                Text(text = "Save")
            }
        }

        Spacer(modifier = Modifier.padding(top = 24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Text(
//                modifier = Modifier.weight(0.5f),
                text = "History", style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                modifier = Modifier.align(Alignment.Bottom).clickable(onClick = viewModel::onClearHistoryClicked),
                painter = painterResource(id = R.drawable.ic_delete), contentDescription = ""
            )
        }

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Black,
            thickness = 1.dp
        )

        HistoryList(state.historyList)

    }

}

@Preview(widthDp = 600, showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun HistoryList(historyList: List<HistoryItemModel> = emptyList()) {
    LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Cell("Entry")
                Cell("Profit %")
                Cell("Loss %")
                Cell("Lev.")
                Cell("Pos.")
                Cell("Profit")
                Cell("Loss")
            }
//            Row(modifier = Modifier.fillMaxWidth()) {
//                Cell("16000")
//                Cell("60%")
//                Cell("20%")
//                Cell("10x")
//                Cell("Long")
//                Cell("20000")
//                Cell("12000")
//            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//            ) {
//                Cell("16000")
//                Cell("60%")
//                Cell("20%")
//                Cell("10x")
//                Cell("Long")
//                Cell("20000")
//                Cell("12000")
//            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//            ) {
//                Cell("16000")
//                Cell("60%")
//                Cell("20%")
//                Cell("10x")
//                Cell("Long")
//                Cell("20000")
//                Cell("12000")
//            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//            ) {
//                Cell("16000")
//                Cell("60%")
//                Cell("20%")
//                Cell("10x")
//                Cell("Long")
//                Cell("20000")
//                Cell("12000")
//            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//            ) {
//                Cell("16000")
//                Cell("60%")
//                Cell("20%")
//                Cell("10x")
//                Cell("Long")
//                Cell("20000")
//                Cell("12000")
//            }
        }
        items(historyList) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Cell(item.entryPrice.toString())
                Cell("${item.profitPercent}%")
                Cell("${item.lossPercent}%")
                Cell("${item.leverage}x")
                Cell("${item.position}")
                Cell(item.profit.toString())
                Cell(item.loss.toString())
            }
        }
    }
}

@Composable
private fun RowScope.Cell(text: String) {
    Text(
        modifier = Modifier.Companion.weight(0.14f),
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ColumnScope.ButtonsRow(
    position: Settings.Position,
    onModeClicked: (Settings.Position) -> Unit
) {
    val longColor = if (position == Settings.Position.LONG) ColorGreen else Color.LightGray
    val shortColor = if (position == Settings.Position.SHORT) ColorRed else Color.LightGray
    val longColorAnim = animateColorAsState(targetValue = longColor)
    val shortColorAnim = animateColorAsState(targetValue = shortColor)
    Row(modifier = Modifier.Companion.align(Alignment.CenterHorizontally)) {
        Surface(
            modifier = Modifier.clickable { onModeClicked(Settings.Position.LONG) },
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
            modifier = Modifier.clickable { onModeClicked(Settings.Position.SHORT) },
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
    }
}