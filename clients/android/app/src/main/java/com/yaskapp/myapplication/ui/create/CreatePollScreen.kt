package com.yaskapp.myapplication.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val YaskRed = Color(0xFFE53935)
private val ScreenBg = Color(0xFFF7F7F7)

@Composable
fun CreatePollScreen(
    onCreateClick: (String, List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var question by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Создать опрос",
            style = MaterialTheme.typography.headlineSmall,
            color = YaskRed
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Введите вопрос...") },
            shape = RoundedCornerShape(16.dp),
            singleLine = false,
            minLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Варианты ответа",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = option,
                    onValueChange = { newValue ->
                        options = options.toMutableList().also {
                            it[index] = newValue
                        }
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Вариант ${index + 1}") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (options.size > 2) {
                    IconButton(
                        onClick = {
                            options = options.toMutableList().also {
                                it.removeAt(index)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Удалить вариант"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        TextButton(
            onClick = {
                options = options + ""
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Добавить вариант")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val cleanedOptions = options.map { it.trim() }.filter { it.isNotBlank() }
                val cleanedQuestion = question.trim()

                if (cleanedQuestion.isNotBlank() && cleanedOptions.size >= 2) {
                    onCreateClick(cleanedQuestion, cleanedOptions)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = YaskRed)
        ) {
            Text("Создать опрос")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

