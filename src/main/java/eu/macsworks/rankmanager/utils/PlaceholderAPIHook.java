package eu.macsworks.rankmanager.utils;

import eu.macsworks.rankmanager.RankManager;
import eu.macsworks.rankmanager.managers.GroupManager;
import eu.macsworks.rankmanager.objects.Group;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

  private final GroupManager groupManager;

  public PlaceholderAPIHook(){
    this.groupManager = RankManager.getInstance().getGroupManager();
  }

  @Override
  public @NotNull String getIdentifier() {
    return "rankmanager";
  }

  @Override
  public @NotNull String getAuthor() {
    return "Alice Benedetti";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0";
  }

  @Override
  public String onPlaceholderRequest(Player player, String identifier) {
    switch (identifier) {
      case "group" -> { //Returns the group name for the requesting player
        Group group = groupManager.getGroupForPlayer(player.getUniqueId());
        return group.getName();
      }

      case "prefix" -> { //Returns the group prefix for the group the requesting player is in
        Group group = groupManager.getGroupForPlayer(player.getUniqueId());
        return group.getPrefix();
      }

      default -> {
        return null;
      }
    }
  }
}
