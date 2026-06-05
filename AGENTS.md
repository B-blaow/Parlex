# Codex Rules For This Repository

These rules are part of the project state and should survive a clean clone.

## Working Contract

- Treat requests as product-engineering work. Make concrete repository changes when the user asks to build, fix, improve, release, or implement.
- Keep changes surgical. Do not rewrite unrelated code or reformat files without need.
- Prefer existing project patterns over new abstractions.
- Use `rg`/`rg --files` for search.
- Use `apply_patch` for manual source/document edits.
- Do not revert user changes unless explicitly asked.
- Always validate with the narrowest useful checks, then broader checks when release risk is high.

## Android Project Rules

- Keep secrets and generated artifacts out of git: `keystore.properties`, APK/AAB files, Gradle build folders, local models, diagnostics, and native engine checkouts are ignored.
- The local `llama.cpp` checkout is restored separately under `app/src/main/cpp/llama.cpp/`; keep `app/src/main/cpp/llama.cpp.version` as the tracked source of truth.
- Use official upstream sources for dependency, model, and native-engine audits.
- For release builds, verify the signed APK with `apksigner` and inspect `versionCode` / `versionName` before publishing.
- Release notes should describe user-visible functionality, not internal dependency details.

## Git And Release Rules

- Keep feature work in focused commits.
- Before a GitHub release: bump `versionCode`, bump `versionName`, update visible version docs/badges, build signed release APK, verify signature, push `main`, then create a prerelease/tag with the APK asset.
- Do not commit restore-only files that are reproducible locally.

## Communication

- Final responses should stay short and factual:
  - `Changed:`
  - `Files:`
  - `Validation:`
  - `Notes:`

## Skills And Plugins

- Codex skills/plugins are installed globally on the workstation, not vendored into this repo.
- Use available GitHub tooling or `gh` for issue/release work.
- Use official documentation/source links when checking unstable current versions.
