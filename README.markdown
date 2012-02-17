# Ikari Warriors VS Zombies

I'm throwing this up on the internet because "why not." This is was a learning project when I was new to Java, and I thought it would be fun to open-source it. Literally the worst thing I've ever written. YOU ARE WARNED.

## Background

I spent a summer on this because I wanted to learn how Java programming worked, and so why not build a horribly complicated Swing application that only uses classes and OO concepts when I was absolutely forced to!

## Why This is Terrible

It's a 2,123 java file with 13 classes in it. Still not convinced? Go check out `renderGame()` to see my complete lack of knowledge about proper polymorphism in OO. Or my incredible overuse of comments without explaining anything at all. Or the `getKey()` method where I have nested switch statements, definitely a low point in my programming career. How can you pick just one reason why this is terrible?

Also I had _just_ seen Evil Dead for the first time in my life.

However, this does have the greatest two lines in the history of programming!

'''java
	// Length of ABBA_CLIP audio in milliseconds
	private static final int ABBA_TIME =				3748;
'''

## Building from Source

Incredibly enough, this still runs just as well as ever (that is, not well). I credit Java with that - I had nothing to do with it. To get it working, do the following:

'''
javac IKVZ.java -Xlint:unchecked -Xlint:deprecation
java IKVZ
'''

## Artwork

Nearly all the artwork used within was cobbled together from random websites in the days before Google Image search. If you find something that belongs to you, let me know.

## License

Copyright (C) 2002 Scott Parker

Distributed under the Eclipse Public License.
