package eu.macsworks.rankmanager.managers;

import eu.macsworks.rankmanager.objects.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class GroupManager {

  private final Map<String, Group> groups = new HashMap<>();

  @Getter @Setter
  private Group defaultGroup;

  /**
   * Adds a group in the plugin.
   * @param group Group to be added
   */
  public void addGroup(Group group) {
    groups.put(group.getName(), group);
  }

  /**
   * Removes a group from the plugin. All players in this group will be on default instead. Doesn't work on default group.
   * @param group Group to be removed
   */
  public void removeGroup(Group group) {
    groups.remove(group.getName());
  }

  /**
   * Sets the current permanent group for the player
   * @param uuid Player UUID
   * @param group New group for player
   */
  public void setPermanentGroupForPlayer(UUID uuid, Group group) {
    Group currentGroup = getGroupForPlayer(uuid);
    if(currentGroup != defaultGroup) currentGroup.removeMember(uuid);

    group.addPermanentMember(uuid);
  }

  /**
   * Sets the current temporary group for the player
   * @param uuid Player UUID
   * @param group New group for player
   * @param expiration Epoch Timestamp of group membership expiration
   */
  public void setGroupForPlayer(UUID uuid, Group group, Long expiration) {
    Group currentGroup = getGroupForPlayer(uuid);
    if(currentGroup != defaultGroup) currentGroup.removeMember(uuid);

    group.addMember(uuid, expiration);
  }

  /**
   * Fetches the group the player is in. If no group is found, the default one is returned.
   * @param uuid Player UUID. Will always return a non-null object, even if the UUID is invalid
   * @return Group the player is in, or default one.
   */
  public @NonNull Group getGroupForPlayer(UUID uuid){
    Optional<Group> groupPlayer = groups.values().stream().filter(gr -> gr.isMember(uuid)).findAny();
    return groupPlayer.orElseGet(this::getDefaultGroup);
  }

  public Optional<Group> getGroup(String groupName) {
    return Optional.ofNullable(groups.get(groupName));
  }

  /**
   * Returns a list of all groups
   */
  public List<Group> getGroups() {
    return new ArrayList<>(groups.values());
  }

}
