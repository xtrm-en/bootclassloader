plugins {
    id("fr.stardustenterprises.rust.wrapper")
}

rust {
    this.command.set("cross")

    listOf(
        "x86_64-pc-windows-gnu" to "bcl64.dll",
        "x86_64-unknown-linux-gnu" to "libbcl64.so",
//        "x86_64-apple-darwin" to "libbcl64.dylib",
        "i686-pc-windows-gnu" to "bcl.dll",
        "i686-unknown-linux-gnu" to "libbcl.so",
    ).forEach(this.targets::plusAssign)

    this.release.set(true)
}
