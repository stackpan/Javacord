/*
 * Copyright (C) 2017 Bastian Oppermann
 * 
 * This file is part of Javacord.
 * 
 * Javacord is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser general Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Javacord is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.btobastian.javacord.utils.handler.user;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.Game;
import de.btobastian.javacord.entities.GameType;
import de.btobastian.javacord.entities.impl.ImplGame;
import de.btobastian.javacord.entities.impl.ImplUser;
import de.btobastian.javacord.utils.PacketHandler;
import org.json.JSONObject;

/**
 * Handles the presence update packet.
 */
public class PresenceUpdateHandler extends PacketHandler {

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public PresenceUpdateHandler(DiscordApi api) {
        super(api, true, "PRESENCE_UPDATE");
    }

    @Override
    public void handle(JSONObject packet) {
        long userId = Long.parseLong(packet.getJSONObject("user").getString("id"));
        api.getUserById(userId).map(user -> ((ImplUser) user)).ifPresent(user -> {
            if (packet.has("game")) {
                Game game = null;
                if (!packet.isNull("game")) {
                    int gameType = packet.getJSONObject("game").getInt("type");
                    String name = packet.getJSONObject("game").getString("name");
                    String streamingUrl =
                            packet.getJSONObject("game").has("url") && !packet.getJSONObject("game").isNull("url") ?
                            packet.getJSONObject("game").getString("url") : null;
                    game = new ImplGame(GameType.getGameTypeById(gameType), name, streamingUrl);
                }
                user.setGame(game);
            }
        });
    }

}