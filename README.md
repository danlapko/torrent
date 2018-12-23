Torrent project.

Compile two jars using gradle:

`gradle jarSeed`

`gradle jarTracker`

You will get `seed.jar` and `tracker.jar` at `build/libs`.

Add aliases to bashrc:

`alias tracker='java -jar <SOME_PATH>/build/libs/tracker.jar' `
   
`alias seed='java -Xmx4096M -jar <SOME_PATH>/build/libs/seed.jar'`

Now you are able to launch tracker (command `tracker`) and client (`seed SEED_PORT`).

`seed SEED_PORT` will launch interactive command line interface.

Supported cli commands:
* `list_tracker` - list files distributed by tracker
* `list_seeding` - list files seeding by me 
* `upload fileName` - uploads file meta information to tracker and starting to seed it
* `download fileID` - download file from other seeds (`fileId` can be obtained by `list_tracker`)
* `store fileID -f fileName` - store downloaded file (with approptiate fileID) to fileName
* `remove fileID`  - remove fileID from seeding by me
* `exit` - correct session finish