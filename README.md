# id1212_homework2_hangman
The second homework in the id1212 course, which is a continuation on the previous project
called id1212_homework_hangman



How to run this program:

To run this program simply clone the repo, open up a terminal or your favorite IDE at the directory you cloned to, and run gradle build.
more information available at https://spring.io/guides/gs/gradle/

The current syntax accepts three commands; connect, guess and quit. They work as follows:

connect ipadress port - Tries to connect the client to the server using the specified ipadress and port number.

The portnumber that the server defaults to is 51234, so if you want to run this locally please connect to 127.0.0.1 51234 like so : connect 127.0.0.1 51234.

quit - just a simple quit will close the connection and terminate the application.

guess letter/word - guess is the bread and butter of this application, this is what you will run most of the time. You can guess either a word or a letter, using the same syntax.

If you want to run the server on some other port you can easily change that in the Server class residing in the Server subproject.
