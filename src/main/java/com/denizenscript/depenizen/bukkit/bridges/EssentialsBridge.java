package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.tags.core.ServerTagBase;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.denizenscript.depenizen.bukkit.events.essentials.*;
import com.denizenscript.depenizen.bukkit.properties.essentials.EssentialsItemProperties;
import com.denizenscript.depenizen.bukkit.properties.essentials.EssentialsPlayerProperties;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;

public class EssentialsBridge extends Bridge {

    public static EssentialsBridge instance;

    @Override
    public void init() {
        instance = this;
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                tagEvent(event);
            }
        }, "essentials");
        ScriptEvent.registerScriptEvent(new PlayerAFKStatusScriptEvent());
        ScriptEvent.registerScriptEvent(new PlayerGodModeStatusScriptEvent());
        ScriptEvent.registerScriptEvent(new PlayerJailStatusScriptEvent());
        ScriptEvent.registerScriptEvent(new PlayerMuteStatusScriptEvent());
        ScriptEvent.registerScriptEvent(new PlayerBalanceChangeScriptEvent());
        PropertyParser.registerProperty(EssentialsPlayerProperties.class, PlayerTag.class);
        PropertyParser.registerProperty(EssentialsItemProperties.class, ItemTag.class);
    }

    public void tagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes().fulfill(1);


        // <--[tag]
        // @attribute <essentials.warps>
        // @returns ListTag(Element)
        // @plugin Depenizen, Essentials
        // @description
        // Returns a list of all Warp names.
        // -->
        if (attribute.startsWith("warps") || attribute.startsWith("list_warps")) {
            ServerTagBase.listDeprecateWarn(attribute);
            Essentials essentials = (Essentials) plugin;
            ListTag warps = new ListTag();
            warps.addAll(essentials.getWarps().getList());
            event.setReplacedObject(warps.getObjectAttribute(attribute.fulfill(1)));
            return;
        }

        // <--[tag]
        // @attribute <essentials.warp[<warp name>]>
        // @returns LocationTag
        // @plugin Depenizen, Essentials
        // @description
        // Returns the location of the warp name.
        // -->
        if (attribute.startsWith("warp") && attribute.hasContext(1)) {
            Essentials essentials = (Essentials) plugin;
            try {
                Location loc = essentials.getWarps().getWarp(attribute.getContext(1));
                event.setReplacedObject(new LocationTag(loc).getObjectAttribute(attribute.fulfill(1)));
            } catch (WarpNotFoundException e) {
                Debug.echoError("Warp not found");
            } catch (InvalidWorldException e) {
                Debug.echoError("Invalid world for getting warp");
            }
        }
    }
}
