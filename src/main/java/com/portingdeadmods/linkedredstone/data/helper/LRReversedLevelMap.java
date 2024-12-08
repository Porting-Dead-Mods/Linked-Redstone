package com.portingdeadmods.linkedredstone.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ChunkPos;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LRReversedLevelMap {
    public static final Codec<LRReversedLevelMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.unboundedMap(Codec.STRING, LRReversedChunkMap.CODEC).fieldOf("reverse_chunk_maps").forGetter(LRReversedLevelMap::LRRLevelMapToString)
    ).apply(builder, LRReversedLevelMap::LRRLevelMapFromString));

    private final Map<ChunkPos, LRReversedChunkMap> LRReversedChunkMaps;

    public LRReversedLevelMap() {
        this.LRReversedChunkMaps = new HashMap<>();
    }

    public LRReversedLevelMap(Map<ChunkPos, LRReversedChunkMap> chunkFacadeMaps) {
        this.LRReversedChunkMaps = chunkFacadeMaps;
    }

    public Map<ChunkPos, LRReversedChunkMap> getLRReversedChunkMaps() {
        return LRReversedChunkMaps;
    }

    public static LRReversedLevelMap LRRLevelMapFromString(Map<String, LRReversedChunkMap> chunkFacade) {
        return new LRReversedLevelMap(chunkFacade.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(new ChunkPos(Long.parseLong(entry.getKey())), entry.getValue()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    public Map<String, LRReversedChunkMap> LRRLevelMapToString() {
        return getLRReversedChunkMaps().entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(String.valueOf(entry.getKey().toLong()), entry.getValue()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
