#!/usr/bin/env python3
"""Inspect packaged ELF LOAD alignment and APK ZIP alignment (not a runtime test)."""
from __future__ import annotations
import argparse
import os
import struct
import subprocess
import sys
import zipfile
from pathlib import Path
from shutil import which

PAGE = 16 * 1024

def load_alignments(data: bytes) -> list[int]:
    if len(data) < 64 or data[:4] != b"\x7fELF":
        raise ValueError("invalid ELF")
    endian = "<" if data[5] == 1 else ">" if data[5] == 2 else None
    if endian is None:
        raise ValueError("unsupported ELF byte order")
    if data[4] == 2:
        phoff = struct.unpack_from(endian + "Q", data, 32)[0]
        entsize = struct.unpack_from(endian + "H", data, 54)[0]
        count = struct.unpack_from(endian + "H", data, 56)[0]
        align_off, fmt, minimum = 48, endian + "Q", 56
    elif data[4] == 1:
        phoff = struct.unpack_from(endian + "I", data, 28)[0]
        entsize = struct.unpack_from(endian + "H", data, 42)[0]
        count = struct.unpack_from(endian + "H", data, 44)[0]
        align_off, fmt, minimum = 28, endian + "I", 32
    else:
        raise ValueError("unsupported ELF class")
    if entsize < minimum or not count:
        raise ValueError("missing/malformed program headers")
    values = []
    for index in range(count):
        offset = phoff + index * entsize
        if offset + entsize > len(data):
            raise ValueError("program header outside file")
        if struct.unpack_from(endian + "I", data, offset)[0] == 1:
            values.append(struct.unpack_from(fmt, data, offset + align_off)[0])
    if not values:
        raise ValueError("no PT_LOAD entries")
    return values

def find_zipalign() -> str | None:
    found = which("zipalign")
    if found:
        return found
    for variable in ("ANDROID_HOME", "ANDROID_SDK_ROOT"):
        root = os.environ.get(variable)
        if root:
            versions = sorted((Path(root) / "build-tools").glob("*/zipalign"))
            if versions:
                return str(versions[-1])
    return None

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--apk", action="append", default=[])
    parser.add_argument("--aab", action="append", default=[])
    args = parser.parse_args()
    inputs = [(Path(p), True) for p in args.apk] + [(Path(p), False) for p in args.aab]
    if not inputs:
        parser.error("provide at least one --apk or --aab")
    zipalign = find_zipalign()
    ok, count = True, 0
    for path, is_apk in inputs:
        if not path.is_file():
            print(f"FAIL missing {path}"); ok = False; continue
        try:
            with zipfile.ZipFile(path) as archive:
                libs = [x for x in archive.infolist() if x.filename.lower().endswith(".so")]
                if not libs:
                    print(f"INFO no native libraries: {path}")
                for lib in libs:
                    count += 1
                    try:
                        values = load_alignments(archive.read(lib))
                        if any(value < PAGE for value in values):
                            print(f"FAIL {path}:{lib.filename}: LOAD alignments {values}")
                            ok = False
                        else:
                            print(f"PASS {path}:{lib.filename}: minimum {min(values)} bytes")
                    except Exception as error:
                        print(f"FAIL {path}:{lib.filename}: {error}"); ok = False
        except zipfile.BadZipFile:
            print(f"FAIL invalid archive {path}"); ok = False
        if is_apk:
            if not zipalign:
                print("FAIL zipalign missing"); ok = False
            else:
                check = subprocess.run([zipalign, "-c", "-P", "16", "4", str(path)],
                    stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, check=False)
                print(check.stdout)
                if check.returncode:
                    print(f"FAIL APK ZIP alignment: {path}"); ok = False
                else:
                    print(f"PASS APK ZIP alignment: {path}")
    if count == 0:
        print("FAIL no packaged native libraries were inspected"); ok = False
    print(("PASS" if ok else "FAIL") + f": inspected {count} native libraries")
    return 0 if ok else 1

if __name__ == "__main__":
    sys.exit(main())
