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

/*
    * This class is used to store the chunk map in a reversed order, SB -> SRC
    * The use is to directly get the SB instead of querying the normal chunk map for a key to a value
    * This should save a lot of useless calls, and should be a decent optimisation
 */

public class LRReversedChunkMap {
    public static final Codec<LRReversedChunkMap> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.unboundedMap(Codec.STRING, Codec.LONG).fieldOf("reversed_chunk_map").forGetter(LRReversedChunkMap::reversedChunkMapToString)
    ).apply(builder, LRReversedChunkMap::reversedChunkMapFromString));

    private final Map<BlockPos, BlockPos> reversedChunkMap;

    public LRReversedChunkMap() {
        this.reversedChunkMap = new HashMap<>();
    }

    public LRReversedChunkMap(Map<BlockPos, BlockPos> reversedChunkMap) {
        this.reversedChunkMap = reversedChunkMap;
    }

    public Map<BlockPos, BlockPos> getReversedChunkMap() {
        return reversedChunkMap;
    }

//    private LRChunkMap(Map<String, Long> LRChunk) {
//        this.chunkMap = LRChunk.entrySet().stream()
//                .map(entry -> new AbstractMap.SimpleEntry<>(BlockPos.of(Long.parseLong(entry.getKey())), BlockPos.of(entry.getValue())))
//                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
//    }

    private static LRReversedChunkMap reversedChunkMapFromString(Map<String, Long> LRChunk) {
        return new LRReversedChunkMap(LRChunk.entrySet().stream()
                .map(entry -> {
                    String sbStr = entry.getKey();
                    Long srcLong = entry.getValue();

                    BlockPos sbBP = BlockPos.of(Long.parseLong(sbStr));
                    BlockPos srcBP = BlockPos.of(srcLong);

                    if (LRConfig.verboseDebug || LRConfig.verboseDebug) {
                        LinkedRedstone.LRLOGGER.debug("-(REVERSED) Converting String/Long -> BP/BP-");
                        LinkedRedstone.LRLOGGER.debug("Converted src: {} -> {} {} {}", sbStr, sbBP.getX(), sbBP.getY(), sbBP.getZ());
                        LinkedRedstone.LRLOGGER.debug("Converted sb: {} -> {} {} {}", srcLong, srcBP.getX(), srcBP.getY(), srcBP.getZ());
                    }

                    return new AbstractMap.SimpleEntry<>(srcBP, sbBP);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    private Map<String, Long> reversedChunkMapToString() {
        return getReversedChunkMap().entrySet().stream()
                .map(entry -> {
                    BlockPos sbBP = entry.getKey();
                    BlockPos srcBP = entry.getValue();

                    String sbStr = String.valueOf(sbBP.asLong());
                    Long srcLong = srcBP.asLong();

                    if (LRConfig.verboseDebug || LRConfig.fullDebug) {
                        LinkedRedstone.LRLOGGER.debug("-(REVERSED) Converting BP/BP to String/Long-");
                        LinkedRedstone.LRLOGGER.debug("Converted sb: {} {} {} -> {}", sbBP.getX(), sbBP.getY(), sbBP.getZ(), sbStr);
                        LinkedRedstone.LRLOGGER.debug("Converted src: {} {} {} -> {}", srcBP.getX(), srcBP.getY(), srcBP.getZ(), srcLong);
                    }
                    return new AbstractMap.SimpleEntry<>(sbStr, srcLong);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
