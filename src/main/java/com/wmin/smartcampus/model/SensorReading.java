package com.wmin.smartcampus.model;

/**
 * A single timestamped observation from a sensor.
 */
public class SensorReading {

    private String readingId;
    private String sensorId;
    private double value;
    private String unit;
    private String timestamp;

    public SensorReading() {}

    public SensorReading(String readingId, String sensorId,
                         double value, String unit, String timestamp) {
        this.readingId = readingId;
        this.sensorId  = sensorId;
        this.value     = value;
        this.unit      = unit;
        this.timestamp = timestamp;
    }

    public String getReadingId()              { return readingId;  }
    public void   setReadingId(String id)     { this.readingId = id; }
    public String getSensorId()               { return sensorId;   }
    public void   setSensorId(String id)      { this.sensorId = id; }
    public double getValue()                  { return value;      }
    public void   setValue(double v)          { this.value = v;    }
    public String getUnit()                   { return unit;       }
    public void   setUnit(String u)           { this.unit = u;     }
    public String getTimestamp()              { return timestamp;  }
    public void   setTimestamp(String ts)     { this.timestamp = ts; }
}
