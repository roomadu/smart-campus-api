package com.wmin.smartcampus.model;

/**
 * Represents an IoT sensor deployed in a campus room.
 */
public class Sensor {

    private String sensorId;
    private String type;         // e.g. TEMPERATURE, CO2, HUMIDITY
    private String roomId;       // FK → Room
    private String status;       // ACTIVE | MAINTENANCE
    private double currentValue;
    private String unit;

    public Sensor() {}

    public Sensor(String sensorId, String type, String roomId,
                  String status, double currentValue, String unit) {
        this.sensorId     = sensorId;
        this.type         = type;
        this.roomId       = roomId;
        this.status       = status;
        this.currentValue = currentValue;
        this.unit         = unit;
    }

    public String getSensorId()               { return sensorId;     }
    public void   setSensorId(String id)      { this.sensorId = id;  }
    public String getType()                   { return type;         }
    public void   setType(String t)           { this.type = t;       }
    public String getRoomId()                 { return roomId;       }
    public void   setRoomId(String r)         { this.roomId = r;     }
    public String getStatus()                 { return status;       }
    public void   setStatus(String s)         { this.status = s;     }
    public double getCurrentValue()           { return currentValue; }
    public void   setCurrentValue(double v)   { this.currentValue = v; }
    public String getUnit()                   { return unit;         }
    public void   setUnit(String u)           { this.unit = u;       }
}
