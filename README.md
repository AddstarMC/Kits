# Kits

Source code is provided by Dragonphase. You may modify this in anyway you like as long as you do not publish this as your own or make this your own in any way.

## Overview

Kits is a simple yet deceptively powerful plugin allowing you to create, edit and spawn custom kits in-game. When creating and editing a kit, you are provided an inventory in which you will be able to place any item into; changes are made when this inventory is closed.

If you want to contribute to the development of Kits, fork the repository and commit new changes. Don't forget to submit a pull request.

### Usage

Add Kits as a dependency inside your `plugin.yml` file. Retrieve the Kits instance inside your onEnable method and use the methods provided within KitManager:

```java
private KitManager kitManager;

public void onEnable() {
    if (!getServer().getPluginManager().getPlugin("Kits").isEnabled()) {
        getServer().getPluginManager().disablePlugin(this);
    } else {
        kitManager = Kits.getInstance().getKitManager();
    }
}
```

You can also use the KitSpawnEvent to handle and modify the spawning of kits. Here's a really basic example:

```java
@EventHandler
public void onKitSpawn(KitSpawnEvent event){
    if (event.getPlayer().hasPermission("my.custom.permission")) {
        event.setDelay(0);
        event.getPlayer().sendMessage("You have special privileges!");
    } else {
        event.getPlayer().sendMessage("You don't have special privileges!");
        event.setCancelled(true);
    }
}
```
