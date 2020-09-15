import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import platform.posix.getenv

fun readLines(path: String): List<String> {

    val file = fopen(path, "r")
    val lines = mutableListOf<String>()

    try {
        memScoped {
            val bufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(bufferLength)
            while (true) {
                val nextLine: String? = fgets(buffer, bufferLength, file)?.toKString()
                if (nextLine == null || nextLine.isEmpty()) {
                    break
                }
                lines.add(nextLine)
            }
        }

    } finally {
        fclose(file)
    }
    return lines
}

fun main() {
    println("Hello, Kotlin/Native!")

    val home: String = getenv("HOME")?.toKString() ?: ""
    val lines: List<String> = readLines("${home}/Desktop/test.txt")
        .map { it.trim() }
    lines.forEach { println(it) }

}