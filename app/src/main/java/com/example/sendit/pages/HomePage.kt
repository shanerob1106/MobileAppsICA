package com.example.sendit.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.sendit.data.PostData
import com.example.sendit.helpers.ExpandableText

@Preview
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    // Sample list of posts (Hard-coded bad practise lol)
    val posts = listOf(
        PostData(
            postId = "1",
            userName = "John Doe",
            userImage = "https://picsum.photos/150",
            postImage = "https://picsum.photos/300",
            postCaption = "This is a really long piece of text to hopefully trigger the text wrap to " +
                    "prevent extremely long posts from occupying the entire screen. Ideally, there " +
                    "should be a word limit for this at around 100 or so characters but I still don't " +
                    "know how to code in the modifier parameter just quite yet so hopefully this sample " +
                    "text will do the job. As of writing this text the wrapping feature seems to not " +
                    "be working implying that a word limit is required to prevent such a long and boring " +
                    "caption to a post, obviously there will be a word limit on the 'Add Post' when the " +
                    "user creates it or people would probably post the entire Bee Movie script or " +
                    "something odd like that. ",
            timeStamp = "2h ago"
        ),
        PostData(
            postId = "2",
            userName = "Jane Smith",
            userImage = "https://picsum.photos/150",
            postImage = "https://picsum.photos/300",
            postCaption = "Loving this new book I'm reading! 📚",
            timeStamp = "5h ago"
        ),
        PostData(
            postId = "3",
            userName = "Doe John",
            userImage = "https://picsum.photos/150",
            postImage = "https://picsum.photos/300",
            postCaption = "Loving this new book I'm reading! 📚",
            timeStamp = "5h ago"
        ),
        PostData(
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
fun PostCard(post: PostData) {
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
                    Text(
                        text = post.timeStamp,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
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
            Row {
                ExpandableText(text = post.postCaption)
            }

            // Spacer
            Spacer(modifier = Modifier.height(8.dp))

            // Row
            Row {

                // Likes
                IconButton(onClick = {/*Todo*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Button",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Like Counter
                Text(
                    text = "100",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )

                // Comments
                IconButton(onClick = {/*Todo*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Like Button",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Comments Counter
                Text(
                    text = "100",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )

                //
                IconButton(onClick = {/*Todo*/ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Like Button",
                        modifier = Modifier.size(24.dp)
                    )
                }

            }

            Text(text = "2 Days Ago")
        }
    }
}
