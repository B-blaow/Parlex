package com.translive.app.data.model

/**
 * Prompt strategy determines how the translation prompt is constructed
 * for different model families. Most models use their built-in chat template
 * via llama_chat_apply_template(), but the prompt text itself varies.
 */
enum class PromptStyle {
    /** HY-MT specific: Chinese prompt for zh pairs, English for others */
    HY_MT,
    /** TranslateGemma: Google's specialized translation prompt */
    TRANSLATE_GEMMA,
    /** Generic: standard "Translate X into Y" prompt, works with most chat models */
    GENERIC_TRANSLATE
}

/**
 * License type for models — determines whether a confirmation dialog
 * is shown before the first download.
 */
enum class ModelLicense(val displayName: String) {
    APACHE_2("Apache 2.0"),
    MIT("MIT"),
    GEMMA_TOU("Gemma Terms of Use"),
    LLAMA_COMMUNITY("Llama Community License"),
    CC_BY_NC("CC-BY-NC 4.0")
}

/**
 * A model family groups multiple GGUF quantizations of the same base model.
 * Example: "Qwen3 1.7B" family contains Q2_K, Q3_K_S, ..., F16 variants.
 */
data class ModelFamily(
    val id: String,
    val name: String,
    val developer: String,
    val description: String,
    val languageCount: Int,
    val parameterSize: String,
    val promptStyle: PromptStyle,
    val license: ModelLicense,
    /** True for models specifically trained for translation (HY-MT, TranslateGemma) */
    val isSpecialized: Boolean,
    val variants: List<ModelVariant>
) {
    /** Whether this family requires a license confirmation dialog before download */
    val requiresLicenseConfirmation: Boolean
        get() = license == ModelLicense.GEMMA_TOU

    companion object {
        /** Find a family by its ID */
        fun findById(familyId: String): ModelFamily? =
            ModelCatalog.ALL_FAMILIES.find { it.id == familyId }

        /** Find a variant across all families by its namespaced ID ("family:quant") */
        fun findVariantById(id: String): ModelVariant? {
            val parts = id.split(":", limit = 2)
            if (parts.size != 2) return null
            val family = findById(parts[0]) ?: return null
            return family.variants.find { it.id == id }
        }

        /** Get the family that a variant belongs to */
        fun familyOf(variant: ModelVariant): ModelFamily? =
            ModelCatalog.ALL_FAMILIES.find { f -> f.variants.any { it.id == variant.id } }
    }
}
