package ru.sourcemap.connect.service

import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class FeatureTogglesService(
    private val environment: Environment
) {
    fun isFeatureEnabled(feature: FeatureToggle): Boolean {
        val environmentPropertyValue: String? = System.getenv(feature.name)
        return environmentPropertyValue == "true"
    }

    fun isNonProdFeatureEnabled(feature: FeatureToggle): Boolean {
        val isNonProd = !environment.activeProfiles.contains("prod")
        val isFeatureEnabled = isFeatureEnabled(feature)
        return isFeatureEnabled && isNonProd
    }
}

enum class FeatureToggle {
    FEATURE_TELEGRAM_HASH_CHECK_DISABLED,
    FEATURE_INFINITE_USE_INVITATION_TOKENS,
    FEATURE_TELEGRAM_INSTEAD_OF_SMS,
    FEATURE_TELEGRAM_CHATS_DISABLED
}