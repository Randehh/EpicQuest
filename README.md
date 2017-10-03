# EpicQuest, The ultimate, lightweight quest plugin!

EpicQuest is an easy to use yet highly customizable quest plugin. Immerse your players into Minecraft with a commandless questing experience through villagers, bounty signs and a book to show your progress!

## Features:
* Multiple tasks per quest
* Multiple quests at the same time
* Quest types:
  * Collect
  * Kill
  * Killplayer
  * Killanyplayer
  * Destroy
  * Place
  * Levelup
  * Enchant
  * Tame
  * Craft
  * Smelt
  * TalkToVillager
  * Killanymob
  * Killmobbyname
  * Repair
  * Goto
  * Clickblock
  * Executecommand
* Quest rewards:
  * Money (Vault)
  * Items
  * Permission group (Vault)
  * Commands execution
  * Heroes Experience
* Supported plugins:
  * Citizens
  * Heroes
  * MythicMobs and all other custom mob plugins through the "Kill Mob By Name" quest type
* Progress a quest in only the specified worlds
* Player statistics
* Permissions
* Quest requirements
* Quest lock time after finish (for daily quests and the likes)
* Sign support
* Any block as quest-giver
* Spawn villagers who give quests
* Party functionality
* Check which quests you have in a book - no commands! -
* Includes Vault integration

### Coming Soon:
* RPG Items support (collect quest type and rewards)
* BarAPI for progress tracking

## Commands
You must use either /q or /quest to precede the following commands:

Command	| Description | Permission
------- | ----------- | ----------
give | Give random quest | epicquest.user.give
give <number> | Give specified quest from the QuestList	| epicquest.user.give
turnin | Turn all your quests in | epicquest.user.turnin
questbook or qb	| Shows the quests you have	| epicquest.user.questbook
info <number> | Shows info on the specified quest from the QuestBook | epicquest.user.info
drop <number> | Drops the specified quest from the QuestBook | epicquest.user.drop
stats <playername> | Shows player statistics | epicquest.user.stats
help | Shows help pages | epicquest.user.help
questblock <give/turnin> <(if give)random/questnumber> | Options for the block you're looking at | epicquest.admin.questblock
party | Shows party members | epicquest.user.party
party invite <playername> | Invites a player to your party | epicquest.user.party
party accept | Accepts the current party invitation	| epicquest.user.party
party kick <playername>	| Kicks a player from your party if you are leader | epicquest.user.party
party leader <playername> | Makes somebody else the party leader | epicquest.user.party
party leave	| Leave your current party | epicquest.user.party
party chat | Toggles party chat | epicquest.user.party
questentity create <name> <quest> | Spawns a villager with a quest. Check the questentities.yml file after reloading the server for more advanced options! | epicquest.admin.questentity
questentity create <name> | Select a Citizen after this command to make him give quests. Check the questentities.yml file after reloading the server for more advanced options! | epicquest.admin.questentity
questentity remove <name> | Remove a villager with a quest | epicquest.admin.questentity
reload | Reload quests from the quests file | epicquest.admin.reload

## How to use
#### [Quests](http://dev.bukkit.org/bukkit-plugins/epicquest/pages/creating-quests/)
#### [Quest Givers](http://dev.bukkit.org/bukkit-plugins/epicquest/pages/how-to-use-quest-givers/)
#### [Tips and Tricks](http://dev.bukkit.org/bukkit-plugins/epicquest/pages/tips-and-tricks/)
#### [Sharing Quests](http://dev.bukkit.org/server-mods/epicquest/pages/quest-sharing/)

## Contribute!
Feel like you want to help EpicQuest grow and become more than what it is now? Simply clone the repo and import it to your favourite IDE with Maven integrated and away you go! Whatever contribution, regardless of how big or small will be appreciated and thus don't be afraid to submit a pull request.

For instructions on how to use Maven with Eclipse, [check out this guide here](http://theopentutorials.com/tutorials/eclipse/installing-m2eclipse-maven-plugin-for-eclipse/). For other IDE's, you will need to look up how to use Maven with your respective IDE.
