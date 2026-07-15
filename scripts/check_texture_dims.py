#!/usr/bin/env python3
"""Reject item/block textures smaller than 16x16 in either dimension (larger is
allowed; gui and entity textures are not checked). Reads PNG headers directly,
no PIL dependency.

Usage:
  check_texture_dims.py            scan all tracked textures
  check_texture_dims.py --staged   scan only staged (added/modified) textures
"""
import os
import struct
import subprocess
import sys


def png_size(path):
    try:
        with open(path, "rb") as fh:
            head = fh.read(24)
    except OSError:
        return None
    if len(head) < 24 or head[:8] != b"\x89PNG\r\n\x1a\n":
        return None
    return struct.unpack(">II", head[16:24])


def is_checked(path):
    p = path.replace("\\", "/")
    i = p.find("/textures/")
    if i == -1:
        return False
    return p[i + len("/textures/"):].startswith(("item/", "block/"))


def png_list(staged):
    if staged:
        args = ["git", "diff", "--cached", "--name-only", "--diff-filter=ACM"]
    else:
        args = ["git", "ls-files", "*.png"]
    out = subprocess.check_output(args).decode("utf-8", "ignore")
    return [l for l in out.splitlines() if l.lower().endswith(".png")]


def main():
    staged = "--staged" in sys.argv
    bad = []
    for f in png_list(staged):
        if not is_checked(f) or not os.path.exists(f):
            continue
        size = png_size(f)
        if size is None:
            continue
        w, h = size
        if w < 16 or h < 16:
            bad.append((f, w, h))

    if bad:
        sys.stderr.write("\n[pre-commit] Texture(s) smaller than 16x16:\n")
        for f, w, h in bad:
            sys.stderr.write("  %dx%d  %s\n" % (w, h, f))
        sys.stderr.write("Textures must be at least 16x16 in both dimensions.\n\n")
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
