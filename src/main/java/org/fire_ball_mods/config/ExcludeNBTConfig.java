package org.fire_ball_mods.config;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class ExcludeNBTConfig extends BaseConfig{
    private HashMap<String, List<String>> excludesNbt = new HashMap<>();

    public ExcludeNBTConfig(String path, String name) {
        super(path + name);
    }

    public List<String> getExcludesNBT(String idBlock) {
        List<String> result = new ArrayList<>();
        if(excludesNbt.containsKey(idBlock)) {
            result = excludesNbt.get(idBlock);
        }
        return result;
    }

    public void addList(String id, List<String> keys) {
        excludesNbt.put(id, keys);
        save();
    }

    @Override
    public void copy(BaseConfig object) {
        ExcludeNBTConfig config = (ExcludeNBTConfig) object;
        excludesNbt = config.excludesNbt;
    }
}
