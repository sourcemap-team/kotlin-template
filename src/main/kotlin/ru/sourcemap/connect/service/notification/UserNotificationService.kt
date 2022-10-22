package ru.sourcemap.connect.service.notification

interface UserNotificationService {

    fun sendMessage(messageText: String, recipientId: String)

}