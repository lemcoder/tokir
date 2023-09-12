package pl.lemanski.tokir

expect val platform: String

class Greeting {
    fun greeting() = "Hello, $platform!"
}