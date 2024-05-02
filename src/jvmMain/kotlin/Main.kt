import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.io.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    //Ruta del archivo
    val studentsFilePath = File("src/jvmMain/kotlin/students.txt").absolutePath
    val studentsFile = File(studentsFilePath)

    //Icono de la ventana
    val icon = painterResource("icon.png")

    // Arranca el programa y lee el contenido del archivo
    var students by remember { mutableStateOf(readStudentsFromFile(studentsFile)) }

    var newStudent by remember { mutableStateOf("") }
    val state = rememberLazyListState()

    Window(
        onCloseRequest = ::exitApplication,
        title = "My students",
        state = rememberWindowState(width = 600.dp, height = 600.dp),
        icon = icon
    ) {
        MaterialTheme {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.weight(1f)) {
                    Column(
                        Modifier.weight(1f).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(40.dp)
                    ) {
                        OutlinedTextField(
                            value = newStudent,
                            onValueChange = { newStudent = it },
                            label = { Text("New student name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (newStudent.isNotBlank()) {
                                    students = students + newStudent
                                    newStudent = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Add new student")
                        }
                    }
                    Box(
                        Modifier.weight(1f).padding(end = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Students: ${students.size}",
                                modifier = Modifier.padding(8.dp)
                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .border(1.dp, Color.Black)
                            ) {
                                LazyColumn(state = state) {
                                    itemsIndexed(students) { index, student ->
                                        TextBoxWithDeleteIcon(student) {
                                            // Elimin el estudiante de la posición
                                            students = students.toMutableList().apply { removeAt(index) }
                                        }
                                    }
                                }
                                VerticalScrollbar(
                                    adapter = rememberScrollbarAdapter(
                                        scrollState = state
                                    ),
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                            }
                            Button(
                                onClick = {
                                    // Vacia la lista de estudiantes
                                    if (students.isNotEmpty()) {
                                        students = emptyList()
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Clear All")
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        try {
                            studentsFile.writeText(students.joinToString("\n"))
                            println("Changes saved successfully.")
                        } catch (e: Exception) {
                            println("Error while saving changes: ${e.message}")
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

// Función que lee el contenido del archivo y lo devolvuelve como una lista
fun readStudentsFromFile(file: File): List<String> {
    val students = mutableListOf<String>()
    try {
        val reader = BufferedReader(FileReader(file))
        reader.useLines { lines ->
            lines.forEach { line ->
                students.add(line)
            }
        }
        println("Students content read from the file: $students")
    } catch (e: Exception) {
        println("Error while reading the file: ${e.message}")
    }
    return students
}

@Composable
fun TextBoxWithDeleteIcon(text: String, onDeleteClicked: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        TextBox(text = text, modifier = Modifier.weight(1f))
        IconButton(onClick = onDeleteClicked) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun TextBox(text: String = "Item", modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(32.dp)
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}
