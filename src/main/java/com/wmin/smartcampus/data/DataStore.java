package com.wmin.smartcampus.data;

import com.wmin.smartcampus.model.Room;
import com.wmin.smartcampus.model.Sensor;
import com.wmin.smartcampus.model.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central in-memory data store.
 *
 * Uses ConcurrentHashMap so concurrent requests do not cause race-conditions
 * (JAX-RS resources are request-scoped by default; static state is shared).
 */
public class DataStore {

    // ---------- Rooms ----------
    private static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();

    // ---------- Sensors ----------
    private static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();

    // ---------- Readings (keyed by sensorId) ----------
    private static final Map<String, List<SensorReading>> READINGS = new ConcurrentHashMap<>();

    /* ---- seed data -------------------------------------------------------- */
    static {
        Room r1 = new Room("ROOM-001", "Innovation Lecture Hall",  "Central Block",   1, "LECTURE", 180);
        Room r2 = new Room("ROOM-002", "AI & Robotics Lab",        "Engineering Wing", 2, "LAB",      35);
        Room r3 = new Room("ROOM-003", "Faculty Meeting Suite",    "Admin Tower",     4, "OFFICE",   12);
        ROOMS.put(r1.getRoomId(), r1);
        ROOMS.put(r2.getRoomId(), r2);
        ROOMS.put(r3.getRoomId(), r3);

        Sensor s1 = new Sensor("SENS-TEMP-01", "TEMPERATURE", "ROOM-001", "ACTIVE",      21.4, "°C");
        Sensor s2 = new Sensor("SENS-CO2-01",  "CO2",         "ROOM-001", "ACTIVE",     408.0, "ppm");
        Sensor s3 = new Sensor("SENS-TEMP-02", "TEMPERATURE", "ROOM-002", "MAINTENANCE", 19.8, "°C");
        SENSORS.put(s1.getSensorId(), s1);
        SENSORS.put(s2.getSensorId(), s2);
        SENSORS.put(s3.getSensorId(), s3);

        List<SensorReading> hist = new ArrayList<>();
        hist.add(new SensorReading("RD-001", "SENS-TEMP-01", 20.1, "°C", "2026-04-20T08:00:00"));
        hist.add(new SensorReading("RD-002", "SENS-TEMP-01", 21.0, "°C", "2026-04-20T09:00:00"));
        READINGS.put("SENS-TEMP-01", hist);
    }

    /* ---- Room operations -------------------------------------------------- */
    public static Map<String, Room> getAllRooms()           { return ROOMS; }
    public static Room getRoom(String id)                   { return ROOMS.get(id); }
    public static void putRoom(Room room)                   { ROOMS.put(room.getRoomId(), room); }
    public static boolean deleteRoom(String id)             { return ROOMS.remove(id) != null; }
    public static boolean roomExists(String id)             { return ROOMS.containsKey(id); }

    /* ---- Sensor operations ------------------------------------------------ */
    public static Map<String, Sensor> getAllSensors()       { return SENSORS; }
    public static Sensor getSensor(String id)               { return SENSORS.get(id); }
    public static void putSensor(Sensor sensor)             { SENSORS.put(sensor.getSensorId(), sensor); }
    public static boolean deleteSensor(String id)           { return SENSORS.remove(id) != null; }
    public static boolean sensorExists(String id)           { return SENSORS.containsKey(id); }

    /** Returns true when at least one sensor is linked to the given room. */
    public static boolean roomHasSensors(String roomId) {
        return SENSORS.values().stream().anyMatch(s -> roomId.equals(s.getRoomId()));
    }

    /* ---- Reading operations ----------------------------------------------- */
    public static List<SensorReading> getReadings(String sensorId) {
        return READINGS.getOrDefault(sensorId, Collections.emptyList());
    }

    public static void addReading(String sensorId, SensorReading reading) {
        READINGS.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }
}
