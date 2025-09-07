package multiContacts
import androidx.compose.runtime.Composable

@Composable
expect fun syncAllContacts(onResult: (List<ContactInfo>) -> Unit): Launcher