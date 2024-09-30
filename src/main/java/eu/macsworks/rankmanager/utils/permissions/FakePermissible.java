/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

//Taken from an old project of mine, which employed this LuckPerms snippet
package eu.macsworks.rankmanager.utils.permissions;

import eu.macsworks.rankmanager.RankManager;
import org.bukkit.entity.Player;
import org.bukkit.permissions.*;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class FakePermissible extends PermissibleBase {

  private static final Field ATTACHMENTS_FIELD;

  static {
    try {
      ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
      ATTACHMENTS_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  // the player this permissible is injected into.
  private final Player player;


  public FakePermissible(ServerOperator opable) {
    super(opable);
    player = (Player)opable;
  }

  @Override
  public boolean isPermissionSet(String permission) {
    if (permission == null) throw new NullPointerException("permission");

    return true;
  }

  @Override
  public boolean isPermissionSet(Permission permission) {
    if (permission == null) throw new NullPointerException("permission");

    return isPermissionSet(permission.getName());
  }

  @Override
  public boolean hasPermission(String permission) {
    if (permission == null) throw new NullPointerException("permission");
    if (player.isOp()) return true;

    return RankManager.getInstance().getGroupManager().getGroupForPlayer(player.getUniqueId()).hasPermission(permission);
  }

  @Override
  public boolean hasPermission(Permission permission) {
    if (permission == null) {
      throw new NullPointerException("permission");
    }

    return hasPermission(permission.getName());
  }


  @Override
  public void setOp(boolean value) {
    this.player.setOp(value);
  }

  //Unsupported
  @Override
  public Set<PermissionAttachmentInfo> getEffectivePermissions() {
    return new HashSet<>();
  }

  @Override
  public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value) {
    Objects.requireNonNull(plugin, "plugin");
    Objects.requireNonNull(permission, "permission");

    PermissionAttachment attachment = addAttachment(plugin);
    attachment.setPermission(permission, value);
    return attachment;
  }

  //Unsupported
  @Override
  public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
    return null;
  }

  //Unsupported
  @Override
  public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value, int ticks) {
    return null;
  }

  //Unsupported
  @Override
  public void removeAttachment(PermissionAttachment attachment) {

  }

  //This actually never does anything
  @Override
  public void recalculatePermissions() {

  }

  //Unsupported
  @Override
  public void clearPermissions() {

  }
}