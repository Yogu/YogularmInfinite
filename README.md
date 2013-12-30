Yogularm Infinite
=================

Yogularm Infinite is a Java game in which Yogu - the player - jumps and runs
through an auto-generated 2-dimensional world. As the name says, it is
impossible to finish the game. However, the goal is to collect as many coins as
possibly. Chickens make their own way through the dizzy air, and you have to
watch out for arrows... 

How to start Yogularm Infinite
-------------------------------

This repository does not contain the binary files of Yogularm. Either follow
the steps below to set up Eclipse, or [start Yogularm from the web][web].

How to develop
-------------------

1. [Eclipse][eclipse] must be installed, and this repository
   downloaded completely.
   - If you want to develop for android, install [android sdk][sdk] next
2. Import the projects (de.yogularm.common and is required, the others are
   optional) into your workspace.
3. If you want to develop for desktop, set up the correct native libraries:
   Create a symbolic link from `de.yogularm.desktop/build/.classpath-PLATFORM`
   (where `PLATFORM` is your platform, e.g. `win32` or `linux64`) to
   `de.yogularm.desktop/.classpath`. Alternatively, you can create a copy.
4. If you want to distribute your own edition of Yogularm, you should set up
   Java WebStart. If you only want to run it locally, you don't need this step.
   - Read the [article about how to create keystore][jws]
   - Save the keystore as `de.yogularm.desktop/build/keystore`
   - Copy `de.yogularm.desktop/build/default.properties` to `build.properties` and
     insert the storepass as value for `sign.storepass`
5. Build the complete workspace. It may be neccessary to open a java file, make
   a little change, save it, undo the change and save it again before compiling.
6. Open the file `de.yogularm.desktop/src/de.yogularm.desktop/SwingLauncher.java`
   and run it as a Java Application.

If you want to contribute to Yogularm, please [fork the git repository][github]

Links
------

* http://www.yogularm.de/software/yogularm
* http://github.com/Yogu/YogularmInfinite

Contact: info@yogularm.de

[jws]: http://jogamp.org/wiki/index.php/Using_JOGL_in_Java_Web_Start#Signing_your_JARs
[sdk]: http://developer.android.com/sdk/index.html
[github]: http://github.com/Yogu/YogularmInfinite
[web]: http://www.yogularm.de/software/yogularm
[eclipse]: http://eclipse.org/
