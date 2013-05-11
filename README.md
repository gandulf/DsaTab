DsaTab
======

An android app to handle your [DSA][2] Pen&amp;Paper roleplaying characters.
DsaTab requires Android 2.2 and is compatible with the newest version of [Helden-Software][1] 5.2.6

Howto use DsaTab
================
DsaTab builds upon the software [Helden-Software][1] and enables you to take your character with you on your android smartphone or tablet.

* Create your character using Helden-Software
* Export your hero as a xml file
* Start DsaTab on smartphone to see where the dsatab folder is created (settings).
* Copy the xml file to your smartphone/tablet into the SD-CARD/dsatab folder. You could also use dropbox or another synchonization tool to sync the files to your smartphone and change the path to the folder in the dsatab settings.
* Start DsaTab again and load your character.
* Don't forget to save the character to keep changes made in DsaTab for the next session.

Development
===========
To deploy this app you need the following Libraries in your Eclipse workspace:

* ActionBarSherlock
  Can be downloaded from http://actionbarsherlock.com, you need at least version 4.2.0+.
	Extract the directory library from the downloaded file and create a android library project named ActionBarSherlock with it.

* GuiLib (Combination of the following libraries below
	Can be checked out from google code too: see https://github.com/gandulf/GuiLib for further instructions
	Create a library project named GuiLib.

	* Drag Sort list
		Can be downloaded from https://github.com/bauerca/drag-sort-listview	
	
	* PhotoView
		Can be downloaded from https://github.com/chrisbanes/PhotoView	
	
	* ShowcaseView
		Can be downloaded from https://github.com/Espiandev/ShowcaseView	
		
	* Draggable GridView 
		Can be downloaded from https://github.com/thquinn/DraggableGridView
	
	* GridViewCompat
		Can be downloaded from https://github.com/paramvir-b/AndroidGridViewCompatLib
	
* GridLayout v7 support library from google sdk	
	Can be downloaded from google using sdk manager



License
=======

    Copyright 2012 Gandulf Kohlweiss

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[1]: [http://www.helden-software.de/]
[2]: [http://www.dasschwarzeauge.de/]
