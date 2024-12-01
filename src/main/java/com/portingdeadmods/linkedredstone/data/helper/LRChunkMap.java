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
                .map(entry -> {
                    String srcStr = entry.getKey();
                    Long sbLong = entry.getValue();

                    BlockPos srcBP = BlockPos.of(Long.parseLong(srcStr));
                    BlockPos sbBP = BlockPos.of(sbLong);

                    if (LRConfig.verboseDebug || LRConfig.verboseDebug) {
                        LinkedRedstone.LRLOGGER.debug("-Converting String/Long -> BP/BP-");
                        LinkedRedstone.LRLOGGER.debug("Converted src: {} -> {} {} {}", srcStr, srcBP.getX(), srcBP.getY(), srcBP.getZ());
                        LinkedRedstone.LRLOGGER.debug("Converted sb: {} -> {} {} {}", sbLong, sbBP.getX(), sbBP.getY(), sbBP.getZ());
                    }

                    return new AbstractMap.SimpleEntry<>(srcBP, sbBP);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    private Map<String, Long> chunkMapToString() {
        return getChunkMap().entrySet().stream()
                .map(entry -> {
                    BlockPos srcBP = entry.getKey();
                    BlockPos sbBP = entry.getValue();

                    String srcStr = String.valueOf(srcBP.asLong());
                    Long sbLong = sbBP.asLong();

                    if (LRConfig.verboseDebug || LRConfig.fullDebug) {
                        LinkedRedstone.LRLOGGER.debug("-Converting BP/BP to String/Long-");
                        LinkedRedstone.LRLOGGER.debug("Converted src: {} {} {} -> {}", srcBP.getX(), srcBP.getY(), srcBP.getZ(), srcStr);
                        LinkedRedstone.LRLOGGER.debug("Converted sb: {} {} {} -> {}", sbBP.getX(), sbBP.getY(), sbBP.getZ(), sbLong);
                    }
                    return new AbstractMap.SimpleEntry<>(srcStr, sbLong);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
