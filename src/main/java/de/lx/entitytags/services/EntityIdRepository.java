package de.lx.entitytags.services;

public interface EntityIdRepository {
    int reserve();
    void free(int id);
}