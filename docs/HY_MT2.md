# Tencent Hy-MT2 Model Catalog

## Added Offline GGUF Families

- `Hy-MT2 1.8B`: mobile/offline family with 1.25Bit, 2Bit, Q4_K_M, Q6_K, and Q8_0 GGUF files.
- `Hy-MT2 7B`: high-quality GGUF family with Q4_K_M, Q6_K, and Q8_0 files.
- `Hy-MT2 30B-A3B` is intentionally not added to the Android catalog because the available public release is not a practical phone GGUF target.

## Source Repositories

- Official collection: https://huggingface.co/collections/tencent/hy-mt2
- 1.8B base: https://huggingface.co/tencent/Hy-MT2-1.8B
- 1.8B 1.25Bit GGUF: https://huggingface.co/tencent/Hy-MT2-1.8B-1.25Bit-GGUF
- 1.8B 2Bit GGUF: https://huggingface.co/tencent/Hy-MT2-1.8B-2Bit-GGUF
- 1.8B Q4/Q6/Q8 GGUF: https://huggingface.co/tencent/Hy-MT2-1.8B-GGUF
- 7B GGUF: https://huggingface.co/tencent/Hy-MT2-7B-GGUF

## Integration Notes

- Hy-MT2 keeps the same recommended sampler family as HY-MT 1.5 for 1.8B/7B: temperature 0.7, top_p 0.6, top_k 20, repetition penalty 1.05.
- The app uses a separate `HY_MT2` prompt style so older HY-MT behavior remains stable.
- Tencent publishes the checked Hy-MT2 repositories with `apache-2.0` metadata. The app now treats Hy-MT2 as Apache 2.0 downloads, while older HY-MT 1.5 remains under Tencent HY Community licensing.
