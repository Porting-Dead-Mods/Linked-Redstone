package com.portingdeadmods.linkedredstone.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ChunkPos;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LRLevelMap {
    public static final Codec<LRLevelMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.unboundedMap(Codec.STRING, LRChunkMap.CODEC).fieldOf("chunks_map").forGetter(LRLevelMap::LRLevelMapToString)
    ).apply(builder, LRLevelMap::LRLevelMapFromString));

    private final Map<ChunkPos, LRChunkMap> LRChunkMaps;

    public LRLevelMap() {
        this.LRChunkMaps = new HashMap<>();
    }

    public LRLevelMap(Map<ChunkPos, LRChunkMap> chunkFacadeMaps) {
        this.LRChunkMaps = chunkFacadeMaps;
    }

    public Map<ChunkPos, LRChunkMap> getLRChunkMaps() {
        return LRChunkMaps;
    }

    public static LRLevelMap LRLevelMapFromString(Map<String, LRChunkMap> chunkFacade) {
        return new LRLevelMap(chunkFacade.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(new ChunkPos(Long.parseLong(entry.getKey())), entry.getValue()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    public Map<String, LRChunkMap> LRLevelMapToString() {
        return getLRChunkMaps().entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(String.valueOf(entry.getKey().toLong()), entry.getValue()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
