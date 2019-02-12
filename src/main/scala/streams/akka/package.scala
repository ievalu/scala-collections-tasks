package streams

package object akka {

  trait IntStreamExceptions extends RuntimeException
  class BadButExpectedException extends IntStreamExceptions

}
