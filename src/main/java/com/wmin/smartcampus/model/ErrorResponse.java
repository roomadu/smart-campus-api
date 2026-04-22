package com.wmin.smartcampus.model;

/**
 * Structured JSON error payload returned by every Exception Mapper.
 * Keeps clients informed without exposing internals.
 */
public class ErrorResponse {

    private int    status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse() {}

    public ErrorResponse(int status, String error, String message, String path) {
        this.status  = status;
        this.error   = error;
        this.message = message;
        this.path    = path;
    }

    public int    getStatus()             { return status;  }
    public void   setStatus(int s)        { this.status = s; }
    public String getError()              { return error;   }
    public void   setError(String e)      { this.error = e; }
    public String getMessage()            { return message; }
    public void   setMessage(String m)    { this.message = m; }
    public String getPath()               { return path;    }
    public void   setPath(String p)       { this.path = p;  }
}
