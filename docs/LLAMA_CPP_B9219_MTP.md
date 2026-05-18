# llama.cpp B9219 Native And MTP Check

## Engine Revision

- Local engine path: `app/src/main/cpp/llama.cpp/`.
- Upstream tag: `b9219`.
- Upstream commit: `45b455e66fc09abed65b7d52d42a4a29ba0d45d6`.
- The engine folder is intentionally ignored by the app repository, so
  `app/src/main/cpp/llama.cpp.version` records the expected checkout.

## Native Android Status

- The existing JNI bridge compiles against B9219 without source changes.
- The active native path remains `TranslationEngine` -> `translive_jni.cpp` ->
  `llama.cpp` -> GGUF.
- Current JNI still performs normal target-model decoding:
  prompt prefill, sampler setup, one sampled token, one `llama_decode`, repeat.
- The app does not currently link the llama.cpp `llama-common` target and does
  not use the upstream speculative helpers from `common/speculative.*`.

## B9219 MTP Status

B9219 includes upstream speculative decoding support for MTP:

- `common/common.h` defines `COMMON_SPECULATIVE_TYPE_DRAFT_MTP`.
- `common/speculative.cpp` maps `draft-mtp` and implements the MTP draft path.
- `common/speculative.cpp` uses `llama-ext.h` staging APIs for pre-norm
  embeddings required by MTP.
- `tools/cli/README.md` documents `--spec-type draft-mtp`,
  `--spec-draft-model`, `--spec-draft-n-max`, and related draft controls.

This means MTP exists in the updated llama.cpp checkout, but it is not yet a
complete app feature. A real app integration still needs a target model, an MTP
draft model/head with a compatible vocabulary, native speculative decode wiring,
settings/catalog controls, and phone benchmarks.

## Next Native MTP Work

1. Add an explicit MTP model variant type in the model catalog instead of
   treating it as a normal GGUF.
2. Add model pairing rules: target GGUF plus compatible MTP draft GGUF/head.
3. Link `llama-common` only if the Android build remains stable and does not
   pull in unnecessary CLI/server code.
4. Port the B9219 speculative decode loop into `translive_jni.cpp` behind a
   beta runtime flag.
5. Benchmark normal decode vs MTP on the Snapdragon 8 Elite phone using the
   same prompts, languages, quantization, and max-token limits.
