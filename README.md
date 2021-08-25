# Buttle-Ship
Battle Ship game coded in Java

There is no big bug so far in my game, this one thing may happen sometimes:
1. if the computer is too "tired", the window can not be closed (have to restart java)

I have all the one that I promised in the initial design in my game, but instead of having a user file to 
store all the information, I have a user class to store all the information

I have added a level design in my game -> level 1: 5x5; level 2: 6x6; level 3: 7x7

The setting menu includes the how to play section and the level section 
(the level setting can not be accessed during the battle process and ranking process)

For the user panel, when the name is taken, the name is too long (length > 18), and the name is empty, 
the user would be asked to renter the name; and the time initally is 10 min (user name is case sensitive ex.harry != Harry)

The place panel defalutly rotate the last moved ship, initially set to be the top left ship

The battle processhas a time limit of 10 minutes, once passed, no best time record but the current point
of the user is used for ranking.

If both the user and the AI wins, it would say the user wins - user friendly & user actually guesses beofre the AI guesses 

The ranking are based on single-turn best mark and single-turn best time record -> try to maxmize the number of different users on 
the ranking panel (user "account" can not be repeated)

Hope you enjoy the game :)) & Have a great day! 

