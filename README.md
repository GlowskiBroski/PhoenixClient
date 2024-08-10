![Screenshot](pictures/Logo2New.png)

PhoenixClient is a new Minecraft Client developed for Fabric modloader for the latest versions of minecraft.
It uses mojang official mappings, not loom mappings. If you would like to build the project, there are no special plugins to do so.

The client began development on June 12th, 2024 and only has a few modules, but more are being added daily

Is the client safe? Yes. You are free to look through all of the code and assess for yourself :)

PhoenixClient has 2 main feature sets; Its Modules & Its HUD

# PhoenixClient Module Menu

*Controls:*
  - RCONTROL (Changeable in the default minecraft keybinds menu) to open the module menu
  - Right click any module to open its settings window

  *Options:*
  - Custom font, color theming, and background blur can all be modified by opening the settings of the Graphics Manager Module

  *What does it look like?*
  - PhoenixClient takes a slightly different approach to the standard hacked client GUI, rendering its windows horizontally rather than vertically
  - ![Screenshot](pictures/windows/modulemenu.jpg)

  *Noteable & Unique Modules:*
  - ElytraFly: Ground Mode - VERY fast elytra fly. Works on 2B2T/GRIM (~120m/s). Its like an airplane taking off. While you are on the ground, you will accelerate, and can reach high speeds. 
  - Banners: An exploit that modifies the loom table to allow for banner patterns past 6, and up to 20.
  - FreeCam: Interact Mode - The player will have rotation packets sent towards your hitResult location. This allows you to place blocks and attack in freecam much easier
  - ContainerSort - Allows you to hold space and sort any inventory block you are working in. I LOVE it and it keeps me organized

  PhoenixClient currently has 33 Modules & counting. (Its very new, modules are scarce, but are not low quality)

# PhoenixClient HUD Editor

*Controls:*
  - RALT (Changeable in the default minecraft keybinds menu) to open the HUD menu
  - Hold Left Click + Drag to move window positions on screen
  - Right Click to pin/unpin windows from the HUD
  - SHIFT + Left Click any window to open the options menu
  - Hold SPACE to hope the window toggle menu. Here you can enable/disable menus from the GUI entirely
    
  *Universal Options:*
  - Backgrounds: All windows have the option to enable/disable their backgrounds
  - Labels: Information Windows (FPS,TPS,Direction,Rotation,Speed,Coordinates,ModuleList,EntityList,StorageList,SignTextList,KeyBindList,EntityData) have the option to enable/disable their label
  - Rendering Side & Scale: List Windows (ModuleList,EntityList,StorageList,SignTextList,KeyBindList) have the option to change their scale & Rendering Side (L/R)

  *Options Menu:*
  - You are able to view all options for a particular window with SHIFT + Left Click (Described above in Controls)
  - When you active an options window, it will appear in the center of your screen and look like this:
  - ![Screenshot](pictures/windows/optionsmenu.jpg)

  *Toggle Menu:*
  - You are able to view all window toggles by holding SPACE (Described above in Controls)
  - When you hold space, it will appear in the center of your screen and look like this:
  - ![Screenshot](pictures/windows/hudtogglemenu.jpg)

  *Window Achoring:*
  - When a window is dragged against a wall, it will have a red line on that wall. It is now 'anchored'. If the window changes, it will do so as if it is glued to that wall
  
  *What does it look like?*
  - PhoenixClient takes a slightly different approach to the standard hacked client GUI, rendering its windows horizontally rather than vertically
  - ![Screenshot](pictures/windows/hudmenu.jpg)


  PhoenixClient HUD currently offers 16 draggable windows. Here are those windows with their custom options, excluding the universal options from above
  - **FPS**: Displays the frames per second
  - **TPS**: Displays the ticks per second (in single player it reads 40. IDK why)
  - **Direction**: Displays the player facing direciton
    - You can change whether or not to have compasss directions, coordinate directions, or both
    - You can change whether or not to displace NE,NW,SE,SW or just the 4 cardinal directions
  - **Rotation**: Displays the YAW & PITCH of the player
  - **Speed**: Displays the speed in m/s (Blocks Per Second)
    - You can change whether or not to have just horizontal speed (XZ) or 3D speed (XYZ)
  - **Coordinates**: Displays the player coordinates
    - You can choose to display nether conversion coordinates
  - **Inventory**: Displays the player inventory
    - You can change the scale
    - You can change the transparency of the menu
  - **Armor**: Displays the player's currently worn armor
    - You can change the scale
  - **ModuleList**: Displays all enabled modules
    - You can make it rainbow
    - You can change whether it renders from the top or the bottom
  - **EntityList**: Displays all nearby entities
    - You can combine all items into 1 entry, or split them apart. Split items render in RED. Shulker boxes render in PURPLE
    - You can change the range at which the list detects entities
  - **StorageList**: Displays all nearby storage blocks
    - You can change the range at which the list detects storages
  - **SignTextList**: Displays all nearby sign text
    - You can change the range at which the list detects signs
    - You can enabled the coordinates for each sign
  - **KeyBindList**: Displays all keybound modules & if they are active or not
    - You can change the rendering order of the list (UP,DOWN,ABC)
  - **EntityData**: Displays data about an entity when hovered over
    - This window is UNFINISHED
  - **ChunkTrails**: A Radar window that highlights new chunks in RED and old chunks in GREEN. The radar comes with a direction line, NSEW indicators, and saves all data to the system
    - ![Screenshot](pictures/windows/chunktrails.jpg)
    - ChunkTrails comes with 3 modes for detecting new chunks, Palette, Copper, and Liquid (Palette by default)
    - You can change the size of the radar, and the scale of each chunk/pixel.
    - MultiThreading Updates: Creating the radar image may be very laggy at lower settings. It is smart to enable MultiThreading if you know your CPU can handle it.
    - NOTE: This is VERY NEW. It may be unstable as it is my first time creating a radar. It has not broken for me yet, so hopefully it works for you too
