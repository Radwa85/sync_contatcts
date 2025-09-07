package multiContacts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun MultiContacts() {
    var contacts by remember { mutableStateOf<List<ContactInfo>>(emptyList()) }
    val syncContactsLauncher = syncAllContacts(onResult = { contacts = it })
    Column(modifier = Modifier.fillMaxSize()) {
        Button(modifier = Modifier.padding(top = 16.dp),
            onClick = { syncContactsLauncher.launch() }) {
            Text("Sync Contacts")
        }
        LazyColumn {
            items(contacts.size) { idx ->
                val contact = contacts[idx]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = contact.name.take(1).uppercase(),
                            style = androidx.compose.material.MaterialTheme.typography.h6
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = contact.name,
                            style = androidx.compose.material.MaterialTheme.typography.body1
                        )
                        if (contact.phone.isNotEmpty()) {
                            Text(
                                text = contact.phone,
                                style = androidx.compose.material.MaterialTheme.typography.body2
                            )
                        }
                    }
                }
            }
        }
    }
}