
def call(String executable) {
  // not allowed in Groovy sandbox:
  // org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException: Scripts not permitted to use staticField java.io.File pathSeparator
  //String separator = File.pathSeparator
  // groovy.lang.MissingPropertyException: No such property: FileSystems for class: groovy.lang.Binding
  //String separator = FileSystems.default.getSeparator()

  // both methods blocked, let's try this another way...
  //for (dir in env.PATH.split('/')) {
    // not allowed in Groovy sandbox
    //def file = new File(dir, executable)
    //if (file.canExecute()) {
    //  return file.absolutePath
    //
    //def file = FileSystems.default.getPath(dir, executable)
    //if (file.isExecutable()) {
    //  return file.toAbsolutePath().toString()
    //}
  //}
  //return ''
  withEnv(["EXECUTABLE=$executable"]) {
    def path = sh(
      label: "Which $EXECUTABLE",
      //returnStatus: true, // overrides returnStdout
      returnStdout: true,
      // try all 3 because some agents containers might not have the which binary installed or bash for the type -P command
      // not only is type -P not available, but it outputs this in the output annoyingly so use without -P even though it might accidentally catch non-binaries
      // type -P $EXECUTABLE 2>/dev/null ||
      // for some reason 'type' command is printing not found to stdout, ruining the expected behaviour
      // type $EXECUTABLE 2>/dev/null ||
      script: """
        which $EXECUTABLE 2>/dev/null ||
        command -v $EXECUTABLE 2>/dev/null || :
      """
    )
    return path
  }
}
