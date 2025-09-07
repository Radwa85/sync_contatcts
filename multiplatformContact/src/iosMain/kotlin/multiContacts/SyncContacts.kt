package multiContacts

import androidx.compose.runtime.Composable
import platform.Contacts.*
import platform.Foundation.*

@Composable
actual fun syncAllContacts(onResult: (List<ContactInfo>) -> Unit): Launcher {
    return Launcher(onLaunch = {
        val contacts = fetchContacts()
        onResult(contacts)
    })
}

fun fetchContacts(): List<ContactInfo> {
    val contactsList = mutableListOf<ContactInfo>()
    val contactStore = CNContactStore()
    
    try {
        val keysToFetch = listOf(
            CNContactGivenNameKey,
            CNContactFamilyNameKey,
            CNContactPhoneNumbersKey,
            CNContactImageDataKey,
            CNContactThumbnailImageDataKey
        )
        
        val request = platform.Contacts.CNContactFetchRequest(keysToFetch)
        contactStore.enumerateContactsWithRequest(request) { contact, _ ->
            val contactInfo = createContactInfo(contact)
            if (contactInfo != null) {
                contactsList.add(contactInfo)
            }
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    return contactsList
}

private fun createContactInfo(contact: CNContact): ContactInfo? {
    val givenName = contact.givenName ?: ""
    val familyName = contact.familyName ?: ""
    val fullName = if (givenName.isNotEmpty() || familyName.isNotEmpty()) {
        "$givenName $familyName".trim()
    } else {
        "Unknown"
    }
    
    val phoneNumbers = contact.phoneNumbers
    val phone = if (phoneNumbers.isNotEmpty()) {
        phoneNumbers[0].value.stringValue ?: ""
    } else {
        ""
    }
    
    return ContactInfo(
        id = contact.identifier,
        name = fullName,
        phone = phone,
        photoUri = null
    )
}

