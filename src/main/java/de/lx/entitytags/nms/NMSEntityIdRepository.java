package de.lx.entitytags.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import de.lx.entitytags.exceptions.NMSException;
import de.lx.entitytags.services.EntityIdRepository;
import net.minecraft.server.v1_15_R1.Entity;

public class NMSEntityIdRepository implements EntityIdRepository {

    private final Map<Integer, Boolean> idStorage = new HashMap<>();
    private AtomicInteger nmsEntityCount;

    @Override
    public int reserve() {
        setup();
        
        Optional<Entry<Integer, Boolean>> freeId = this.idStorage
            .entrySet()
            .stream()
            .filter(e -> e.getValue() == false)
            .findFirst();

        if(freeId.isPresent()){
            this.idStorage.put(freeId.get().getKey(), true);
            return freeId.get().getKey();
        }else{
            int nextId = nmsEntityCount.incrementAndGet();
            this.idStorage.put(nextId, true);
            return nextId;
        }
    }

    @Override
    public void free(int id) {
        if(!this.idStorage.containsKey(id))
            return;

        this.idStorage.put(id, false);
    }

    private void setup(){
        if (this.nmsEntityCount != null) 
            return;

        Optional<Field> entityCountField = Arrays.stream(Entity.class.getDeclaredFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()))
            .filter(f -> Modifier.isPrivate(f.getModifiers()))
            .filter(f -> Modifier.isFinal(f.getModifiers()))
            .filter(f -> f.getType() == AtomicInteger.class)
            .findFirst();

        if (!entityCountField.isPresent()) 
            throw new NMSException("'entityCount' field not found in Entity.class!");

        try {
            entityCountField.get().setAccessible(true);
            this.nmsEntityCount = (AtomicInteger) entityCountField.get().get(null);
        } catch (SecurityException | IllegalAccessException ex) {
            throw new NMSException("Could not retrieve 'entityCount' field value!", ex);
        }
    }
}