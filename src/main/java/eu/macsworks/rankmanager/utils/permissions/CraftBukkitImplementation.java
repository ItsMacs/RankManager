package eu.macsworks.rankmanager.utils.permissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

//Taken from an old project of mine, which employed this LuckPerms snippet
public final class CraftBukkitImplementation {

  private static final String SERVER_PACKAGE_VERSION;

  static {
    Class<?> server = Bukkit.getServer().getClass();
    Matcher matcher = Pattern.compile("^org\\.bukkit\\.craftbukkit\\.(\\w+)\\.CraftServer$").matcher(server.getName());
    SERVER_PACKAGE_VERSION = matcher.matches() ? '.' + matcher.group(1) + '.' : ".";
  }


  public static Class<?> obcClass(String className) throws ClassNotFoundException {
    return Class.forName("org.bukkit.craftbukkit" + SERVER_PACKAGE_VERSION + className);
  }
}