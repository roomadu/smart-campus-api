package com.wmin.smartcampus.model;

/**
 * Represents a physical campus room / location.
 */
public class Room {

    private String roomId;
    private String name;
    private String building;
    private int floor;
    private String type;   // e.g. LECTURE, LAB, OFFICE
    private int capacity;

    public Room() {}

    public Room(String roomId, String name, String building, int floor, String type, int capacity) {
        this.roomId   = roomId;
        this.name     = name;
        this.building = building;
        this.floor    = floor;
        this.type     = type;
        this.capacity = capacity;
    }

    public String getRoomId()             { return roomId;   }
    public void   setRoomId(String id)    { this.roomId = id; }
    public String getName()               { return name;     }
    public void   setName(String name)    { this.name = name; }
    public String getBuilding()           { return building; }
    public void   setBuilding(String b)   { this.building = b; }
    public int    getFloor()              { return floor;    }
    public void   setFloor(int f)         { this.floor = f;  }
    public String getType()               { return type;     }
    public void   setType(String t)       { this.type = t;   }
    public int    getCapacity()           { return capacity; }
    public void   setCapacity(int c)      { this.capacity = c; }
}
