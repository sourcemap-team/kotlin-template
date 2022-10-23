package ru.sourcemap.template.service.notification

interface UserNotificationService {

    fun sendMessage(messageText: String, recipientId: String)

}