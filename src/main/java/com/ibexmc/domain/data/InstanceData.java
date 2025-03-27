package com.ibexmc.domain.data;

import com.ibexmc.domain.Domain;
import com.ibexmc.domain.dependencies.papi.PAPIValues;
import com.ibexmc.internal.functions.TimeFunctions;

import java.sql.Timestamp;
import java.util.*;

 public class InstanceData {
     private Domain instance;

     public InstanceData() {
         this.instance = Domain.getInstance();
     }

     public InstanceData(Domain instance) {
         this.instance = instance;
     }

     private final Bypass bypass = new Bypass();
     private final EnderPearlThrown enderPearlThrown = new EnderPearlThrown();
     private final PAPI papi = new PAPI();

     public Bypass bypass() {
         return this.bypass;
     }

     public EnderPearlThrown enderPearlThrown() {
         return this.enderPearlThrown;
     }

     public PAPI papi() {
         return this.papi;
     }


     public class Bypass {
         private Set<UUID> bypassPlayers = new HashSet<>();

         public Set<UUID> get() {
             return this.bypassPlayers;
         }

         public boolean has(UUID uuid) {
             return this.bypassPlayers.contains(uuid);
         }

         public void set(Set<UUID> bypassPlayers) {
             this.bypassPlayers = bypassPlayers;
         }

         public void put(UUID uuid) {
             this.bypassPlayers.add(uuid);
         }

         public void remove(UUID uuid) {
             this.bypassPlayers.remove(uuid);
         }
     }

     public class EnderPearlThrown {
         private Map<UUID, Timestamp> enderPearlThrowns = new HashMap<>();

         public Map<UUID, Timestamp> get() {
             return this.enderPearlThrowns;
         }

         public Timestamp get(UUID uuid) {
             return this.enderPearlThrowns.get(uuid);
         }

         public boolean has(UUID uuid) {
             return this.enderPearlThrowns.containsKey(uuid);
         }

         public void set(Map<UUID, Timestamp> enderPearlThrowns) {
             this.enderPearlThrowns = enderPearlThrowns;
         }

         public void put(UUID uuid) {
             this.enderPearlThrowns.put(uuid, TimeFunctions.now());
         }

         public void remove(UUID uuid) {
             this.enderPearlThrowns.remove(uuid);
         }
     }


     public class PAPI {
         private Map<UUID, PAPIValues> papiValues = new HashMap<>();

         public Map<UUID, PAPIValues> get() {
             return this.papiValues;
         }

         public PAPIValues get(UUID uuid) {
             return this.papiValues.get(uuid);
         }

         public boolean has(UUID uuid) {
             return this.papiValues.containsKey(uuid);
         }

         public void set(Map<UUID, PAPIValues> papiValues) {
             this.papiValues = papiValues;
         }

         public void put(UUID uuid, PAPIValues papiValues) {
             this.papiValues.put(uuid, papiValues);
         }

         public void remove(UUID uuid) {
             this.papiValues.remove(uuid);
         }
     }

 }