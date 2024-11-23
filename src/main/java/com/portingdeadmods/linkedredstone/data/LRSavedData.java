package com.portingdeadmods.linkedredstone.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
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

/**
 * This saved data saves all facades based on chunks.
 * In future versions this system will be replaced with chunk data attachments.
 * <br>
 * Internally it uses a hashmap that maps {@link ChunkPos} to {@link LRChunkMap}
 * <br>
 * {@link LRChunkMap} maps individual {@link BlockPos}itions to their respective {@link BlockPos}
 */
public class LRSavedData extends SavedData {
    public static final String ID = "cable_facades_saved_data";

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

    public @NotNull LRChunkMap getOrCreateFacadeMapForChunk(ChunkPos chunkPos) {
        LRChunkMap map = getLRChunkMapForChunkPos(chunkPos);
        if (map == null) {
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
        return getOrCreateFacadeMapForChunk(chunkPos);
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
        getOrCreateLRChunkMapForPos(blockPos).getChunkMap().remove(blockPos);
        setDirty();
    }

    public boolean isEmpty() {
        return this.levelMap.getLRChunkMaps().isEmpty();
    }

    public @Nullable BlockPos getLinkedBlock(BlockPos blockPos) {
        LRChunkMap facadeMapForPos = getLRChunkMapForBlockPos(blockPos);
        if (facadeMapForPos != null) {
            return facadeMapForPos.getChunkMap().get(blockPos);
        }
        return null;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag) {
        DataResult<Tag> tagDataResult = LRLevelMap.CODEC.encodeStart(NbtOps.INSTANCE, this.levelMap);
        tagDataResult
                .resultOrPartial(err -> LinkedRedstone.LRLOGGER.error("Encoding error: {}", err))
                .ifPresent(tag -> compoundTag.put(ID, tag));
        return compoundTag;
    }

    private static LRSavedData load(CompoundTag compoundTag, ServerLevel serverLevel) {
        DataResult<Pair<LRLevelMap, Tag>> dataResult = LRLevelMap.CODEC.decode(NbtOps.INSTANCE, compoundTag.get(ID));
        Optional<Pair<LRLevelMap, Tag>> mapTagPair = dataResult
                .resultOrPartial(err -> LinkedRedstone.LRLOGGER.error("Decoding error: {}", err));
        if (mapTagPair.isPresent()) {
            LRLevelMap facadeMap = mapTagPair.get().getFirst();
            return new LRSavedData(facadeMap);
        }
        return new LRSavedData();
    }

    public static LRSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(compoundTag -> load(compoundTag, level), LRSavedData::new, ID);
    }
}