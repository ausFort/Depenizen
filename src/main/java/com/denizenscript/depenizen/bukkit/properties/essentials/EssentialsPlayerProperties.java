package com.denizenscript.depenizen.bukkit.properties.essentials;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.tags.core.ServerTagBase;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import com.denizenscript.depenizen.bukkit.bridges.EssentialsBridge;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public class EssentialsPlayerProperties implements Property {

    public static final String[] handledTags = new String[] {
            "god_mode", "has_home", "is_afk", "is_muted", "is_vanished", "home_list", "home_location_list",
            "ignored_players", "home_name_list", "mail_list", "mute_timout", "socialspy",
            "list_home_locations", "list_home_names", "list_homes", "list_mails", "homes", "mails", "vanished", "muted", "afk"
    };
    public static final String[] handledMechs = new String[] {
            "is_afk", "god_mode", "is_muted", "socialspy", "vanish", "essentials_ignore", "muted", "afk", "vanished"
    };
    PlayerTag player;

    private EssentialsPlayerProperties(PlayerTag player) {
        this.player = player;
    }

    public static boolean describes(ObjectTag object) {
        return object instanceof PlayerTag && ((PlayerTag) object).isOnline();
    }

    public static EssentialsPlayerProperties getFrom(ObjectTag object) {
        if (!describes(object)) {
            return null;
        }
        else {
            return new EssentialsPlayerProperties((PlayerTag) object);
        }
    }

    @Override
    public String getPropertyString() {
        return null;
    }

    @Override
    public String getPropertyId() {
        return "EssentialsPlayer";
    }

    public User getUser() {
        return ((Essentials) EssentialsBridge.instance.plugin).getUser(player.getOfflinePlayer().getUniqueId());
    }

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <PlayerTag.god_mode>
        // @returns ElementTag(Boolean)
        // @mechanism PlayerTag.god_mode
        // @plugin Depenizen, Essentials
        // @description
        // Returns whether the player is currently in god mode.
        // -->
        if (attribute.startsWith("god_mode")) {
            return new ElementTag(getUser().isGodModeEnabled()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.has_home>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, Essentials
        // @description
        // Returns whether the player has set at least one home.
        // -->
        if (attribute.startsWith("has_home")) {
            return new ElementTag(getUser().hasHome()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.afk>
        // @returns ElementTag(Boolean)
        // @mechanism PlayerTag.afk
        // @plugin Depenizen, Essentials
        // @description
        // Returns whether the player is AFK.
        // -->
        if (attribute.startsWith("is_afk") || attribute.startsWith("afk")) {
            return new ElementTag(getUser().isAfk()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.muted>
        // @returns ElementTag(Boolean)
        // @mechanism PlayerTag.muted
        // @plugin Depenizen, Essentials
        // @description
        // Returns whether the player is muted.
        // -->
        if (attribute.startsWith("is_muted") || attribute.startsWith("muted")) {
            return new ElementTag(getUser().isMuted()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.vanished>
        // @returns ElementTag(Boolean)
        // @mechanism PlayerTag.vanished
        // @plugin Depenizen, Essentials
        // @description
        // Returns whether the player is vanished.
        // -->
        if (attribute.startsWith("is_vanished") || attribute.startsWith("vanished")) {
            return new ElementTag(getUser().isVanished()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.homes>
        // @returns MapTag
        // @plugin Depenizen, Essentials
        // @description
        // Returns a map of the homes of the player.
        // -->
        if (attribute.startsWith("homes")) {
            ServerTagBase.listDeprecateWarn(attribute);
            MapTag homes = new MapTag();
            for (String home : getUser().getHomes()) {
                try {
                    homes.map.put(new StringHolder(home), new LocationTag(getUser().getHome(home)));
                }
                catch (Exception e) {
                    if (!attribute.hasAlternative()) {
                        Debug.echoError(e);
                    }
                }
            }
            return homes.getAttribute(attribute.fulfill(1));
        }

        if (attribute.startsWith("list_homes") || attribute.startsWith("home_list")) {
            Debug.echoError("<player.list_homes> is deprecated: use <player.homes> instead.");
            ListTag homes = new ListTag();
            for (String home : getUser().getHomes()) {
                try {
                    homes.add(home + "/" + new LocationTag(getUser().getHome(home)).identifySimple());
                }
                catch (Exception e) {
                    if (!attribute.hasAlternative()) {
                        Debug.echoError(e);
                    }
                }
            }
            return homes.getAttribute(attribute.fulfill(1));
        }

        if (attribute.startsWith("list_home_locations") || attribute.startsWith("home_location_list")) {
            Debug.echoError("<player.list_home_locations> is deprecated: use <player.homes> instead.");
            ListTag homes = new ListTag();
            for (String home : getUser().getHomes()) {
                try {
                    homes.addObject(new LocationTag(getUser().getHome(home)));
                }
                catch (Exception e) {
                    if (!attribute.hasAlternative()) {
                        Debug.echoError(e);
                    }
                }
            }
            return homes.getAttribute(attribute.fulfill(1));
        }

        if (attribute.startsWith("list_home_names") || attribute.startsWith("home_name_list")) {
            Debug.echoError("<player.list_home_names> is deprecated: use <player.homes> instead.");
            return new ListTag(getUser().getHomes()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.ignored_players>
        // @returns ListTag(PlayerTag)
        // @plugin Depenizen, Essentials
        // @description
        // Returns a list of the ignored players of the player.
        // -->
        if (attribute.startsWith("ignored_players")) {
            ListTag players = new ListTag();
            Essentials ess = (Essentials) EssentialsBridge.instance.plugin;
            for (String player : getUser()._getIgnoredPlayers()) {
                try {
                    players.addObject(new PlayerTag(ess.getOfflineUser(player).getBase()));
                }
                catch (Exception e) {
                    if (!attribute.hasAlternative()) {
                        Debug.echoError(e);
                    }
                }
            }
            return players.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.mails>
        // @returns ListTag
        // @plugin Depenizen, Essentials
        // @description
        // Returns a list of mail the player currently has.
        // -->
        if (attribute.startsWith("mails") || attribute.startsWith("list_mails") || attribute.startsWith("mail_list")) {
            ServerTagBase.listDeprecateWarn(attribute);
            return new ListTag(getUser().getMails()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.mute_timeout>
        // @returns DurationTag
        // @plugin Depenizen, Essentials
        // @description
        // Returns how much time is left until the player is un-muted.
        // -->
        if (attribute.startsWith("mute_timeout")) {
            return new DurationTag((getUser().getMuteTimeout() - System.currentTimeMillis()))
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.socialspy>
        // @returns ElementTag(Boolean)
        // @mechanism PlayerTag.socialspy
        // @plugin Depenizen, Essentials
        // @description
        // Returns whether the player has SocialSpy enabled.
        // -->
        if (attribute.startsWith("socialspy")) {
            return new ElementTag(getUser().isSocialSpyEnabled()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object PlayerTag
        // @name afk
        // @input ElementTag(Boolean)
        // @plugin Depenizen, Essentials
        // @description
        // Sets whether the player is marked as AFK.
        // @tags
        // <PlayerTag.afk>
        // -->
        if ((mechanism.matches("is_afk") || mechanism.matches("afk")) && mechanism.requireBoolean()) {
            getUser().setAfk(mechanism.getValue().asBoolean());
        }

        // <--[mechanism]
        // @object PlayerTag
        // @name god_mode
        // @input ElementTag(Boolean)
        // @plugin Depenizen, Essentials
        // @description
        // Sets whether the player has god mode enabled.
        // @tags
        // <PlayerTag.god_mode>
        // -->
        if (mechanism.matches("god_mode") && mechanism.requireBoolean()) {
            getUser().setGodModeEnabled(mechanism.getValue().asBoolean());
        }

        // <--[mechanism]
        // @object PlayerTag
        // @name muted
        // @input ElementTag(Boolean)(|Duration)
        // @plugin Depenizen, Essentials
        // @description
        // Sets whether the player is muted. Optionally, you may also
        // specify a duration to set how long they are muted for.
        // @tags
        // <PlayerTag.muted>
        // <PlayerTag.mute_timeout>
        // -->
        if ((mechanism.matches("is_muted") || mechanism.matches("muted")) && mechanism.requireBoolean()) {
            ListTag split = mechanism.valueAsType(ListTag.class);
            getUser().setMuted(new ElementTag(split.get(0)).asBoolean());
            if (split.size() > 1) {
                getUser().setMuteTimeout(System.currentTimeMillis() + DurationTag.valueOf(split.get(1), mechanism.context).getMillis());
            }
        }

        // <--[mechanism]
        // @object PlayerTag
        // @name socialspy
        // @input ElementTag(Boolean)
        // @plugin Depenizen, Essentials
        // @description
        // Sets whether the player has SocialSpy enabled.
        // @tags
        // <PlayerTag.socialspy>
        // -->
        if (mechanism.matches("socialspy") && mechanism.requireBoolean()) {
            getUser().setSocialSpyEnabled(mechanism.getValue().asBoolean());
        }

        // <--[mechanism]
        // @object PlayerTag
        // @name vanished
        // @input ElementTag(Boolean)
        // @plugin Depenizen, Essentials
        // @description
        // Sets whether the player has vanish enabled.
        // @tags
        // <PlayerTag.vanished>
        // -->
        if ((mechanism.matches("vanish") || mechanism.matches("vanished")) && mechanism.requireBoolean()) {
            getUser().setVanished(mechanism.getValue().asBoolean());
        }

        // <--[mechanism]
        // @object PlayerTag
        // @name essentials_ignore
        // @input PlayerTag(|ElementTag(Boolean))
        // @plugin Depenizen, Essentials
        // @description
        // Sets whether the player should ignore another player.
        // Optionally, specify a boolean indicate whether to ignore (defaults to true).
        // @tags
        // <PlayerTag.ignored_players>
        // -->
        if (mechanism.matches("essentials_ignore")) {
            Essentials ess = (Essentials) EssentialsBridge.instance.plugin;
            ListTag split = mechanism.valueAsType(ListTag.class);
            PlayerTag otherPlayer = PlayerTag.valueOf(split.get(0), mechanism.context);
            boolean shouldIgnore = split.size() < 2 || new ElementTag(split.get(1)).asBoolean();
            getUser().setIgnoredPlayer(ess.getUser(otherPlayer.getOfflinePlayer().getUniqueId()), shouldIgnore);
        }

    }
}
