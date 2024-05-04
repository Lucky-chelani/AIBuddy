package com.example.aibuddy

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.aibuddy.ui.theme.AIBuddyTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    private val uriState  =  MutableStateFlow("")
    private val imagePicker   =  registerForActivityResult<PickVisualMediaRequest , Uri>(
        ActivityResultContracts.PickVisualMedia()
    ){uri ->
        uri?.let {
            uriState.update {
                uri.toString()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIBuddyTheme {
                SetBarColor(color = MaterialTheme.colorScheme.inverseOnSurface)
                Scaffold(
                    topBar = {
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorResource(id = R.color.top))
                                .height(55.dp)
                                .padding(horizontal = 16.dp)
                        ){
                            Text(
                                modifier = Modifier.align(Alignment.CenterStart),

                                text = stringResource(id = R.string.app_name),
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White

                            )

                        }
                    }



                ) {
                    ChatScreen(paddingValues = it)

                }
            }
        }
    }
    @Composable
    fun ChatScreen(paddingValues: PaddingValues){
        val chatViewModel =  viewModel<ChatViewModel>()
        val chatState = chatViewModel.chatstate.collectAsState().value
        val bitmap =  GetBitmap()
        Image(painter = painterResource(id = R.drawable.wh1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                ,
                reverseLayout = true
            ) {
                itemsIndexed(chatState.chatList){index, chat ->
                    if(chat.isFromUser){
                        UserChatItem(prompt = chat.prompt, bitmap = chat.bitmap)
                    } else {
                        ModelChatItem(response = chat.prompt)

                    }


                }

            }





                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        bitmap?.let {
                            Image(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(bottom = 2.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentDescription = "image",
                                contentScale = ContentScale.Crop,
                                bitmap = it.asImageBitmap()
                            )
                        }
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    imagePicker.launch(
                                        PickVisualMediaRequest
                                            .Builder()
                                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            .build()
                                    )
                                },
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Add Photo",
                            tint = colorResource(id = R.color.icon)

                        )

                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        modifier = Modifier
                            .weight(1f),
                        value = chatState.prompt,
                        onValueChange = {
                            chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                        },
                        placeholder = {
                            Text(text = "Type a prompt")
                        }

                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                chatViewModel.onEvent(
                                    ChatUiEvent.SendPrompt(
                                        chatState.prompt,
                                        bitmap
                                    )
                                )
                                uriState.update { "" }
                            },
                        painter = painterResource(id = R.drawable.send),
                        contentDescription = "Send Photo",
                        tint =  colorResource(id = R.color.icon)

                    )


                }


        }

    }


    @Composable
    fun UserChatItem(prompt : String , bitmap: Bitmap? ){
        Column(
            modifier = Modifier.padding(start = 100.dp, bottom = 22.dp)
        ) {
            bitmap?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(bottom = 2.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentDescription = " picked image",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                    )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(id = R.color.user))
                    .padding(16.dp),
                text =  prompt,
                fontSize = 17.sp,
                color = Color.White
                )
        }
    }
    @Composable
    fun ModelChatItem(response : String ){
        Column(
            modifier = Modifier.padding(end = 100.dp, bottom = 16.dp)
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(id = R.color.model))
                    .padding(16.dp),
                text =  response,
                fontSize = 17.sp,
                color = Color.White
            )
        }
    }
    @Composable
    private fun GetBitmap(): Bitmap?{
        val uri = uriState.collectAsState().value

        val imageState:  AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .size(Size.ORIGINAL)
                .build()
        ).state
        if(imageState is AsyncImagePainter.State.Success){
            return  imageState.result.drawable.toBitmap()
        }

        return null

    }
    @Composable
    private fun SetBarColor(color: Color) {
        val systemUiController = rememberSystemUiController()
        LaunchedEffect(key1 = color) {
            systemUiController.setSystemBarsColor(color)
        }
    }
}

