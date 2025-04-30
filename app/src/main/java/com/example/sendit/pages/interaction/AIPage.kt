package com.example.sendit.pages.interaction

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sendit.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID

// Data class for chat messages
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val image: Bitmap? = null
)

// Main Composable for AI Page
@Composable
fun AIPage(modifier: Modifier = Modifier) {
    // Main values
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var serverAddress by remember { mutableStateOf("86.4.67.83:11434") }

    var userInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val viewModel = viewModel<AIPageViewModel>()
    var selectedImageBitmap = viewModel.selectedImageBitmap.value

    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()

    // Greeting message
    LaunchedEffect(Unit) {
        chatMessages.add(
            ChatMessage(
                content = "Hi! I'm your AI assistant. Send me an image or ask me a question.",
                isFromUser = false
            )
        )
    }

    // Auto-scroll
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val imageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                selectedImageBitmap = bitmap
            }
        }

    fun sendTextMessage() {
        if (userInput.isBlank() && selectedImageBitmap == null) return

        val inputText = userInput.trim()
        userInput = ""
        focusManager.clearFocus()

        // Add user message (with optional image)
        chatMessages.add(
            ChatMessage(
                content = if (inputText.isEmpty()) "[Image sent]" else inputText,
                isFromUser = true,
                image = selectedImageBitmap
            )
        )

        // Thinking message for AI response
        val thinkingMessageId = UUID.randomUUID().toString()
        chatMessages.add(
            ChatMessage(
                id = thinkingMessageId,
                content = "Thinking...",
                isFromUser = false
            )
        )

        isLoading = true

        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    if (selectedImageBitmap != null) {
                        val base64Image = bitmapToBase64(selectedImageBitmap!!)
                        sendImageToOllama(base64Image, serverAddress)
                    } else {
                        sendTextToOllama(inputText, serverAddress, chatMessages)
                    }
                }

                // Replace thinking message
                val index = chatMessages.indexOfFirst { it.id == thinkingMessageId }
                if (index >= 0) {
                    chatMessages[index] = ChatMessage(
                        id = thinkingMessageId,
                        content = response ?: "No response.",
                        isFromUser = false
                    )
                }
            } catch (e: Exception) {
                val index = chatMessages.indexOfFirst { it.id == thinkingMessageId }
                if (index >= 0) {
                    chatMessages[index] = ChatMessage(
                        id = thinkingMessageId,
                        content = "Error: ${e.message}",
                        isFromUser = false
                    )
                }
            } finally {
                isLoading = false
                selectedImageBitmap = null
            }
        }
    }

    // UI starts here
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("AI Assistant", style = MaterialTheme.typography.headlineSmall)
        }

        // Chat history
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chatMessages) { message ->
                    ChatMessageItem(message)
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
        }

        // Input Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Image thumbnail (if selected)
                selectedImageBitmap?.let { bitmap ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = { selectedImageBitmap = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { imageLauncher.launch("image/*") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.image_icon),
                            contentDescription = "Attach Image",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        placeholder = { Text("Type a message...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendTextMessage() })
                    )

                    IconButton(
                        onClick = { sendTextMessage() },
                        enabled = userInput.isNotBlank() || selectedImageBitmap != null
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = if (userInput.isBlank() && selectedImageBitmap == null)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// Chat message item
@Composable
fun ChatMessageItem(message: ChatMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isFromUser) {
                // AI avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.Top)
                ) {
                    Text(
                        text = "AI",
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column {
                // Message bubble
                Surface(
                    shape = RoundedCornerShape(
                        topStart = if (message.isFromUser) 16.dp else 0.dp,
                        topEnd = if (message.isFromUser) 0.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    ),
                    color = if (message.isFromUser)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Display image if attached
                        message.image?.let { img ->
                            Image(
                                bitmap = img.asImageBitmap(),
                                contentDescription = "Attached Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Message text
                        Text(
                            text = message.content,
                            color = if (message.isFromUser)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (message.isFromUser) {
                Spacer(modifier = Modifier.width(8.dp))
                // User avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary)
                        .align(Alignment.Top)
                ) {
                    Text(
                        text = "Me",
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Convert Bitmap to Base64 String
fun bitmapToBase64(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG,80, stream )
    val byteArray = stream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

// Post text to Ollama API
fun sendTextToOllama(text: String, serverAddress: String, chatHistory: List<ChatMessage>): String? {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val formattedHistory = buildString {
        chatHistory.forEach {
            append(if (it.isFromUser) "User: " else "AI: ")
            if (it.image != null) {
                append("[Image attached]\n")
            } else {
                append(it.content.trim() + "\n")
            }
        }
        append("User: $text\n")
        append("AI:")
    }

    val jsonRequest = JSONObject().apply {
        put("model", "gemma3:4b")
        put("prompt", formattedHistory)
        put("stream", false)
    }

    val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://$serverAddress/api/generate")
        .post(requestBody)
        .build()

    return try {
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    jsonResponse.optString("response", "No response content")
                }
            } else {
                "HTTP error ${response.code}: ${response.body?.string() ?: "Unknown error"}"
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Network error: ${e.message}"
    }
}

// Post image to Ollama API
fun sendImageToOllama(base64Image: String, serverAddress: String): String? {
    val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    // Format base64 into JSONArray
    val jsonRequest = JSONObject().apply {

        // Using Gemma3:4b, can use gemma3:4b for better response
        put("model", "gemma3:4b")
        put("prompt", "Describe this image in detail and ask a follow-up question about it.")
        put("stream", false)

        // Create the images array as expected by Ollama
        put("images", JSONArray().apply {
            put(base64Image)
        })
    }

    val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://$serverAddress/api/generate")
        .post(requestBody)
        .build()

    return try {
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    jsonResponse.optString("response", "No response content")
                }
            } else {
                "HTTP error ${response.code}: ${response.body?.string() ?: "Unknown error"}"
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Network error: ${e.message}"
    }
}