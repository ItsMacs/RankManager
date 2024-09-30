package eu.macsworks.rankmanager.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import eu.macsworks.rankmanager.RankManager;
import eu.macsworks.rankmanager.managers.GroupManager;
import eu.macsworks.rankmanager.objects.Group;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.serialize.BlockPosition;
import me.lucko.helper.sql.DatabaseCredentials;
import me.lucko.helper.sql.plugin.HelperSql;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.intellij.lang.annotations.Language;

public class PluginLoader {

  private final RankManager plugin;
  private DatabaseCredentials dbCredentials;

  private final Map<String, String> messages = new HashMap<>();

  public PluginLoader(){
    plugin = RankManager.getInstance();
  }

  public void load() {
    //Load database credentials from config - implicitly loaded
    dbCredentials = DatabaseCredentials.of(Objects.requireNonNull(plugin.getConfig().getString("sql.host")),
        plugin.getConfig().getInt("sql.port"),
        Objects.requireNonNull(plugin.getConfig().getString("sql.database")),
        Objects.requireNonNull(plugin.getConfig().getString("sql.username")),
        Objects.requireNonNull(plugin.getConfig().getString("sql.password")));

    //Create lang folder if it doesn't exist
    File langFolder = new File(plugin.getDataFolder() + "/lang");
    if(!langFolder.exists()) langFolder.mkdirs();

    //Load the right langfile
    String currentLanguage = plugin.getConfig().getString("language");
    YamlConfiguration langFile = handleFileCreation("lang/lang-" + currentLanguage + ".yml");
    langFile.getKeys(false).forEach(s -> messages.put(s, ColorTranslator.translate(langFile.getString(s))));

    //Load groups and put them into GroupManager
    GroupManager groupManager = plugin.getGroupManager();

    Schedulers.async().run(() -> {
      try(HelperSql sql = new HelperSql(dbCredentials)){
        sql.execute("CREATE TABLE IF NOT EXISTS `rm_groups` (`name` varchar(128) UNIQUE KEY, "
            + "`prefix` TEXT, "
            + "`members` TEXT, "
            + "`permissions` TEXT);");

        //Have to pass them as a List as there is no query that returns Promise<Void>
        Optional<List<Group>> promise = sql.query("SELECT * FROM rm_groups", (set) -> {
          List<Group> groups = new ArrayList<>();
          while(set.next()){
            Group group = new Group(set.getString("name"),
                set.getString("prefix"),
                set.getString("members"),
                set.getString("permissions"));

            groups.add(group);
          }

          return groups;
        });

        if(promise.isEmpty()) return;
        promise.get().forEach(groupManager::addGroup);
      }
    });

    //If the default group doesn't exist, create it
    Optional<Group> defaultGroup = groupManager.getGroup(plugin.getConfig().getString("default-group"));
    if(defaultGroup.isEmpty()){
      groupManager.setDefaultGroup(new Group(plugin.getConfig().getString("default-group")));
    }
  }

  public String getMessage(String key) {
    if (!messages.containsKey(key)) {
      return key;
    }
    return messages.get(key);
  }

  private YamlConfiguration handleFileCreation(String path){
    File file = new File(plugin.getDataFolder() + "/" + path);
    if(!file.exists()) plugin.saveResource(path, false);

    return YamlConfiguration.loadConfiguration(file);
  }

  public void save(){
    try(HelperSql sql = new HelperSql(dbCredentials)){
      Gson gson = new Gson();

      Base64.Encoder endcoder = Base64.getEncoder();
      plugin.getGroupManager().getGroups().forEach(group -> {
        @Language("MySQL") String query = "REPLACE INTO rm_groups (`name`, `prefix`, `members`, `permissions`) VALUES ('%s', '%s', '%s', '%s');"
            .formatted(group.getName(),
                group.getPrefix(),
                group.getMembersString(),
                group.getPermissionsString());
        sql.execute(query);
      });
    }
  }

}