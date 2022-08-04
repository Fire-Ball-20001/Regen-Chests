package org.fire_ball.config;

public class MainConfig extends BaseConfig {
    public int INTERVAL_REGEN = 360;
    public boolean IS_DEBUG = true;

    public MainConfig(String path, String name) {
        super(path+name);
    }

    @Override
    public void copy(BaseConfig object) {
        MainConfig config = (MainConfig) object;
        INTERVAL_REGEN = config.INTERVAL_REGEN;
        IS_DEBUG = config.IS_DEBUG;
    }
}
