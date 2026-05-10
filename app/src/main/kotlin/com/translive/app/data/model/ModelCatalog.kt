package com.translive.app.data.model

/**
 * Central catalog of all available model families and their GGUF quantizations.
 * Data sourced from HuggingFace API (May 2026).
 */
object ModelCatalog {

    val ALL_FAMILIES: List<ModelFamily> = listOf(
        hyMtFamily(),
        translateGemmaFamily(),
        qwen3_1_7bFamily(),
        qwen3_4bFamily(),
        tinyAyaFamily(),
        gemma4E2bFamily(),
        phi4MiniFamily()
    )

    // ─── 1. HY-MT 1.5 1.8B (Tencent) ─────────────────────────────────

    private fun hyMtFamily(): ModelFamily {
        val b = "https://huggingface.co/mradermacher/HY-MT1.5-1.8B-GGUF/resolve/main"
        val p = "HY-MT1.5-1.8B"
        return ModelFamily(
            id = "hy_mt", name = "HY-MT 1.5 1.8B", developer = "Tencent",
            description = "Специализирован на перевод, 33 языка",
            languageCount = 33, parameterSize = "1.8B",
            promptStyle = PromptStyle.HY_MT, license = ModelLicense.APACHE_2,
            isSpecialized = true,
            variants = listOf(
                v("hy_mt:q2_k","Q2_K","Минимальная","2-bit",777_000_000L,1228,"$b/$p.Q2_K.gguf?download=true","$p.Q2_K.gguf"),
                v("hy_mt:q3_k_s","Q3_K_S","Компактная","3-bit, маленький",872_000_000L,1400,"$b/$p.Q3_K_S.gguf?download=true","$p.Q3_K_S.gguf"),
                v("hy_mt:q3_k_m","Q3_K_M","Баланс 3-bit","3-bit средний",951_000_000L,1500,"$b/$p.Q3_K_M.gguf?download=true","$p.Q3_K_M.gguf"),
                v("hy_mt:q3_k_l","Q3_K_L","Улучшенная 3-bit","3-bit большой",1_020_000_000L,1600,"$b/$p.Q3_K_L.gguf?download=true","$p.Q3_K_L.gguf"),
                v("hy_mt:iq4_xs","IQ4_XS","iMatrix 4-bit","4-bit iMatrix",1_040_000_000L,1600,"$b/$p.IQ4_XS.gguf?download=true","$p.IQ4_XS.gguf"),
                v("hy_mt:q4_k_s","Q4_K_S","Стандартная 4-bit","4-bit",1_080_000_000L,1700,"$b/$p.Q4_K_S.gguf?download=true","$p.Q4_K_S.gguf"),
                v("hy_mt:q4_k_m","Q4_K_M","Рекомендуемая","Лучший баланс",1_130_000_000L,1800,"$b/$p.Q4_K_M.gguf?download=true","$p.Q4_K_M.gguf",true),
                v("hy_mt:q5_k_m","Q5_K_M","Высокое качество","5-bit",1_300_000_000L,2100,"$b/$p.Q5_K_M.gguf?download=true","$p.Q5_K_M.gguf"),
                v("hy_mt:q6_k","Q6_K","Премиум","6-bit, почти без потерь",1_470_000_000L,2300,"$b/$p.Q6_K.gguf?download=true","$p.Q6_K.gguf"),
                v("hy_mt:q8_0","Q8_0","Максимальная точность","8-bit",1_910_000_000L,2800,"$b/$p.Q8_0.gguf?download=true","$p.Q8_0.gguf"),
                v("hy_mt:f16","F16","Полная","FP16, без квантизации",3_590_000_000L,4500,"$b/$p.f16.gguf?download=true","$p.f16.gguf")
            )
        )
    }

    // ─── 2. TranslateGemma 4B (Google) ────────────────────────────────

    private fun translateGemmaFamily(): ModelFamily {
        val b = "https://huggingface.co/mradermacher/translategemma-4b-it-GGUF/resolve/main"
        val p = "translategemma-4b-it"
        return ModelFamily(
            id = "translate_gemma", name = "TranslateGemma 4B", developer = "Google",
            description = "Специализирован на перевод, 55 языков",
            languageCount = 55, parameterSize = "4B",
            promptStyle = PromptStyle.TRANSLATE_GEMMA, license = ModelLicense.GEMMA_TOU,
            isSpecialized = true,
            variants = listOf(
                v("translate_gemma:q2_k","Q2_K","Минимальная","2-bit",1_729_180_160L,2200,"$b/$p.Q2_K.gguf?download=true","$p.Q2_K.gguf"),
                v("translate_gemma:q3_k_s","Q3_K_S","Компактная","3-bit",1_937_379_840L,2500,"$b/$p.Q3_K_S.gguf?download=true","$p.Q3_K_S.gguf"),
                v("translate_gemma:q3_k_m","Q3_K_M","Баланс 3-bit","3-bit средний",2_098_475_520L,2700,"$b/$p.Q3_K_M.gguf?download=true","$p.Q3_K_M.gguf"),
                v("translate_gemma:q3_k_l","Q3_K_L","Улучшенная 3-bit","3-bit большой",2_236_101_120L,2900,"$b/$p.Q3_K_L.gguf?download=true","$p.Q3_K_L.gguf"),
                v("translate_gemma:iq4_xs","IQ4_XS","iMatrix 4-bit","4-bit iMatrix",2_279_641_600L,2900,"$b/$p.IQ4_XS.gguf?download=true","$p.IQ4_XS.gguf"),
                v("translate_gemma:q4_k_s","Q4_K_S","Стандартная 4-bit","4-bit",2_377_945_600L,3000,"$b/$p.Q4_K_S.gguf?download=true","$p.Q4_K_S.gguf"),
                v("translate_gemma:q4_k_m","Q4_K_M","Рекомендуемая","Лучший баланс",2_489_909_760L,3200,"$b/$p.Q4_K_M.gguf?download=true","$p.Q4_K_M.gguf",true),
                v("translate_gemma:q5_k_s","Q5_K_S","Качество 5-bit","5-bit",2_764_608_000L,3500,"$b/$p.Q5_K_S.gguf?download=true","$p.Q5_K_S.gguf"),
                v("translate_gemma:q5_k_m","Q5_K_M","Высокое качество","5-bit",2_829_713_920L,3600,"$b/$p.Q5_K_M.gguf?download=true","$p.Q5_K_M.gguf"),
                v("translate_gemma:q6_k","Q6_K","Премиум","6-bit",3_190_755_840L,4000,"$b/$p.Q6_K.gguf?download=true","$p.Q6_K.gguf"),
                v("translate_gemma:q8_0","Q8_0","Максимальная точность","8-bit",4_130_417_920L,5000,"$b/$p.Q8_0.gguf?download=true","$p.Q8_0.gguf"),
                v("translate_gemma:f16","F16","Полная","FP16",7_767_819_520L,9000,"$b/$p.f16.gguf?download=true","$p.f16.gguf")
            )
        )
    }

    // ─── 3. Qwen3 1.7B (Alibaba) ─────────────────────────────────────

    private fun qwen3_1_7bFamily(): ModelFamily {
        val b = "https://huggingface.co/unsloth/Qwen3-1.7B-GGUF/resolve/main"
        val p = "Qwen3-1.7B"
        return ModelFamily(
            id = "qwen3_1_7b", name = "Qwen3 1.7B", developer = "Alibaba",
            description = "119 языков, универсальная",
            languageCount = 119, parameterSize = "1.7B",
            promptStyle = PromptStyle.GENERIC_TRANSLATE, license = ModelLicense.APACHE_2,
            isSpecialized = false,
            variants = listOf(
                v("qwen3_1_7b:q2_k","Q2_K","Минимальная","2-bit",777_796_160L,1200,"$b/$p-Q2_K.gguf","$p-Q2_K.gguf"),
                v("qwen3_1_7b:q3_k_s","Q3_K_S","Компактная","3-bit",867_252_800L,1350,"$b/$p-Q3_K_S.gguf","$p-Q3_K_S.gguf"),
                v("qwen3_1_7b:q3_k_m","Q3_K_M","Баланс 3-bit","3-bit средний",939_539_008L,1450,"$b/$p-Q3_K_M.gguf","$p-Q3_K_M.gguf"),
                v("qwen3_1_7b:iq4_xs","IQ4_XS","iMatrix 4-bit","4-bit iMatrix",1_010_383_424L,1550,"$b/$p-IQ4_XS.gguf","$p-IQ4_XS.gguf"),
                v("qwen3_1_7b:q4_k_s","Q4_K_S","Стандартная 4-bit","4-bit",1_060_190_784L,1600,"$b/$p-Q4_K_S.gguf","$p-Q4_K_S.gguf"),
                v("qwen3_1_7b:q4_k_m","Q4_K_M","Рекомендуемая","Лучший баланс",1_107_409_472L,1700,"$b/$p-Q4_K_M.gguf","$p-Q4_K_M.gguf",true),
                v("qwen3_1_7b:q5_k_s","Q5_K_S","Качество 5-bit","5-bit",1_230_584_384L,1900,"$b/$p-Q5_K_S.gguf","$p-Q5_K_S.gguf"),
                v("qwen3_1_7b:q5_k_m","Q5_K_M","Высокое качество","5-bit",1_257_880_128L,1950,"$b/$p-Q5_K_M.gguf","$p-Q5_K_M.gguf"),
                v("qwen3_1_7b:q6_k","Q6_K","Премиум","6-bit",1_417_755_200L,2200,"$b/$p-Q6_K.gguf","$p-Q6_K.gguf"),
                v("qwen3_1_7b:q8_0","Q8_0","Максимальная точность","8-bit",1_834_426_944L,2700,"$b/$p-Q8_0.gguf","$p-Q8_0.gguf"),
                v("qwen3_1_7b:bf16","BF16","Полная","BF16",3_447_349_568L,4500,"$b/$p-BF16.gguf","$p-BF16.gguf")
            )
        )
    }

    // ─── 4. Qwen3 4B (Alibaba) ────────────────────────────────────────

    private fun qwen3_4bFamily(): ModelFamily {
        val b = "https://huggingface.co/unsloth/Qwen3-4B-GGUF/resolve/main"
        val p = "Qwen3-4B"
        return ModelFamily(
            id = "qwen3_4b", name = "Qwen3 4B", developer = "Alibaba",
            description = "119 языков, универсальная, высокое качество",
            languageCount = 119, parameterSize = "4B",
            promptStyle = PromptStyle.GENERIC_TRANSLATE, license = ModelLicense.APACHE_2,
            isSpecialized = false,
            variants = listOf(
                v("qwen3_4b:q2_k","Q2_K","Минимальная","2-bit",1_800_000_000L,2300,"$b/$p-Q2_K.gguf","$p-Q2_K.gguf"),
                v("qwen3_4b:q3_k_s","Q3_K_S","Компактная","3-bit",2_000_000_000L,2600,"$b/$p-Q3_K_S.gguf","$p-Q3_K_S.gguf"),
                v("qwen3_4b:q3_k_m","Q3_K_M","Баланс 3-bit","3-bit средний",2_200_000_000L,2800,"$b/$p-Q3_K_M.gguf","$p-Q3_K_M.gguf"),
                v("qwen3_4b:q4_k_s","Q4_K_S","Стандартная 4-bit","4-bit",2_500_000_000L,3200,"$b/$p-Q4_K_S.gguf","$p-Q4_K_S.gguf"),
                v("qwen3_4b:q4_k_m","Q4_K_M","Рекомендуемая","Лучший баланс",2_600_000_000L,3300,"$b/$p-Q4_K_M.gguf","$p-Q4_K_M.gguf",true),
                v("qwen3_4b:q5_k_m","Q5_K_M","Высокое качество","5-bit",3_000_000_000L,3800,"$b/$p-Q5_K_M.gguf","$p-Q5_K_M.gguf"),
                v("qwen3_4b:q6_k","Q6_K","Премиум","6-bit",3_400_000_000L,4300,"$b/$p-Q6_K.gguf","$p-Q6_K.gguf"),
                v("qwen3_4b:q8_0","Q8_0","Максимальная точность","8-bit",4_400_000_000L,5500,"$b/$p-Q8_0.gguf","$p-Q8_0.gguf"),
                v("qwen3_4b:bf16","BF16","Полная","BF16",8_200_000_000L,10000,"$b/$p-BF16.gguf","$p-BF16.gguf")
            )
        )
    }

    // ─── 5. Tiny Aya Water 3.35B (Cohere) ─────────────────────────────

    private fun tinyAyaFamily(): ModelFamily {
        val b = "https://huggingface.co/CohereLabs/tiny-aya-water-GGUF/resolve/main"
        return ModelFamily(
            id = "tiny_aya", name = "Tiny Aya Water 3.35B", developer = "Cohere",
            description = "70+ языков, Европа + Азия",
            languageCount = 70, parameterSize = "3.35B",
            promptStyle = PromptStyle.GENERIC_TRANSLATE, license = ModelLicense.CC_BY_NC,
            isSpecialized = false,
            variants = listOf(
                v("tiny_aya:q4_0","Q4_0","Стандартная 4-bit","4-bit базовая",2_034_490_976L,2600,"$b/tiny-aya-water-q4_0.gguf","tiny-aya-water-q4_0.gguf"),
                v("tiny_aya:q4_k_m","Q4_K_M","Рекомендуемая","Лучший баланс",2_143_977_056L,2800,"$b/tiny-aya-water-q4_k_m.gguf","tiny-aya-water-q4_k_m.gguf",true),
                v("tiny_aya:q8_0","Q8_0","Максимальная точность","8-bit",3_570_654_816L,4500,"$b/tiny-aya-water-q8_0.gguf","tiny-aya-water-q8_0.gguf"),
                v("tiny_aya:f16","F16","Полная","FP16",6_710_484_576L,8000,"$b/tiny-aya-water-f16.gguf","tiny-aya-water-f16.gguf"),
                v("tiny_aya:bf16","BF16","Полная BF16","BF16",6_710_484_576L,8000,"$b/tiny-aya-water-bf16.gguf","tiny-aya-water-bf16.gguf")
            )
        )
    }

    // ─── 6. Gemma 4 E2B (Google) ──────────────────────────────────────

    private fun gemma4E2bFamily(): ModelFamily {
        val b = "https://huggingface.co/unsloth/gemma-4-E2B-it-GGUF/resolve/main"
        val p = "gemma-4-E2B-it"
        return ModelFamily(
            id = "gemma4_e2b", name = "Gemma 4 E2B", developer = "Google",
            description = "Мультимодальная, ~30 языков",
            languageCount = 30, parameterSize = "E2B",
            promptStyle = PromptStyle.GENERIC_TRANSLATE, license = ModelLicense.APACHE_2,
            isSpecialized = false,
            variants = listOf(
                v("gemma4_e2b:q3_k_s","Q3_K_S","Компактная","3-bit",2_445_650_048L,3100,"$b/$p-Q3_K_S.gguf","$p-Q3_K_S.gguf"),
                v("gemma4_e2b:q3_k_m","Q3_K_M","Баланс 3-bit","3-bit средний",2_536_784_000L,3200,"$b/$p-Q3_K_M.gguf","$p-Q3_K_M.gguf"),
                v("gemma4_e2b:iq4_xs","IQ4_XS","iMatrix 4-bit","4-bit iMatrix",2_983_942_272L,3700,"$b/$p-IQ4_XS.gguf","$p-IQ4_XS.gguf"),
                v("gemma4_e2b:q4_k_s","Q4_K_S","Стандартная 4-bit","4-bit",3_043_932_288L,3800,"$b/$p-Q4_K_S.gguf","$p-Q4_K_S.gguf"),
                v("gemma4_e2b:q4_k_m","Q4_K_M","Рекомендуемая","Лучший баланс",3_106_736_256L,3900,"$b/$p-Q4_K_M.gguf","$p-Q4_K_M.gguf",true),
                v("gemma4_e2b:q5_k_s","Q5_K_S","Качество 5-bit","5-bit",3_321_149_568L,4200,"$b/$p-Q5_K_S.gguf","$p-Q5_K_S.gguf"),
                v("gemma4_e2b:q5_k_m","Q5_K_M","Высокое качество","5-bit",3_356_035_200L,4200,"$b/$p-Q5_K_M.gguf","$p-Q5_K_M.gguf"),
                v("gemma4_e2b:q6_k","Q6_K","Премиум","6-bit",4_501_719_168L,5500,"$b/$p-Q6_K.gguf","$p-Q6_K.gguf"),
                v("gemma4_e2b:q8_0","Q8_0","Максимальная точность","8-bit",5_048_350_848L,6200,"$b/$p-Q8_0.gguf","$p-Q8_0.gguf"),
                v("gemma4_e2b:bf16","BF16","Полная","BF16",9_311_303_552L,11000,"$b/$p-BF16.gguf","$p-BF16.gguf")
            )
        )
    }

    // ─── 7. Phi-4-mini 3.8B (Microsoft) ───────────────────────────────

    private fun phi4MiniFamily(): ModelFamily {
        val b = "https://huggingface.co/unsloth/Phi-4-mini-instruct-GGUF/resolve/main"
        val p = "Phi-4-mini-instruct"
        return ModelFamily(
            id = "phi4_mini", name = "Phi-4-mini 3.8B", developer = "Microsoft",
            description = "Мультилингвальная, 200K vocab",
            languageCount = 30, parameterSize = "3.8B",
            promptStyle = PromptStyle.GENERIC_TRANSLATE, license = ModelLicense.MIT,
            isSpecialized = false,
            variants = listOf(
                v("phi4_mini:q2_k","Q2_K","Минимальная","2-bit",1_682_635_744L,2200,"$b/$p-Q2_K.gguf","$p-Q2_K.gguf"),
                v("phi4_mini:q3_k_m","Q3_K_M","Баланс 3-bit","3-bit",2_117_532_640L,2700,"$b/$p-Q3_K_M.gguf","$p-Q3_K_M.gguf"),
                v("phi4_mini:q4_k_m","Q4_K_M","Рекомендуемая","Лучший баланс",2_491_874_272L,3200,"$b/$p-Q4_K_M.gguf","$p-Q4_K_M.gguf",true),
                v("phi4_mini:q5_k_m","Q5_K_M","Высокое качество","5-bit",2_848_127_968L,3600,"$b/$p-Q5_K_M.gguf","$p-Q5_K_M.gguf"),
                v("phi4_mini:q6_k","Q6_K","Премиум","6-bit",3_155_622_880L,4000,"$b/$p-Q6_K.gguf","$p-Q6_K.gguf"),
                v("phi4_mini:q8_0","Q8_0","Максимальная точность","8-bit",4_084_611_040L,5100,"$b/$p.Q8_0.gguf","$p.Q8_0.gguf"),
                v("phi4_mini:bf16","BF16","Полная","BF16",7_680_694_240L,9500,"$b/$p.BF16.gguf","$p.BF16.gguf")
            )
        )
    }

    // ─── Helper ───────────────────────────────────────────────────────

    private fun v(
        id: String, quant: String, display: String, desc: String,
        size: Long, ram: Int, url: String, file: String, rec: Boolean = false
    ) = ModelVariant(id, quant, display, desc, size, ram, url, file, rec)
}
