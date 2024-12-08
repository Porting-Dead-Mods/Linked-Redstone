package com.portingdeadmods.linkedredstone.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.portingdeadmods.linkedredstone.LRConfig;
import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.data.helper.LRReversedChunkMap;
import com.portingdeadmods.linkedredstone.data.helper.LRReversedLevelMap;
import com.portingdeadmods.linkedredstone.mixins.SignalGetterMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This saved data compared to the normal {@link LRSavedData} is used to save all
 * blockPos pairs in reverse order, SB -> SRC for {@link SignalGetterMixin} to directly
 * query the SB instead of querying the normal chunk map for a key to a value
 */

public class LRReversedSavedData extends SavedData {
    public static final String ID = "linked_redstone_reversed_saved_data";
    
    private final LRReversedLevelMap reversedLevelMap;

    public LRReversedSavedData(LRReversedLevelMap preversedlevelMap) {
        this.reversedLevelMap = preversedlevelMap;
    }
    
    public LRReversedSavedData() {
        this(new LRReversedLevelMap());
    }

    public LRReversedLevelMap getLRRLevelMap() {
        return this.reversedLevelMap;
    }

    public LRReversedLevelMap getReversedLevelMap() {
        return this.reversedLevelMap;
    }

    public @NotNull LRReversedChunkMap getOrCreateLRRChunkMapForChunkPos(ChunkPos chunkPos) {
        LRReversedChunkMap map = getLRRChunkMapForChunkPos(chunkPos);
        if (map == null) {

            if (LRConfig.verboseDebug || LRConfig.fullDebug) {
                LinkedRedstone.LRLOGGER.debug("Creating new LRChunkMap for chunk {}, {}", chunkPos.getRegionX(), chunkPos.getRegionZ());
            }

            map = new LRReversedChunkMap();
            this.reversedLevelMap.getLRReversedChunkMaps().put(chunkPos, map);
            setDirty();
        }
        return map;
    }

    public @Nullable LRReversedChunkMap getLRRChunkMapForChunkPos(ChunkPos chunkPos) {
        return this.reversedLevelMap.getLRReversedChunkMaps().get(chunkPos);
    }

    public @NotNull LRReversedChunkMap getOrCreateLRReversedChunkMapForPos(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        return getOrCreateLRRChunkMapForChunkPos(chunkPos);
    }

    public @Nullable LRReversedChunkMap getLRRChunkMapForBlockPos(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        return getLRRChunkMapForChunkPos(chunkPos);
    }

    public void addLinkedPair(BlockPos src, BlockPos sb) {
        getOrCreateLRReversedChunkMapForPos(src).getReversedChunkMap().put(src, sb);
        setDirty();
    }

    public void removeLinkedPair(BlockPos blockPos) {
        if (LRConfig.verboseDebug || LRConfig.fullDebug) LinkedRedstone.LRLOGGER.debug("Removing linked pair for Linked Redstone Component -> {} {} {}", blockPos.getX(), blockPos.getY(), blockPos.getZ());
        getOrCreateLRReversedChunkMapForPos(blockPos).getReversedChunkMap().remove(blockPos);
        setDirty();
    }

    public boolean isEmpty() {
        return this.reversedLevelMap.getLRReversedChunkMaps().isEmpty();
    }

    public @Nullable BlockPos getLinkedBlock(BlockPos blockPos) {
        LRReversedChunkMap LRRChunkMapForBlockPos = getLRRChunkMapForBlockPos(blockPos);
        if (LRRChunkMapForBlockPos != null) {
            if (LRConfig.verboseDebug || LRConfig.fullDebug) {
                boolean hasPair = false;
                ChunkPos chunkPos = new ChunkPos(blockPos);
                LinkedRedstone.LRLOGGER.debug("Fetched ReversedChunkMap for block at {} {} {} - Chunk {} {}", blockPos.getX(), blockPos.getY(), blockPos.getZ(), chunkPos.getRegionX(), chunkPos.getRegionZ());
                LinkedRedstone.LRLOGGER.debug("Chunk {} {} has pairs:", chunkPos.getRegionX(), chunkPos.getRegionZ());
                for (BlockPos src : LRRChunkMapForBlockPos.getReversedChunkMap().keySet()) {
                    BlockPos sb = LRRChunkMapForBlockPos.getReversedChunkMap().get(src);
                    LinkedRedstone.LRLOGGER.debug("Linked block at {} -> {}", src, sb);
                    if (src.equals(blockPos)) {
                        hasPair = true;
                    }
                }
                LinkedRedstone.LRLOGGER.debug("Supplied blockPos: {}", blockPos);
                if (hasPair) {
                    LinkedRedstone.LRLOGGER.debug("Supplied block has a pair, is something broken?");
                }
            }

            return LRRChunkMapForBlockPos.getReversedChunkMap().get(blockPos);
        }

        if (LRConfig.verboseDebug || LRConfig.fullDebug) LinkedRedstone.LRLOGGER.error("Fetched ReversedChunkMap for block at {} {} {} returned null?", blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return null;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        AtomicBoolean errored = new AtomicBoolean(false);
        DataResult<Tag> tagDataResult = LRReversedLevelMap.CODEC.encodeStart(NbtOps.INSTANCE, this.reversedLevelMap);
        tagDataResult
                .resultOrPartial(err -> {
                    LinkedRedstone.LRLOGGER.error("Encoding error: {}", err);
                    errored.set(true);
                })
                .ifPresent(tag -> compoundTag.put(ID, tag));

        if (LRConfig.verboseDebug || LRConfig.fullDebug) {
            if (errored.get()) {
                LinkedRedstone.LRLOGGER.debug("Failed to save LRRLevelMap to saved data, read error above.");
            } else {
                LinkedRedstone.LRLOGGER.debug("Successfully saved LRRLevelMap to saved data");
            }
        }

        return compoundTag;
    }

    private static LRReversedSavedData load(CompoundTag compoundTag, ServerLevel serverLevel) {
        DataResult<Pair<LRReversedLevelMap, Tag>> dataResult = LRReversedLevelMap.CODEC.decode(NbtOps.INSTANCE, compoundTag.get(ID));
        Optional<Pair<LRReversedLevelMap, Tag>> mapTagPair = dataResult
                .resultOrPartial(err -> LinkedRedstone.LRLOGGER.error("Decoding error: {}", err));
        if (mapTagPair.isPresent()) {
            LRReversedLevelMap lrrLevelMap = mapTagPair.get().getFirst();
            if (LRConfig.verboseDebug || LRConfig.fullDebug) {
                LinkedRedstone.LRLOGGER.debug("Successfully loaded LRRLevelMap from saved data");
            }
            return new LRReversedSavedData(lrrLevelMap);
        }

        if (LRConfig.verboseDebug || LRConfig.fullDebug) {
            LinkedRedstone.LRLOGGER.debug("No LRRLevelMap found in saved data, creating new one");
        }

        return new LRReversedSavedData();
    }

    public static LRReversedSavedData get(ServerLevel level) {
        LRReversedSavedData data = level.getDataStorage().computeIfAbsent(compoundTag -> load(compoundTag, level), LRReversedSavedData::new, ID);

        if (LRConfig.fullDebug) {

            LinkedRedstone.LRLOGGER.debug("----- START OF LRRSavedData DUMP -----");
            LinkedRedstone.LRLOGGER.debug("Fetched LRRSavedData for level {}", level.dimension().location().getPath());
            for (ChunkPos chunkpos : data.reversedLevelMap.getLRReversedChunkMaps().keySet()) {
                LinkedRedstone.LRLOGGER.debug("Dimension {} - Chunk: {}, {}", level.dimensionTypeId().toString(), chunkpos.getRegionX(), chunkpos.getRegionZ());
                LRReversedChunkMap chunkMap = data.reversedLevelMap.getLRReversedChunkMaps().get(chunkpos);
                for (BlockPos sb : chunkMap.getReversedChunkMap().keySet()) {
                    BlockPos src = chunkMap.getReversedChunkMap().get(sb);
                    LinkedRedstone.LRLOGGER.debug("Linked block at {} -> {}", sb, src);
                }
            }
            LinkedRedstone.LRLOGGER.debug("------ END OF LRRSavedData DUMP ------");
        }

        return data;
    }
}