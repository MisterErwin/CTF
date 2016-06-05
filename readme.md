

This is the source of the [CTF bukkit plugin](http://dev.bukkit.org/bukkit-plugins/ctf/) from late 2012.

(Which is a plain copy from the MCCTF server done on my couch to learn the bukkit API :D)

The current master branch "should" run on a 1.9.x spigot server.

I (MisterErwin/Alex) did the mistake of letting a code analysis run on this source. Now I am amazed that it ever worked 
and ashamed at the same time...


Apparently I never heard of enums, equals-String-comparison or DRY...

If you are that crazy and want to continue/update the CTF plugin just contact me. But I highly suggest rewriting it from scratch.

**Do not try to learn java or the bukkit API with this plugin. Just don't**

##Notes when using this plugin (for whatever reason)
There are per-class permissions. In the source they are referred as `premiumclasses`. 
Make sure to follow the Minecraft EULA and do **not** sell them via shops!

##Notes when building this plugin
The *core/* directory contains the CTF plugin itself.

The *CTFClasses/* directory contains the classes from MCCTF back then.

Both will be packed into the *target/* directory.




_~MisterErwin - with a nostalgic feeling_