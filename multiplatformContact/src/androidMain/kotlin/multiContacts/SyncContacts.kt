package multiContacts

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun syncAllContacts(onResult: (List<ContactInfo>) -> Unit): Launcher {
    val context = LocalContext.current
    val launcherPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                val contacts = fetchContacts(context)
                onResult(contacts)
            }
        }
    )
    return remember {
        Launcher(onLaunch = {
            launcherPermission.launch(Manifest.permission.READ_CONTACTS)
        })
    }
}

private fun fetchContacts(context: Context): List<ContactInfo> {
    val contactsList = mutableListOf<ContactInfo>()
    val contentResolver = context.contentResolver

    contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        ),
        null, null, null
    )?.use { contactData ->
        while (contactData.moveToNext()) {
            val contactId = contactData.getString(contactData.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val displayName = contactData.getString(contactData.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)) ?: "Unknown"
            val photoUri = contactData.getString(contactData.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
            val hasPhoneNumber = contactData.getInt(contactData.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0

            val phoneNumber = if (hasPhoneNumber) {
                fetchPhoneNumberForContact(contentResolver, contactId)
            } else {
                ""
            }

            contactsList.add(ContactInfo(contactId, displayName, phoneNumber, photoUri))
        }
    }
    return contactsList
}

private fun fetchPhoneNumberForContact(contentResolver: ContentResolver, contactId: String): String {
    var phoneNumber = ""
    contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
        arrayOf(contactId),
        null
    )?.use { phone ->
        if (phone.moveToFirst()) {
            phoneNumber = phone.getString(phone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: ""
        }
    }
    return phoneNumber
}