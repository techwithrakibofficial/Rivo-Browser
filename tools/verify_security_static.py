#!/usr/bin/env python3
"""Fail-closed static security guardrails for the shipping Android application.

This is a regression check for repository configuration, not a vulnerability scanner,
runtime test, penetration test, or MASVS certification.
"""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
ANDROID = "{http://schemas.android.com/apk/res/android}"
errors: list[str] = []

def require(condition: bool, message: str) -> None:
    if not condition:
        errors.append(message)

def read(path: str) -> str:
    candidate = ROOT / path
    if not candidate.is_file():
        errors.append(f"Required file missing: {path}")
        return ""
    return candidate.read_text(encoding="utf-8")

manifest_path = ROOT / "app/src/main/AndroidManifest.xml"
try:
    manifest = ET.parse(manifest_path).getroot()
    app = manifest.find("application")
    require(app is not None, "Application element missing from app manifest")
    if app is not None:
        require(app.get(ANDROID + "allowBackup") == "false", "Application backup must remain disabled")
        require(app.get(ANDROID + "usesCleartextTraffic") == "false", "Application cleartext traffic must remain disabled")
        require(app.get(ANDROID + "networkSecurityConfig") == "@xml/network_security_config",
                "Application must reference the explicit network security config")
        for component in list(app):
            tag = component.tag.rsplit("}", 1)[-1]
            exported = component.get(ANDROID + "exported")
            name = component.get(ANDROID + "name", "<unnamed>")
            if tag in {"provider", "receiver", "service"}:
                require(exported == "false", f"{tag} {name} must not be exported")
            if tag == "activity" and exported == "true":
                actions = {e.get(ANDROID + "name") for e in component.findall("./intent-filter/action")}
                categories = {e.get(ANDROID + "name") for e in component.findall("./intent-filter/category")}
                require(actions == {"android.intent.action.MAIN"} and
                        "android.intent.category.LAUNCHER" in categories and
                        "android.intent.category.BROWSABLE" not in categories,
                        f"Exported activity {name} must be launcher-only")
except (ET.ParseError, OSError) as exc:
    errors.append(f"Unable to parse app manifest: {exc}")

network_path = ROOT / "app/src/main/res/xml/network_security_config.xml"
try:
    network = ET.parse(network_path).getroot()
    base = network.find("base-config")
    require(base is not None and base.get("cleartextTrafficPermitted") == "false",
            "Network security base-config must deny cleartext")
    if base is not None:
        anchors = base.find("trust-anchors")
        certs = [] if anchors is None else anchors.findall("certificates")
        require(len(certs) == 1 and certs[0].get("src") == "system",
                "Network trust anchors must be limited to system certificates")
    require(network.find("debug-overrides") is None, "Release network config must not contain debug trust overrides")
except (ET.ParseError, OSError) as exc:
    errors.append(f"Unable to parse network security config: {exc}")

gecko_path = "engine-gecko/src/main/java/com/rivo/browser/engine/gecko/GeckoBrowserTab.kt"
gecko = read(gecko_path)
for setting in (".remoteDebuggingEnabled(false)", ".consoleOutput(false)",
                ".debugLogging(false)", ".aboutConfigEnabled(false)"):
    require(setting in gecko, f"Gecko runtime must explicitly set {setting}")

# Shipping source must not bridge arbitrary website JavaScript into Android APIs.
for source_root in ("app/src/main", "engine-gecko/src/main"):
    for source in (ROOT / source_root).rglob("*.kt"):
        content = source.read_text(encoding="utf-8")
        require("addJavascriptInterface" not in content,
                f"JavaScript bridge API found in shipping source: {source.relative_to(ROOT)}")
        require("onReceivedSslError" not in content and "X509TrustManager" not in content,
                f"Custom TLS override/trust manager requires security review: {source.relative_to(ROOT)}")

# Android WebView is not the shipping engine; its benchmark/probe adapters must stay hardened.
webview_sources = [
    "engine-benchmark/src/webview/java/com/rivo/browser/enginebenchmark/EngineProvider.kt",
    "engine-webview/src/main/java/com/rivo/browser/engine/webview/WebViewEngineProbe.kt",
]
for path in webview_sources:
    content = read(path)
    for setting in ("WebSettings.MIXED_CONTENT_NEVER_ALLOW", "allowFileAccess = false",
                    "allowContentAccess = false"):
        require(setting in content, f"{path} must explicitly set {setting}")
    require("addJavascriptInterface" not in content, f"WebView adapter must not expose a JavaScript bridge: {path}")

if errors:
    print("SECURITY STATIC AUDIT: FAILED")
    for error in errors:
        print(f" - {error}")
    sys.exit(1)

print("SECURITY STATIC AUDIT: PASSED")
print(" - Backup and cleartext network controls verified")
print(" - Network trust anchors restricted to system CAs; no debug overrides")
print(" - Exported components constrained to the launcher entry point")
print(" - No shipping JavaScript bridge or custom trust-all TLS hook found")
print(" - Gecko diagnostics/debug configuration explicitly disabled")
print(" - WebView benchmark/probe mixed-content and file-access controls verified")
