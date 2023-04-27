package org.fire_ball_mods.util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDUtils {

    public static List<UUID> getUUIDsWithPartialUUID(String partialUuid, List<UUID> uuids) {
        return uuids.stream().filter(uuid -> uuid.toString().startsWith(partialUuid)).collect(Collectors.toList());
    }
}
