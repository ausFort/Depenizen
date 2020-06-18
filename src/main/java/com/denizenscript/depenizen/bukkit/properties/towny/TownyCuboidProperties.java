package com.denizenscript.depenizen.bukkit.properties.towny;

import com.denizenscript.denizen.tags.core.ServerTagBase;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.depenizen.bukkit.objects.towny.TownTag;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.denizenscript.denizen.objects.CuboidTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TownyCuboidProperties implements Property {

    @Override
    public String getPropertyString() {
        return null;
    }

    @Override
    public String getPropertyId() {
        return "TownyCuboid";
    }

    @Override
    public void adjust(Mechanism mechanism) {
        // None
    }

    public static boolean describes(ObjectTag object) {
        return object instanceof CuboidTag;
    }

    public static TownyCuboidProperties getFrom(ObjectTag object) {
        if (!describes(object)) {
            return null;
        }
        else {
            return new TownyCuboidProperties((CuboidTag) object);
        }
    }

    public static final String[] handledTags = new String[] {
            "has_town", "list_towns"
    };

    public static final String[] handledMechs = new String[] {
    }; // None

    private TownyCuboidProperties(CuboidTag cuboid) {
        this.cuboid = cuboid;
    }

    CuboidTag cuboid;

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <CuboidTag.has_town>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, Towny
        // @description
        // Returns whether the cuboid contains any town at all.
        // -->
        if (attribute.startsWith("has_town")) {
            for (Location location : cuboid.getBlockLocationsUnfiltered()) {
                if (TownyUniverse.getTownName(location) != null) {
                    return new ElementTag(true).getAttribute(attribute.fulfill(1));
                }
            }
            return new ElementTag(false).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <CuboidTag.towns>
        // @returns ListTag(TownTag)
        // @plugin Depenizen, Towny
        // @description
        // Returns all the towns within the cuboid.
        // -->
        if (attribute.startsWith("towns") || attribute.startsWith("list_towns")) {
            ServerTagBase.listDeprecateWarn(attribute);
            ListTag list = new ListTag();
            List<String> towns = new ArrayList<>();
            try {
                for (Location location : cuboid.getBlockLocationsUnfiltered()) {
                    String townName = TownyUniverse.getTownName(location);
                    if (townName != null && !towns.contains(townName)) {
                        list.addObject(new TownTag(TownyUniverse.getTownBlock(location).getTown()));
                        towns.add(townName);
                    }
                }
            }
            catch (NotRegisteredException e) {
            }
            return list.getAttribute(attribute.fulfill(1));
        }

        return null;
    }
}
