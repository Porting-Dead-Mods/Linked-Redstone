package com.portingdeadmods.linkedredstone.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.portingdeadmods.linkedredstone.LRConfig;
import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.data.helper.LRChunkMap;
import com.portingdeadmods.linkedredstone.data.helper.LRLevelMap;
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
 * This saved data saves all facades based on chunks.
 * In future versions this system will be replaced with chunk data attachments.
 * <br>
 * Internally it uses a hashmap that maps {@link ChunkPos} to {@link LRChunkMap}
 * <br>
 * {@link LRChunkMap} maps individual {@link BlockPos}itions to their respective {@link BlockPos}
 */
public class LRSavedData extends SavedData {
    public static final String ID = "linked_redstone_saved_data";

    private final LRLevelMap levelMap;

    public LRSavedData(LRLevelMap plevelMap) {
        this.levelMap = plevelMap;
    }
    public LRSavedData() {
        this(new LRLevelMap());
    }

    public LRLevelMap getLRLevelMap() {
        return this.levelMap;
    }

    public @NotNull LRChunkMap getOrCreateLRChunkMapForChunkPos(ChunkPos chunkPos) {
        LRChunkMap map = getLRChunkMapForChunkPos(chunkPos);
        if (map == null) {
            if (LRConfig.verboseDebug || LRConfig.fullDebug) {
                LinkedRedstone.LRLOGGER.debug("Creating new LRChunkMap for chunk {}, {}", chunkPos.getRegionX(), chunkPos.getRegionZ());
            }
            map = new LRChunkMap();
            this.levelMap.getLRChunkMaps().put(chunkPos, map);
            setDirty();
        }
        return map;
    }

    public @Nullable LRChunkMap getLRChunkMapForChunkPos(ChunkPos chunkPos) {
        return this.levelMap.getLRChunkMaps().get(chunkPos);
    }

    public @NotNull LRChunkMap getOrCreateLRChunkMapForPos(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        return getOrCreateLRChunkMapForChunkPos(chunkPos);
    }

    public @Nullable LRChunkMap getLRChunkMapForBlockPos(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        return getLRChunkMapForChunkPos(chunkPos);
    }

    public void addLinkedPair(BlockPos blockPos, BlockPos blockPos2) {
        getOrCreateLRChunkMapForPos(blockPos).getChunkMap().put(blockPos, blockPos2);
        setDirty();
    }

    public void removeLinkedPair(BlockPos blockPos) {
        if (LRConfig.verboseDebug || LRConfig.fullDebug) LinkedRedstone.LRLOGGER.debug("Removing linked pair for Linked Redstone Component -> {} {} {}", blockPos.getX(), blockPos.getY(), blockPos.getZ());
        getOrCreateLRChunkMapForPos(blockPos).getChunkMap().remove(blockPos);
        setDirty();
    }

    public boolean isEmpty() {
        return this.levelMap.getLRChunkMaps().isEmpty();
    }

    public @Nullable BlockPos getLinkedBlock(BlockPos blockPos) {
        LRChunkMap LRChunkMapForBlockPos = getLRChunkMapForBlockPos(blockPos);
        if (LRChunkMapForBlockPos != null) {
            if (LRConfig.verboseDebug || LRConfig.fullDebug) {
                boolean hasPair = false;
                ChunkPos chunkPos = new ChunkPos(blockPos);
                LinkedRedstone.LRLOGGER.debug("Fetched ChunkMap for block at {} {} {} - Chunk {} {}", blockPos.getX(), blockPos.getY(), blockPos.getZ(), chunkPos.getRegionX(), chunkPos.getRegionZ());
                LinkedRedstone.LRLOGGER.debug("Chunk {} {} has pairs:", chunkPos.getRegionX(), chunkPos.getRegionZ());
                for (BlockPos src : LRChunkMapForBlockPos.getChunkMap().keySet()) {
                    BlockPos sb = LRChunkMapForBlockPos.getChunkMap().get(src);
                    LinkedRedstone.LRLOGGER.debug("Linked block at {} -> {}", src, sb);
                    if (src.equals(blockPos)) {
                        hasPair = true;
                    }
                }
                LinkedRedstone.LRLOGGER.debug("Supplied blockPos: {}", blockPos);
                if (hasPair) {
                    LinkedRedstone.LRLOGGER.debug("Supplied redstone component has a pair, is something broken?");
                }
            }

            return LRChunkMapForBlockPos.getChunkMap().get(blockPos);
        }
        if (LRConfig.verboseDebug || LRConfig.fullDebug) LinkedRedstone.LRLOGGER.error("Fetched ChunkMap for block at {} {} {} returned null?", blockPos.getX(), blockPos.getY(), blockPos.getZ());

        return null;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        AtomicBoolean errored = new AtomicBoolean(false);
        DataResult<Tag> tagDataResult = LRLevelMap.CODEC.encodeStart(NbtOps.INSTANCE, this.levelMap);
        tagDataResult
                .resultOrPartial(err -> {
                    LinkedRedstone.LRLOGGER.error("Encoding error: {}", err);
                    errored.set(true);
                })
                .ifPresent(tag -> compoundTag.put(ID, tag));
        if (LRConfig.verboseDebug || LRConfig.fullDebug) {
            if (errored.get()) {
                LinkedRedstone.LRLOGGER.debug("Failed to save LRLevelMap to saved data, read error above.");
            } else {
                LinkedRedstone.LRLOGGER.debug("Successfully saved LRLevelMap to saved data");
            }
        }
        return compoundTag;
    }

    private static LRSavedData load(CompoundTag compoundTag, ServerLevel serverLevel) {
        DataResult<Pair<LRLevelMap, Tag>> dataResult = LRLevelMap.CODEC.decode(NbtOps.INSTANCE, compoundTag.get(ID));
        Optional<Pair<LRLevelMap, Tag>> mapTagPair = dataResult
                .resultOrPartial(err -> LinkedRedstone.LRLOGGER.error("Decoding error: {}", err));
        if (mapTagPair.isPresent()) {
            LRLevelMap lrLevelMap = mapTagPair.get().getFirst();
            if (LRConfig.verboseDebug || LRConfig.fullDebug) {
                LinkedRedstone.LRLOGGER.debug("Successfully loaded LRLevelMap from saved data");
            }
            return new LRSavedData(lrLevelMap);
        }
        if (LRConfig.verboseDebug || LRConfig.fullDebug) {
            LinkedRedstone.LRLOGGER.debug("No LRLevelMap found in saved data, creating new one");
        }
        return new LRSavedData();
    }

    public static LRSavedData get(ServerLevel level) {
        LRSavedData data = level.getDataStorage().computeIfAbsent(compoundTag -> load(compoundTag, level), LRSavedData::new, ID);

        if (LRConfig.fullDebug) {

            LinkedRedstone.LRLOGGER.debug("----- START OF LRSavedData DUMP -----");
            LinkedRedstone.LRLOGGER.debug("Fetched LRSavedData for level {}", level.dimension().location().getPath());
            for (ChunkPos chunkpos : data.levelMap.getLRChunkMaps().keySet()) {
                LinkedRedstone.LRLOGGER.debug("Dimension {} - Chunk: {}, {}", level.dimensionTypeId().toString(), chunkpos.getRegionX(), chunkpos.getRegionZ());
                LRChunkMap chunkMap = data.levelMap.getLRChunkMaps().get(chunkpos);
                for (BlockPos src : chunkMap.getChunkMap().keySet()) {
                    BlockPos sb = chunkMap.getChunkMap().get(src);
                    LinkedRedstone.LRLOGGER.debug("Linked block at {} -> {}", src, sb);
                }
            }
            LinkedRedstone.LRLOGGER.debug("------ END OF LRSavedData DUMP ------");
        }
        return data;
    }
}