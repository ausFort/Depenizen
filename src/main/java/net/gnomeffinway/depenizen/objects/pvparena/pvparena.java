package net.gnomeffinway.depenizen.objects.pvparena;

import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.TagContext;
import net.aufdemrand.denizencore.utilities.debugging.dB;
import net.slipcor.pvparena.arena.Arena;
import net.slipcor.pvparena.arena.ArenaPlayer;
import net.slipcor.pvparena.managers.ArenaManager;

public class pvparena implements dObject {

    String prefix = "PVPArena";
    Arena arena = null;

    public static boolean matches(String name) {
        name = name.replace("pvparena@", "");
        return ArenaManager.getArenaByName(name) != null;
    }

    public static pvparena valueOf(String name) {
        return valueOf(name, null);
    }

    public static pvparena valueOf(String name, TagContext context) {
        name = name.replace("pvparena@", "");
        Arena a = ArenaManager.getArenaByName(name);
        if (a == null) {
            return null;
        }
        return new pvparena(a);
    }

    public pvparena(Arena a) {
        if (a != null) {
            this.arena = a;
        }
        else {
            dB.echoError("Arena referenced is null");
        }
    }

    public Arena getArena() {
        return arena;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public dObject setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String debug() {
        return prefix + "='<A>" + identify() + "<G>' ";
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String getObjectType() {
        return "PVPArena";
    }

    @Override
    public String identify() {
        return "pvparena@" + arena.getName();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <pvparena@pvparena.name>
        // @returns Element
        // @description
        // Returns the name of the pvparena.
        // @plugin Depenizen, PVPArena
        if (attribute.startsWith("name")) {
            return new Element(arena.getName()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <pvparena@pvparena.fighters>
        // @returns dList(dPlayer)
        // @description
        // Returns a list of all fighters in the arena.
        // @plugin Depenizen, PvPArena
        // -->
        else if (attribute.startsWith("fighters")) {
            dList fighters = new dList();
            for (ArenaPlayer p : arena.getFighters()) {
                fighters.add(new dPlayer(p.get()).identify());
            }
            return fighters.getAttribute(attribute.fulfill(1));
        }

        // NOTE: Deprecated.
        if (attribute.startsWith("playercount") || attribute.startsWith("player_count")) {
            return new Element(arena.getFighters().size()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <pvparena@pvparena.type>
        // @returns Element
        // @description
        // Always returns 'PVPArena' for PVPArena objects. All objects fetchable by the Object Fetcher will return the
        // type of object that is fulfilling this attribute.
        // @plugin Depenizen, PVPArena
        // -->
        else if (attribute.startsWith("type")) {
            return new Element("PVPArena").getAttribute(attribute.fulfill(1));
        }

        return new Element(identify()).getAttribute(attribute);
    }
}
