##RankManager

###Setup
- Copy **helper**, **helper-sql** and **RankManager** JAR files in your server *plugins* folder.
- Enable **helper** and **helper-sql**, and put your SQL connection details in the **helper-sql** folder.
- Once both hard dependencies are enabled, enable **RankManager**.
- Enter your SQL connection details in the **RankManager** config.yml file.
- Restart the plugins.

Note: If there are errors in console about SQL, it's normal as the connection with the default values will always fail.

###Commands
- /rm and /rankmanager: Admin command (Required permission node: rm.admin)
- /rank: Sends info to the sender about their rank

###Signs
The plugin also supports sign info, simply create a sign with the first line being *[RankInfo]*


###Dependencies and Libraries
####Hard dependencies:
- helper
- helper-sql

####Soft dependencies:
- PlaceholderAPI

####Libraries:
- ACF by aikar
- Lombok
- JUnit
