package com.example.sendit.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.sendit.data.DataType

@Preview
@Composable
fun HomePage(modifier: Modifier = Modifier) {

    val posts = listOf(
        DataType(
            postId = "1",
            userName = "John Doe",
            userImage = "https://picsum.photos/150",
            postImage = "https://picsum.photos/300",
            postCaption = "Had a great day at the beach! 🌊☀️",
            timeStamp = "2h ago"
        ),
        DataType(
            postId = "2",
            userName = "Jane Smith",
            userImage = "https://picsum.photos/150",
            postImage = "https://picsum.photos/300",
            postCaption = "Loving this new book I'm reading! 📚",
            timeStamp = "5h ago"
        ),
        DataType(
            postId = "3",
            userName = "Doe John",
            userImage = "https://picsum.photos/150",
            postImage = "https://picsum.photos/300",
            postCaption = "Loving this new book I'm reading! 📚",
            timeStamp = "5h ago"
        ),
        DataType(
            postId = "4",
            userName = "Smith Jane",
            userImage = "https://picsum.photos/150",
            postImage = "https://picsum.photos/300",
            postCaption = "Loving this new book I'm reading! 📚",
            timeStamp = "5h ago"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(posts) { post ->
            PostCard(post = post)
        }
    }
}

@Composable
fun PostCard(post: DataType) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // User Info Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = post.userImage,
                    contentDescription = "User Image",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = post.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = post.timeStamp, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post Image
            AsyncImage(
                model = post.postImage,
                contentDescription = "Post Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Caption
            Text(text = post.postCaption, fontSize = 14.sp)

            // Spacer
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                IconButton(onClick = {/*Todo*/}) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Button",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(text = "100", modifier = Modifier.align(alignment = Alignment.CenterVertically))

                IconButton(onClick = {/*Todo*/}) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Like Button",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(text = "100", modifier = Modifier.align(alignment = Alignment.CenterVertically))

                IconButton(onClick = {/*Todo*/}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Like Button",
                        modifier = Modifier.size(24.dp)
                    )
                }

            }


        }
    }
}