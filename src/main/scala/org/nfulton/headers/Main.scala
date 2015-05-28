package org.nfulton.headers

import java.io.{FileWriter, BufferedWriter, File}

/**
 * Prepends the contents of a file to all .scala files in a given path.
 * Usage:
 *    java -jar blah.jar /blah/src/main/scala ./LICENSE_HEADER.TXT
 */
object Main {
  def main(args : Array[String]) : Unit = {
    validation(args)

    val path        = new File(args(0))
    val sourceFiles = allSourceFiles(path)

    val headerFile  = new File(args(1))
    val header      = scala.io.Source.fromFile(headerFile).mkString

    sourceFiles.foreach(prependHeader(header))
  }

  private def validation(args : Array[String]) : Unit =
    if(args.length == 2) {
      val path       = new File(args(0))
      val headerFile = new File(args(1))

      assert(path.exists && path.isDirectory && path.canRead && path.canWrite,
        "Specified path (first argument) should be an exteand readable/writable director.")
      assert(headerFile.exists && headerFile.isFile && headerFile.canRead,
        "Specified file (second argument) should be an extand readable file")
    }
    else assert(false, "Expected two arguments; found " + args.length)

  private def prependHeader(header : String)(sourceFile : File) : Unit = {
    assert(sourceFile.isFile && sourceFile.canWrite && isSourceFile(sourceFile))
    val newContents = header + scala.io.Source.fromFile(sourceFile).mkString
    val writer = new BufferedWriter(new FileWriter(sourceFile))
    writer.write(newContents)
    writer.close
  }

  private def allSourceFiles(d : File) : Array[File] = {
    assert(d.isDirectory && d.canRead)
    d.listFiles.flatMap(f => if(f.isFile) {
      ifSourceFile(f) match {
        case Some(theFile) => theFile :: Nil
        case None          => Nil
      }
    }
    else allSourceFiles(f))
  }

  private def isSourceFile(file : File) =
    file.isFile && file.canWrite && file.getAbsolutePath.endsWith("scala")

  private def ifSourceFile(file : File) =
    if(isSourceFile(file)) Some(file)
    else None
}