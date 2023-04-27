package org.fire_ball_mods.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class RegenChestData {
    public String lastTimeOpen = "";
    public UUID idLoot;
}
