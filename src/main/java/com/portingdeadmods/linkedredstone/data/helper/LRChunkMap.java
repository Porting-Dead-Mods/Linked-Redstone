package com.portingdeadmods.linkedredstone.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LRChunkMap {
    public static final Codec<LRChunkMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.unboundedMap(Codec.STRING, Codec.LONG).fieldOf("chunk_map").forGetter(LRChunkMap::chunkMapToString)
    ).apply(builder, LRChunkMap::chunkMapFromString));

    private final Map<BlockPos, BlockPos> chunkMap;

    public LRChunkMap() {
        this.chunkMap = new HashMap<>();
    }

    public LRChunkMap(Map<BlockPos, BlockPos> chunkMap) {
        this.chunkMap = chunkMap;
    }

    public Map<BlockPos, BlockPos> getChunkMap() {
        return chunkMap;
    }

//    private LRChunkMap(Map<String, Long> LRChunk) {
//        this.chunkMap = LRChunk.entrySet().stream()
//                .map(entry -> new AbstractMap.SimpleEntry<>(BlockPos.of(Long.parseLong(entry.getKey())), BlockPos.of(entry.getValue())))
//                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
//    }

    private static LRChunkMap chunkMapFromString(Map<String, Long> LRChunk) {
        return new LRChunkMap(LRChunk.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(BlockPos.of(Long.parseLong(entry.getKey())), BlockPos.of(entry.getValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    private Map<String, Long> chunkMapToString() {
        return getChunkMap().entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(String.valueOf(BlockPos.asLong(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ())), entry.getValue().asLong()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
