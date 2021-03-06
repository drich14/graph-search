import io.kotlintest.TestCaseContext
import io.kotlintest.matchers.*
import io.kotlintest.specs.FreeSpec
import search.nodeNotFoundExceptionMsg
import java.io.PrintStream
import java.io.ByteArrayOutputStream


internal fun String.addNewLine(): String {
    return this + System.getProperty("line.separator")
}

class MainTest : FreeSpec() {

    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()

    override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
        // capture std out and err
        with(context.spec as MainTest) {
            setUpStreams()
            test()
            cleanUpStreams()
        }
    }

    init {
        "main" - {
            "prints expected output" {
                main(arrayOf("src/test/resources/graph.txt"))
                errContent.toString() shouldBe ""
                outContent.toString() shouldBe findFile("src/test/resources/output.txt").readText()
            }

            "prints expected output2" {
                main(arrayOf("src/test/resources/second_graph.txt"))
                errContent.toString() shouldBe ""
                outContent.toString() shouldBe findFile("src/test/resources/output2.txt").readText()
            }

            "prints error when file not found" {
                val path = "test"
                main(arrayOf(path))
                errContent.toString() shouldBe fileNotFoundMsg(path).addNewLine()
            }

            "prints error when file path not passed as argument" {
                main(arrayOf())
                errContent.toString() shouldBe FileArgExceptionMsg.addNewLine()
            }

            "prints error when initial or goal state not found in graph" {
                main(arrayOf("src/test/resources/graph1.txt"))
                errContent.toString() shouldBe nodeNotFoundExceptionMsg(StateImpl('S')).addNewLine()
            }
        }

        "findFile" - {
            "finds existent file" {
                findFile("src/test/resources/graph1.txt") shouldNotBe null
            }

            "throws exception when file not found" {
                val path = "test"
                val exception = shouldThrow<RuntimeException> {
                    findFile(path)
                }
                exception.message shouldBe fileNotFoundMsg(path)
            }
        }

        "parseArgs" - {
            "returns first argument if present" {
                val path = "test"
                parseArgs(arrayOf(path, "c")) shouldBe path
            }

            "throws exception when argument is not present" {
                val exception = shouldThrow<RuntimeException> {
                    parseArgs(arrayOf())
                }
                exception.message shouldBe FileArgExceptionMsg
            }
        }
    }

    private fun setUpStreams() {
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    private fun cleanUpStreams() {
        System.setOut(null)
        System.setErr(null)
    }
}