package com.example.tugaspert4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.ui.graphics.vector.ImageVector


val DisplayBlue = Color(0xFF4C7BAF)
val ScientificButtonColor = Color(0xFF6A9BCF)
val ButtonBlue = Color(0xFFC7D9ED)
val OperatorLight = Color(0xFFF0F0F0)
val TextDark = Color(0xFF333333)
val BackgroundColor = Color(0xFFFFFFFF)
val SecondaryBackground = Color(0xFFF0F8FF)


sealed class Screen(val route: String) {
    object Menu : Screen("menu_screen")
    object Calculator : Screen("calculator_screen")
    object TextEditor : Screen("text_editor_screen")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.Menu.route
            ) {
                composable(Screen.Menu.route) {
                    MenuScreen(navController = navController)
                }

                composable(Screen.Calculator.route) {
                    val calculatorViewModel = viewModel<CalculatorViewModel>()
                    CalculatorScreen(viewModel = calculatorViewModel)
                }

                composable(Screen.TextEditor.route) {
                    val textEditorViewModel = viewModel<TextEditorViewModel>()
                    TextEditorScreen(viewModel = textEditorViewModel)
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val state = viewModel.state
    val buttonSpacing = 4.dp


    val finalBasicLayout = listOf(
        listOf("7", "8", "9", "%", "AC"),
        listOf("4", "5", "6", "×", "÷"),
        listOf("1", "2", "3", "-", "+"),
        listOf("ModeToggle", "0", ".", "±", "=")
    )

    val scientificLayout = listOf(
        listOf("sin", "cos", "tan", "(", ")"),
        listOf("sin⁻¹", "cos⁻¹", "tan⁻¹", "ln", "log"),
        listOf("x^y", "x³", "x²", "π", "e"),
        listOf("y√x", "3√x", "√x", "x!", "1/x"),
        finalBasicLayout[0],
        finalBasicLayout[1],
        finalBasicLayout[2],
        finalBasicLayout[3]
    )

    val layoutToDisplay = if (state.isScientificMode) scientificLayout else finalBasicLayout

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(buttonSpacing)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .background(DisplayBlue, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = state.fullExpression,
                    fontSize = 32.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End
                )

                Text(
                    text = state.displayInput,
                    fontSize = 64.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(buttonSpacing))

        fun getButtonColors(symbol: String, isInverse: Boolean): Pair<Color, Color> {
            val scientificFunctions = listOf(
                "sin", "cos", "tan", "sin⁻¹", "cos⁻¹", "tan⁻¹", "π", "e",
                "x^y", "x³", "x²", "y√x", "3√x", "√x", "ln", "log", "x!", "1/x",
                "(", ")"
            )

            val operators = listOf("+", "-", "×", "÷", "=")
            val controlFunctions = listOf("%", "AC", "C", "±", "ModeToggle")
            val numbersAndDecimals = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")

            val isScientificFunc = symbol in scientificFunctions
            val isOperator = symbol in operators
            val isControl = symbol in controlFunctions
            val isNumberOrDecimal = symbol in numbersAndDecimals

            return when {
                isScientificFunc -> Pair(ScientificButtonColor, Color.White)
                isOperator || isControl -> Pair(ButtonBlue, TextDark)
                isNumberOrDecimal -> Pair(OperatorLight, TextDark)
                else -> Pair(OperatorLight, TextDark)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            layoutToDisplay.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    row.forEachIndexed { colIndex, symbol ->
                        val (bgColor, contentColor) = getButtonColors(symbol, state.isInverse)
                        val weight = 1f

                        val buttonModifier = Modifier
                            .weight(weight)
                            .aspectRatio(1.0f)

                        CalculatorButton(
                            symbol = symbol,
                            isInverse = state.isInverse,
                            isScientificMode = state.isScientificMode,
                            modifier = buttonModifier,
                            backgroundColor = bgColor,
                            contentColor = contentColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            handleAction(viewModel, symbol)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(buttonSpacing))
            }
        }
    }
}

fun handleAction(viewModel: CalculatorViewModel, symbol: String) {
    when (symbol) {
        "Sci", "Bas", "ModeToggle" -> viewModel.onAction(CalculatorAction.ToggleMode)

        "π" -> viewModel.onAction(CalculatorAction.Pi)
        "e" -> viewModel.onAction(CalculatorAction.E_Const)

        "x^y" -> viewModel.onAction(CalculatorAction.PowerOf)
        "y√x" -> viewModel.onAction(CalculatorAction.YRootX)

        "x³" -> viewModel.onAction(CalculatorAction.Cube)
        "x²" -> viewModel.onAction(CalculatorAction.Square)
        "eˣ" -> viewModel.onAction(CalculatorAction.ExpE)
        "10ˣ" -> viewModel.onAction(CalculatorAction.Exp10)
        "√x" -> viewModel.onAction(CalculatorAction.SquareRoot)
        "3√x" -> viewModel.onAction(CalculatorAction.CubeRoot)
        "ln" -> viewModel.onAction(CalculatorAction.Ln)
        "log" -> viewModel.onAction(CalculatorAction.Log)
        "1/x" -> viewModel.onAction(CalculatorAction.Reciprocal)
        "x!" -> viewModel.onAction(CalculatorAction.Factorial)

        "F" -> println("Action F: Toggle secondary functions (Placeholder)")
        "MC" -> println("Action MC: Memory Clear (Placeholder)")
        "MR" -> println("Action MR: Memory Recall (Placeholder)")
        "M+" -> println("Action M+: Memory Add (Placeholder)")
        "M-" -> println("Action M-: Memory Subtract (Placeholder)")

        "sin", "cos", "tan", "sin⁻¹", "cos⁻¹", "tan⁻¹" -> {
            val isInverse = symbol.endsWith("⁻¹") || viewModel.state.isInverse
            val baseSymbol = symbol.replace("⁻¹", "")
            when(baseSymbol) {
                "sin" -> viewModel.onAction(CalculatorAction.Sin(inverse = isInverse))
                "cos" -> viewModel.onAction(CalculatorAction.Cos(inverse = isInverse))
                "tan" -> viewModel.onAction(CalculatorAction.Tan(inverse = isInverse))
            }
        }

        "AC" -> viewModel.onAction(CalculatorAction.AllClear)
        "C" -> viewModel.onAction(CalculatorAction.Clear)
        "±" -> viewModel.onAction(CalculatorAction.ToggleSign)
        "." -> viewModel.onAction(CalculatorAction.Decimal)
        "=" -> viewModel.onAction(CalculatorAction.Calculate)
        "(" -> viewModel.onAction(CalculatorAction.ParenthesisOpen)
        ")" -> viewModel.onAction(CalculatorAction.ParenthesisClose)
        "%" -> viewModel.onAction(CalculatorAction.Percentage)

        "+", "-", "×", "÷" -> {
            val op = when (symbol) {
                "×" -> '*'
                "÷" -> '/'
                else -> symbol.first()
            }
            viewModel.onAction(CalculatorAction.Operation(op))
        }
        else -> symbol.toIntOrNull()?.let { viewModel.onAction(CalculatorAction.Number(it)) }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    isInverse: Boolean,
    isScientificMode: Boolean,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color,
    shape: RoundedCornerShape,
    isWide: Boolean = false,
    onClick: () -> Unit
) {
    val modeToggleText = if (symbol == "ModeToggle") {
        if (isScientificMode) "Bas" else "Sci"
    } else {
        symbol
    }

    val displayText = when (modeToggleText) {
        "x^y" -> "xʸ"
        "y√x" -> "ʸ√x"
        "x³" -> "x³"
        "x²" -> "x²"
        "eˣ" -> "eˣ"
        "10ˣ" -> "10ˣ"
        "3√x" -> "³√x"
        "√x" -> "√x"
        "1/x" -> "1/x"
        "x!" -> "x!"
        "±" -> "±"
        "E" -> "E"
        "sin" -> if (isInverse) "sin⁻¹" else "sin"
        "cos" -> if (isInverse) "cos⁻¹" else "cos"
        "tan" -> if (isInverse) "tan⁻¹" else "tan"
        else -> modeToggleText
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .border(0.5.dp, Color.LightGray, shape)
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            fontSize = 16.sp,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}


@Composable
fun AppIcon(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(96.dp)
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MenuScreen(navController: NavController) {
    var screenBackground by remember { mutableStateOf(BackgroundColor) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    screenBackground = if (screenBackground == BackgroundColor) SecondaryBackground else BackgroundColor
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ColorLens,
                    contentDescription = "Change Background Color",
                    tint = TextDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            AppIcon(
                icon = Icons.Default.Calculate,
                label = "Calculator",
                backgroundColor = DisplayBlue,
                onClick = { navController.navigate(Screen.Calculator.route) }
            )

            Spacer(modifier = Modifier.width(16.dp))

            AppIcon(
                icon = Icons.Default.EditNote,
                label = "Notepad",
                backgroundColor = Color(0xFF6B8E23),
                onClick = { navController.navigate(Screen.TextEditor.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen(viewModel: TextEditorViewModel) {
    val state = viewModel.state
    val scrollState = rememberScrollState()

    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = state.text))
    }
    var menuExpanded: Boolean by remember { mutableStateOf(false) }

    var canvasBackground by remember { mutableStateOf(Color.White) }


    LaunchedEffect(state.text) {
        if (textFieldValueState.text != state.text) {
            textFieldValueState = textFieldValueState.copy(text = state.text)
        }
    }

    val editorTextStyle = LocalTextStyle.current.copy(
        fontSize = state.fontSize.sp,
        fontWeight = if (state.isBold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (state.isItalic) FontStyle.Italic else FontStyle.Normal
    )

    val menuActions: Map<String, () -> Unit> = mapOf(
        "New" to { viewModel.onAction(TextEditorAction.New) },
        "Save" to { viewModel.onAction(TextEditorAction.Save) },
        "Cut" to { viewModel.onAction(TextEditorAction.Cut) },
        "Copy" to { viewModel.onAction(TextEditorAction.Copy) },
        "Paste" to { viewModel.onAction(TextEditorAction.Paste) },
        "ToggleBold" to { viewModel.onAction(TextEditorAction.ToggleBold) },
        "ToggleItalic" to { viewModel.onAction(TextEditorAction.ToggleItalic) },
        "SizeUp" to { viewModel.onAction(TextEditorAction.ChangeFontSize(2)) },
        "SizeDown" to { viewModel.onAction(TextEditorAction.ChangeFontSize(-2)) },
    )

    val isTextSelected = !textFieldValueState.selection.collapsed

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0))
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Notepad",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    IconButton(
                        onClick = {
                            canvasBackground = if (canvasBackground == Color.White) Color(0xFFFAFAD2) else Color.White
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Change Canvas Color",
                            tint = Color.DarkGray
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F8F8))
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = menuActions["ToggleBold"]!!) {
                            Icon(Icons.Default.FormatBold, contentDescription = "Bold",
                                tint = if (state.isBold) Color.Blue else Color.DarkGray)
                        }
                        IconButton(onClick = menuActions["ToggleItalic"]!!) {
                            Icon(Icons.Default.FormatItalic, contentDescription = "Italic",
                                tint = if (state.isItalic) Color.Blue else Color.DarkGray)
                        }
                        Spacer(modifier = Modifier.width(4.dp))

                        TextButton(onClick = menuActions["SizeDown"]!!, enabled = state.fontSize > 12) {
                            Text("A-", color = Color.DarkGray, fontSize = 16.sp)
                        }
                        TextButton(onClick = menuActions["SizeUp"]!!, enabled = state.fontSize < 30) {
                            Text("A+", color = Color.DarkGray, fontSize = 16.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = menuActions["Cut"]!!,
                            enabled = isTextSelected
                        ) {
                            Icon(Icons.Default.ContentCut, contentDescription = "Cut", tint = Color.DarkGray)
                        }

                        IconButton(
                            onClick = menuActions["Copy"]!!,
                            enabled = isTextSelected
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.DarkGray)
                        }

                        IconButton(
                            onClick = menuActions["Paste"]!!
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = Color.DarkGray)
                        }

                        Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More actions", tint = Color.DarkGray)
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("New") },
                                    onClick = { menuActions["New"]!!(); menuExpanded = false },
                                    leadingIcon = { Icon(Icons.Default.Create, contentDescription = "New") }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (state.isUnsaved) "Save (Unsaved)" else "Save") },
                                    onClick = { menuActions["Save"]!!(); menuExpanded = false },
                                    leadingIcon = { Icon(Icons.Default.Save, contentDescription = "Save") }
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.LightGray)
            }
        }
    ) { paddingValues ->
        BasicTextField(
            value = textFieldValueState,
            onValueChange = { newTextFieldValue ->
                textFieldValueState = newTextFieldValue
                viewModel.onAction(TextEditorAction.UpdateText(newTextFieldValue.text))
                viewModel.onAction(TextEditorAction.UpdateSelection(newTextFieldValue.selection))
            },
            modifier = Modifier
                .fillMaxSize()
                .background(canvasBackground)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            textStyle = editorTextStyle,
        )
    }
}