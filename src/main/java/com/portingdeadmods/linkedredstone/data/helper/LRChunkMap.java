package com.portingdeadmods.linkedredstone.data.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.linkedredstone.LRConfig;
import com.portingdeadmods.linkedredstone.LinkedRedstone;
import net.minecraft.core.BlockPos;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LRChunkMap {
    public static final Codec<LRChunkMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.unboundedMap(Codec.LONG, Codec.LONG).fieldOf("chunk_map").forGetter(LRChunkMap::chunkMapToLong)
    ).apply(builder, LRChunkMap::chunkMapFromLong));

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

    private static LRChunkMap chunkMapFromLong(Map<Long, Long> LRChunk) {
        return new LRChunkMap(LRChunk.entrySet().stream()
                .map(entry -> {
                    Long srcLong = entry.getKey();
                    Long sbLong = entry.getValue();

                    BlockPos srcBP = BlockPos.of(srcLong);
                    BlockPos sbBP = BlockPos.of(sbLong);

                    if (LRConfig.verboseDebug) {
                        LinkedRedstone.LRLOGGER.debug("-Converting ChunkMap from Long-");
                        LinkedRedstone.LRLOGGER.debug("Converted src: {} -> {} {} {}", srcLong, srcBP.getX(), srcBP.getY(), srcBP.getZ());
                        LinkedRedstone.LRLOGGER.debug("Converted sb: {} -> {} {} {}", sbLong, sbBP.getX(), sbBP.getY(), sbBP.getZ());
                    }

                    return new AbstractMap.SimpleEntry<>(srcBP, sbBP);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    private Map<Long, Long> chunkMapToLong() {
        return getChunkMap().entrySet().stream()
                .map(entry -> {
                    BlockPos srcBP = entry.getKey();
                    BlockPos sbBP = entry.getValue();

                    Long srcLong = srcBP.asLong();
                    Long sbLong = sbBP.asLong();

                    if (LRConfig.verboseDebug) {
                        LinkedRedstone.LRLOGGER.debug("-Converting ChunkMap to Long-");
                        LinkedRedstone.LRLOGGER.debug("Converted src: {} {} {} -> {}", srcBP.getX(), srcBP.getY(), srcBP.getZ(), sbLong);
                        LinkedRedstone.LRLOGGER.debug("Converted sb: {} {} {} -> {}", sbBP.getX(), sbBP.getY(), sbBP.getZ(), sbLong);
                    }
                    return new AbstractMap.SimpleEntry<>(srcLong, sbLong);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
