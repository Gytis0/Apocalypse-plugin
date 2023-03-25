package Utility;

import logic.Difficulty;
import logic.Hordes;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class YamlConfig {
    private File file;
    private FileConfiguration config;

    public YamlConfig(Plugin plugin, String fileName, Object logic) {
        this.file = new File(plugin.getDataFolder().getAbsolutePath() + "/" + fileName);
        if(this.file.length() == 0){
            //save();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            InputStream stream = plugin.getResource(fileName);
            FileReading.copyInputStreamToFile(stream, this.file);
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);

        if(logic.getClass().equals(Difficulty.class)) loadFromConfigDifficultySettings((Difficulty) logic);
        else if(logic.getClass().equals(Hordes.class)) loadFromConfigHordeSpawningSettings((Hordes) logic);
    }

    public boolean save(){
        try{
            this.config.save(this.file);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public File getFile(){
        return this.file;
    }

    public FileConfiguration getConfig(){
        return this.config;
    }

    public void setConfig(FileConfiguration config){
        this.config = config;
    }

    private void loadFromConfigDifficultySettings(Difficulty difficulty){
        try{
            FileConfiguration c = getConfig();
            ConfigurationSection cs;
            for(String setting : difficulty.getAvailableSettings()){
                cs = c.getConfigurationSection(setting);
                if (cs != null) {
                    difficulty.getSetting(setting).deserialize(cs.get("base"), cs.get("linear"), cs.get("scale"));
                }
                else {
                    Bukkit.getLogger().info("Configuration Section for [" + setting + "] could not be found.");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Bukkit.getLogger().info("The difficulty settings couldn't be loaded.");
        }
    }

    private void loadFromConfigHordeSpawningSettings(Hordes hordes){
        try{
            FileConfiguration c = getConfig();
            ConfigurationSection cs;
        }
        catch(Exception e){
            e.printStackTrace();
            Bukkit.getLogger().info("The difficulty settings couldn't be loaded.");
        }
    }

}
