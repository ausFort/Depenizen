package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.denizen.tags.core.ServerTagBase;
import com.denizenscript.depenizen.bukkit.events.mobarena.MobArenaStartsScriptEvent;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.denizenscript.depenizen.bukkit.commands.mobarena.MobArenaCommand;
import com.denizenscript.depenizen.bukkit.events.mobarena.MobArenaEndsScriptEvent;
import com.denizenscript.depenizen.bukkit.properties.mobarena.MobArenaPlayerProperties;
import com.denizenscript.depenizen.bukkit.objects.mobarena.MobArenaArenaTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.DenizenAPI;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.depenizen.bukkit.events.mobarena.MobArenaWaveChangesScriptEvent;
import com.denizenscript.denizencore.tags.TagManager;

public class MobArenaBridge extends Bridge {

    public static MobArenaBridge instance;

    @Override
    public void init() {
        instance = this;
        ObjectFetcher.registerWithObjectFetcher(MobArenaArenaTag.class);
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                tagEvent(event);
            }
        }, "mobarena");
        DenizenAPI.getCurrentInstance().getCommandRegistry().registerCommand(MobArenaCommand.class);
        ScriptEvent.registerScriptEvent(new MobArenaStartsScriptEvent());
        ScriptEvent.registerScriptEvent(new MobArenaEndsScriptEvent());
        ScriptEvent.registerScriptEvent(new MobArenaWaveChangesScriptEvent());
        PropertyParser.registerProperty(MobArenaPlayerProperties.class, PlayerTag.class);
    }

    public void tagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes();

        // <--[tag]
        // @attribute <mobarena[<name>]>
        // @returns MobArena
        // @plugin Depenizen, MobArena
        // @description
        // Returns the mob arena for the input name.
        // -->
        if (attribute.hasContext(1)) {
            MobArenaArenaTag arena = attribute.contextAsType(1, MobArenaArenaTag.class);
            if (arena != null) {
                event.setReplacedObject(arena.getObjectAttribute(attribute.fulfill(1)));
            }
            else if (!event.hasAlternative()) {
                Debug.echoError("Unknown mob arena '" + attribute.getContext(1) + "' for mobarena[] tag.");
            }
            return;
        }

        attribute = attribute.fulfill(1);

        // <--[tag]
        // @attribute <mobarena.arenas>
        // @returns ListTag(MobArena)
        // @plugin Depenizen, MobArena
        // @description
        // Returns a list of all MobArenas.
        // -->
        if (attribute.startsWith("arenas") || attribute.startsWith("list_arenas")) {
            ServerTagBase.listDeprecateWarn(attribute);
            ListTag arenas = new ListTag();
            for (Arena a : ((MobArena) plugin).getArenaMaster().getArenas()) {
                if (((MobArena) plugin).getArenaMaster().getArenaWithName(a.configName()) == null) {
                    continue;
                }
                arenas.addObject(new MobArenaArenaTag(a));
            }
            event.setReplacedObject(arenas.getObjectAttribute(attribute.fulfill(1)));
        }
    }
}
