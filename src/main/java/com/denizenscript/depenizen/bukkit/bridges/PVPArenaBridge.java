package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.denizen.tags.core.ServerTagBase;
import com.denizenscript.depenizen.bukkit.events.pvparena.PlayerExitsPVPArenaScriptEvent;
import com.denizenscript.depenizen.bukkit.events.pvparena.PlayerJoinsPVPArenaScriptEvent;
import com.denizenscript.depenizen.bukkit.events.pvparena.PlayerLeavesPVPArenaScriptEvent;
import com.denizenscript.depenizen.bukkit.events.pvparena.PVPArenaStartsScriptEvent;
import com.denizenscript.depenizen.bukkit.properties.pvparena.PVPArenaPlayerProperties;
import com.denizenscript.depenizen.bukkit.objects.pvparena.PVPArenaArenaTag;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagManager;
import net.slipcor.pvparena.arena.Arena;
import net.slipcor.pvparena.managers.ArenaManager;

public class PVPArenaBridge extends Bridge {

    @Override
    public void init() {
        ScriptEvent.registerScriptEvent(new PVPArenaStartsScriptEvent());
        ScriptEvent.registerScriptEvent(new PlayerJoinsPVPArenaScriptEvent());
        ScriptEvent.registerScriptEvent(new PlayerLeavesPVPArenaScriptEvent());
        ScriptEvent.registerScriptEvent(new PlayerExitsPVPArenaScriptEvent());
        PropertyParser.registerProperty(PVPArenaPlayerProperties.class, PlayerTag.class);
        ObjectFetcher.registerWithObjectFetcher(PVPArenaArenaTag.class);
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                tagEvent(event);
            }
        }, "pvparena");
    }

    public void tagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes();

        // <--[tag]
        // @attribute <pvparena[<name>]>
        // @returns PVPArena
        // @plugin Depenizen, PVPArena
        // @description
        // Returns the PVPArena by the input name.
        // -->
        if (attribute.hasContext(1)) {
            PVPArenaArenaTag arena = attribute.contextAsType(1, PVPArenaArenaTag.class);
            if (arena != null) {
                event.setReplacedObject(arena.getObjectAttribute(attribute.fulfill(1)));
            }
            else if (!event.hasAlternative()) {
                Debug.echoError("Unknown arena '" + attribute.getContext(1) + "' for pvparena[] tag.");
            }
            return;
        }
        attribute = attribute.fulfill(1);

        // <--[tag]
        // @attribute <pvparena.arenas>
        // @returns ListTag(PVPArena)
        // @plugin Depenizen, PVPArena
        // @description
        // Returns a list of all PVPArenas.
        // -->
        if (attribute.startsWith("arenas") || attribute.startsWith("list_arenas")) {
            ServerTagBase.listDeprecateWarn(attribute);
            ListTag arenas = new ListTag();
            for (Arena arena : ArenaManager.getArenas()) {
                arenas.addObject(new PVPArenaArenaTag(arena));
            }
            event.setReplacedObject(arenas.getObjectAttribute(attribute.fulfill(1)));
        }

    }
}
