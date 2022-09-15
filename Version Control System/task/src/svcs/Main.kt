package svcs
import java.io.File
import java.security.MessageDigest
import java.util.Base64

fun getHashValue(str:String):String {
    val md = MessageDigest.getInstance("SHA-256")
    val input = str.toByteArray()
    val bytes = md.digest(input)
    return Base64.getEncoder().encodeToString(bytes)
}


fun main(args: Array<String>) {
    val pathConfig = File("./vcs")
    val pathCommit =  File("./vcs/commits")

    if (!pathConfig.exists()) {
        pathConfig.mkdir()
        File("./vcs/log.txt").createNewFile()
        File("./vcs/config.txt").createNewFile()
        File("./vcs/index.txt").createNewFile()
    }
    if (!pathCommit.exists()) {
        pathCommit.mkdir()
    }

    if (args.size > 0) {
        val command = args.first()
        val userInput = if (args.size > 1) args[1].trim() else ""

        when (command) {
            "config" -> {
                if (args.size == 1) {
                    val text = File("./vcs/config.txt").readText()
                    if (text.length > 0) {
                        println("The username is ${text}.")
                    } else
                        println("Please, tell me who you are.")
                } else {
                    File("./vcs/config.txt").writeText(userInput.trim())
                    println("The username is $userInput.")
                }
            }

                "add" -> {
                    if (args.size == 1) {
                        val lines = File("./vcs/index.txt").readLines()
                        if (lines.isNotEmpty()) {
                            println("Tracked files:")
                            for (i in lines) {
                                println("$i")
                            }
                        } else {
                            println("Add a file to the index.")
                        }
                    } else {
                        if (File("./$userInput").exists()) {
                            File("./vcs/index.txt").appendText("$userInput\n")
                            println("The file \'$userInput\' is tracked.")
                        } else {
                            println("Can't find \'$userInput\'.")
                        }
                    }
                }
                "log" ->{
                    val logFIle = File("./vcs/log.txt").readLines()
                    if (logFIle.isNotEmpty()) {
                       // println(logFIle.joinToString())
                        val logList3 = mutableListOf<String>()
                        for (i in 0..logFIle.size - 1) {
                            if ((i + 1) % 3 == 0) {
                                logList3.add("${logFIle[i - 2]}\n${logFIle[i - 1]}\n${logFIle[i]}\n")
                            }
                        }
                        val reversed = logList3.reversed()
                        //println(reversed.joinToString())
                        for(i in reversed) {
                            println("$i")
                        }
                      //  println(logList3.joinToString())
                       // println(logFIle.joinToString())
                    } else {
                        println("No commits yet.")
                    }
                }
                "commit" -> {
                    if (args.size == 1) {
                        println("Message was not passed.")
                    } else {

                        val file1 = File("./first_file.txt")
                        val file2 = File("./second_file.txt")
                        val text1 = file1.readText()
                        val text2 = file2.readText()
                        val logFIle = File("./vcs/log.txt")
                        val hashCode = getHashValue("$text1$text2" )
                        val logFileText = logFIle.readText()
                        val isChangedFiles = logFileText.contains(hashCode)


                        if (logFileText.isEmpty()) {
                            val commName= File("./vcs/commits/$hashCode/")
                            commName.mkdir()
                            //File("./vcs/commits/$hashCode/file1.txt").createNewFile()
                            //File("./vcs/commits/$hashCode/file2.txt").createNewFile()
                            val filetoCopy1 = File("./vcs/commits/$hashCode/first_file.txt")
                            val filetoCopy2= File("./vcs/commits/$hashCode/second_file.txt")
                            file1.copyTo(filetoCopy1)
                            file2.copyTo(filetoCopy2)
                            val commitName = "commit $hashCode";
                            val userName = "Author: ${File("./vcs/config.txt").readText()}"
                            val commitText = args[1]
                            logFIle.appendText("$commitName\n$userName\n$commitText\n")
                            println("Changes are committed.")
                        } else if (isChangedFiles) {
                                println("Nothing to commit.")
                          } else if (!isChangedFiles) {
                            val commName= File("./vcs/commits/$hashCode/")
                            commName.mkdir()
                            //File("./vcs/commits/$hashCode/file1.txt").createNewFile()
                            //File("./vcs/commits/$hashCode/file2.txt").createNewFile()
                            val filetoCopy1 = File("./vcs/commits/$hashCode/first_file.txt")
                            val filetoCopy2= File("./vcs/commits/$hashCode/second_file.txt")
                            file1.copyTo(filetoCopy1)
                            file2.copyTo(filetoCopy2)

                            val commitName = "commit $hashCode"
                            val userName = "Author: ${File("./vcs/config.txt").readText()}"
                            val commitText = args[1]
                            logFIle.appendText("$commitName\n$userName\n$commitText\n")
                            println("Changes are committed.")
                        }
                    }
                }
                "checkout" -> {
                    if (args.size == 1) {
                        println("Commit id was not passed.")
                    } else {
                        val commitDir = File("./vcs/commits/")
                        val commNames = commitDir.listFiles().map{it.name}.
                    filter {it.equals(args[1]) }// [Doc.pdf, Reviews.txt]
                        if (commNames.isNotEmpty()) {
                            val filetoCopy1 = File("./vcs/commits/${args[1]}/first_file.txt")
                            val filetoCopy2= File("./vcs/commits/${args[1]}/second_file.txt")
                            val file1 = File("./first_file.txt")
                            val file2 = File("./second_file.txt")
                            file1.writeText(filetoCopy1.readText())
                            file2.writeText(filetoCopy2.readText())
                            println("Switched to commit ${args[1]}.")
                        } else {
                            println("Commit does not exist.")
                        }

                    }
                }
                "--help" -> println("""These are SVCS commands:
config     Get and set a username.
add        Add a file to the index.
log        Show commit logs.
commit     Save changes.
checkout   Restore a file.""")

                else -> {
                    println("'${command}' is not a SVCS command.")
                }
            }

    } else {
        println("""These are SVCS commands:
config     Get and set a username.
add        Add a file to the index.
log        Show commit logs.
commit     Save changes.
checkout   Restore a file.""")
}
    
}
