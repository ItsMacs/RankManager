package eu.macsworks.rankmanager.objects;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Group {

  @Getter
  private final String name;

  @Getter @Setter
  private String prefix = "";


  //K = Player UUID
  //V = When the group will expire for them, unix epoch timestamp. Long.MAX_VALUE for permanent ones. (Which will expire in more than 100.000 years)
  private final Map<UUID, Long> members = new HashMap<>();
  private final List<String> permissions = new ArrayList<>();

  public Group(String name, String prefix, String membersEncoded, String permissionsEncoded){
    this.name = name;
    this.prefix = prefix;

    Base64.Decoder decoder = Base64.getDecoder();

    String membersDecoded = new String(decoder.decode(membersEncoded));
    for(String member : membersDecoded.split(",")){
      if(member.isEmpty()) continue;
      members.put(UUID.fromString(member.split(":")[0]), Long.parseLong(member.split(":")[1]));
    }

   if(!permissionsEncoded.isEmpty()){
     String permissionsDecoded = new String(decoder.decode(permissionsEncoded));
     Collections.addAll(permissions, permissionsDecoded.split(","));
   }
  }

  /**
   * Returns whether this player is a member of this group or not. If this is the default group,
   * it'll always return true. Always use GroupManager#getGroupForPlayer instead of running a loop on every group with this method.
   * @param uuid
   * @return
   */
  public boolean isMember(UUID uuid) {
    //Remove expired members, used here to avoid having a task as this needs to be run every time we want to check for a user
    //This is effectively the same as having a task, but without the task overhead
    members.values().removeIf(l -> ZonedDateTime.now().toEpochSecond() > l);

    return members.containsKey(uuid);
  }

  /**
   * Adds the player as a permanent member of this group
   * @param uuid Player UUID
   */
  public void addPermanentMember(UUID uuid) {
    members.put(uuid, Long.MAX_VALUE);
  }

  /**
   * Adds the player as a member for a limited time only
   * @param uuid Player UUID
   * @param expirationDate Epoch Timestamp of the expiration date (in seconds)
   */
  public void addMember(UUID uuid, Long expirationDate){
    members.put(uuid, expirationDate);
  }

  /**
   * Removes the player from this group. Will always succeed regardless of whether the player is in this group or not.
   * @param uuid Player UUID
   */
  public void removeMember(UUID uuid) {
    members.remove(uuid);
  }

  public boolean hasPermission(String permission) {
    if(permissions.contains("*")) return true; //If this group has the star permission, don't even bother with the other checks

    String formPermission = permission.toLowerCase();
    if(permissions.contains(formPermission)) return true; //If the plain permission is contained, return true

    //If this group has a star permission for this specific permission type, return true
    if(permissions.stream().anyMatch(perm ->
        permission.startsWith(perm.replace(".*", "")) && perm.endsWith(".*"))) return true;

    //No checks have been successful, we don't have the permission
    return false;
  }

  /**
   * Returns when (in an epoch timestamp seconds format) the player's membership to the group will expire.
   * Will return Long.MAX_VALUE for both permanent members and non-members. Do not use this to check for membership.
   * @param uuid Player UUID
   * @return Either the timestamp (in seconds) at which the membership will expire, or MAX_VALUE for permanent
   */
  public long getExpirationDate(UUID uuid) {
    if(members.containsKey(uuid)) return members.get(uuid);

    return Long.MAX_VALUE;
  }

  public void addPermission(String permission) {
    permissions.add(permission.toLowerCase());
  }

  public void removePermission(String permission) {
    permissions.remove(permission.toLowerCase());
  }

  /**
   * @return A Base64 encoded String of all this group's members
   */
  public String getMembersString(){
    StringBuilder output = new StringBuilder();

    for(Map.Entry<UUID, Long> entry : members.entrySet()){
      output.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
    }

    return Base64.getEncoder().encodeToString(output.toString().getBytes());
  }

  /**
   * @return A Base64 encoded String of all this group's permissions
   */
  public String getPermissionsString(){
    return Base64.getEncoder().encodeToString(String.join(",", permissions.toArray(new String[0])).getBytes());
  }
}
