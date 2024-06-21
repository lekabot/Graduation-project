package com.example.front

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.front.data.Common.fileExists
import com.example.front.data.Common.stringToPath
import com.example.front.data.ParameterAPI
import com.example.front.ui.theme.FrontTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

val param_api = ParameterAPI()

class ThingParameters : ComponentActivity() {

    private lateinit var itemTitle: String

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            if (::itemTitle.isInitialized) {
                val file = uriToFile(it)
                param_api.postDocument(file, itemTitle)
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            if (::itemTitle.isInitialized) {
                val bitmap = uriToBitmap(it)
                param_api.postImg(bitmap, itemTitle, "image.png")
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        val fileName = getFileName(uri)
        val tempFile = File(applicationContext.cacheDir, fileName)
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        return tempFile
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "tempFile"
    }


    private fun uriToBitmap(uri: Uri): Bitmap {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        itemTitle = intent.getStringExtra("item_title") ?: ""

        setContent {
            FrontTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { ThingAppBar(title = itemTitle) }) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        MainScreen(
                            thingTitle = itemTitle,
                            onPickDocument = {
                                pickFileLauncher.launch("*/*")
                            },
                            onPickImage = {
                                pickImageLauncher.launch("image/*")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterSquare(
    parameter: ParameterAPI.Parameter,
    modifier: Modifier = Modifier,
    onDelete: (ParameterAPI.Parameter) -> Unit,
    onUpdate: (ParameterAPI.Parameter, String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var newText by remember { mutableStateOf(parameter.value) }
    val context = LocalContext.current

    Box(
        modifier = modifier
            .border(2.dp, Color(0xFF62519F), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        if (isEditing) {
            BasicTextField(
                value = newText,
                onValueChange = { newText = it },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .wrapContentSize(Alignment.Center),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newText.isNotEmpty()) {
                            onUpdate(parameter, newText)
                            isEditing = false
                        }
                    }
                ),
                singleLine = true,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (parameter.key) {
                    "document", "image", "qr" -> {
                        if (fileExists(parameter.value)) {
                            if (parameter.key == "image" || parameter.key == "qr") {
                                DisplayImage(parameter.value, param_api)
                            } else {
                                val fileName = getFilenameFromPath(parameter.value)
                                val fileFormat = getFileFormat(fileName)
                                Text(
                                    text = fileName,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .clickable {
                                            val responseBody =
                                                param_api.getFile(stringToPath(parameter.value))
                                            runBlocking {
                                                val file = convertResponseToFile(
                                                    responseBody,
                                                    context,
                                                    fileName
                                                )
                                                if (file != null) {
                                                    offerFileDownload(
                                                        file = file,
                                                        context = context
                                                    )
                                                    openFileForUser(
                                                        file = file,
                                                        mimeType = fileFormat,
                                                        context = context
                                                    )

                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                    else -> {
                        IconButton(onClick = { onDelete(parameter) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete"
                            )
                        }
                        Text(
                            text = "${parameter.key}: ${parameter.value}",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit"
                            )
                        }
                    }
                }
            }
        }
    }
}

suspend fun convertToBitmap(responseBody: ResponseBody?): Bitmap? {
    return withContext(Dispatchers.IO) {
        responseBody?.byteStream()?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }
}

@Composable
fun DisplayImage(parameterValue: String, paramApi: ParameterAPI) {
    val imageBitmap by produceState<Bitmap?>(initialValue = null, parameterValue) {
        value = try {
            val responseBody = paramApi.getFile(stringToPath(parameterValue))
            convertToBitmap(responseBody)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ImageLoading", "Error loading image: ${e.message}")
            null
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.align(Alignment.Center)
            )
        } ?: run {
            Log.e("ImageConversion", "Failed to convert response body to bitmap.")
        }
    }
}



fun getFileFormat(path: String): String {
    return path.substring(path.lastIndexOf(".") + 1)
}

fun getFilenameFromPath(path: String): String {
    return path.substring(path.lastIndexOf("/") + 1)
}

suspend fun convertResponseToFile(responseBody: ResponseBody?, context: Context, fileName: String): File? {
    return withContext(Dispatchers.IO) {
        if (responseBody == null) {
            return@withContext null
        }

        val file = File(context.cacheDir, fileName)
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = responseBody.byteStream()
            outputStream = FileOutputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            return@withContext file
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
}


fun offerFileDownload(file: File, context: Context) {
    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, context.contentResolver.getType(uri))
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val options = Bundle()

    try {
        context.startActivity(intent, options)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Не удалось найти приложение для загрузки файла", Toast.LENGTH_LONG).show()
    }
}

fun openFileForUser(file: File, mimeType: String = "application/pdf", context: Context) {
    val fileUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    val openFileIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(fileUri, mimeType)
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    try {
        context.startActivity(openFileIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Не найдено приложение для открытия файла", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun AddParameterButton(
    thingTitle: String,
    createParameter: (String, String, String) -> Unit,
    onPickDocument: (String) -> Unit,
    onPickImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isKeyInputVisible by remember { mutableStateOf(false) }
    var isValueInputVisible by remember { mutableStateOf(false) }
    var keyText by remember { mutableStateOf("") }
    var valueText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .border(2.dp, Color(0xFF62519F), RoundedCornerShape(12.dp))
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isValueInputVisible -> {
                    when (keyText) {
                        "document" -> {
                            onPickDocument(thingTitle)
                            keyText = ""
                            valueText = ""
                            isKeyInputVisible = false
                            isValueInputVisible = false
                        }
                        "image" -> {
                            onPickImage(thingTitle)
                            keyText = ""
                            valueText = ""
                            isKeyInputVisible = false
                            isValueInputVisible = false
                        }
                        "qr" -> {
                            param_api.genQR(thingTitle)
                            keyText = ""
                            valueText = ""
                            isKeyInputVisible = false
                            isValueInputVisible = false
                        }
                        else -> {
                            OutlinedTextField(
                                value = valueText,
                                onValueChange = { valueText = it },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth(),
                                label = { Text("Введите значение") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (valueText.isNotEmpty()) {
                                            createParameter(thingTitle, keyText, valueText)
                                            keyText = ""
                                            valueText = ""
                                            isKeyInputVisible = false
                                            isValueInputVisible = false
                                        }
                                    }
                                ),
                                singleLine = true,
                            )
                        }
                    }
                }

                isKeyInputVisible -> {
                    OutlinedTextField(
                        value = keyText,
                        onValueChange = { keyText = it },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        label = { Text("Введите ключ") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (keyText.isNotEmpty()) {
                                    isKeyInputVisible = false
                                    isValueInputVisible = true
                                }
                            }
                        ),
                        singleLine = true,
                    )
                }

                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add",
                        tint = Color(0xFF62519F),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                isKeyInputVisible = true
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    thingTitle: String,
    onPickDocument: (String) -> Unit,
    onPickImage: (String) -> Unit
) {
    var paramList by remember { mutableStateOf(param_api.getParameters(thingTitle).data.toMutableList()) }

    fun updateParameterList() {
        paramList = param_api.getParameters(thingTitle).data.toMutableList()
    }

    LaunchedEffect(thingTitle) {
        updateParameterList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddParameterButton(
            thingTitle = thingTitle,
            createParameter = { title, key, value ->
                param_api.createParameter(title, key, value)
                updateParameterList()
            },
            onPickDocument = { title ->
                onPickDocument(title)
                updateParameterList()
            },
            onPickImage = { title ->
                onPickImage(title)
                updateParameterList()
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (paramList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Parameters not found")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(paramList.size) { index ->
                    ParameterSquare(
                        parameter = paramList[index],
                        onDelete = { parameter ->
                            param_api.deleteParameter(thingTitle, parameter.key, parameter.value)
                            updateParameterList()
                        },
                        onUpdate = { parameter, newValue ->
                            param_api.updateParameter(
                                ParameterAPI.OldParameter(thingTitle, parameter.key, parameter.value),
                                ParameterAPI.NewParameter(parameter.key, newValue)
                            )
                            updateParameterList()
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThingAppBar(title: String) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                val intent = Intent(context, Finder::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF62519F))
    )
}
