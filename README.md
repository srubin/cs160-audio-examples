cs160-audio-examples
====================

(mostly) Android audio processing examples for UC Berkeley CS160, Spring 2014.

Speech to Text
--------------

Requires that Google Voice Search is installed on the device or emulator. To install this on an emulator (_not_ Genymotion) you can grab the apk for Google Voice search here: https://docs.google.com/file/d/0B5rZBNIQG5NWZHV0dWdGVmkzRG8/edit

Click the button and then begin speaking. The text box below will be update after you stop speaking.

Text to Speech
--------------

Type your text in the edit box and then press the Speak button.

Speech to Phonemes
------------------

This is not for Android, but could run on a server that your app could access.

There's no code to list here, but here are detailed instructions on how to get Speech-to-phoneme recognition set up using Sphinx: http://cmusphinx.sourceforge.net/wiki/phonemerecognition

Detecting pre-recorded audio segments
-------------------------------------

A la Shazam/SoundHound, but could be applied to detecting any fixed sounds.

See music fingerprinting code:

* Open source [dejavu](https://github.com/worldveil/dejavu)
* Echoprint from The Echonest: http://echoprint.me/

More information about the subject: http://en.wikipedia.org/wiki/Acoustic_fingerprint
