package org.fire_ball_mods.config;

public class MainConfig extends BaseConfig {
    public int INTERVAL_REGEN = 360;
    public boolean IS_DEBUG = true;
    public int DEFAULT_WEIGHT = 10;
    public String TP_COMMAND = "tppos [x] [y] [z] [world]";
    public String VERSION_CONFIG = "0.9.5";

    public MainConfig(String path, String name) {
        super(path+name);
    }

    @Override
    public void copy(BaseConfig object) {
        MainConfig config = (MainConfig) object;
        INTERVAL_REGEN = config.INTERVAL_REGEN;
        IS_DEBUG = config.IS_DEBUG;
        DEFAULT_WEIGHT = config.DEFAULT_WEIGHT;
        TP_COMMAND = config.TP_COMMAND;
        VERSION_CONFIG = config.VERSION_CONFIG;
    }
}
