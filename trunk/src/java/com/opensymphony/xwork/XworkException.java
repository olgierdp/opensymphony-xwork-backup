package com.opensymphony.xwork;

/**
 * XworkException
 * @author Jason Carreira
 * Created Sep 7, 2003 12:15:03 AM
 */
public class XworkException extends RuntimeException {
    Throwable throwable;

    /**
     * Constructs a <code>XworkException</code> with no detail  message.
     */
    public XworkException() {
    }

    /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public XworkException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>XworkException</code> with no detail  message.
     */
    public XworkException(Throwable cause) {
        this.throwable = cause;
    }

    /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public XworkException(String s, Throwable cause) {
        super(s);
        this.throwable = cause;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Returns a short description of this throwable object.
     * If this <code>Throwable</code> object was
     * {@link #Throwable(String) created} with an error message string,
     * then the result is the concatenation of three strings:
     * <ul>
     * <li>The name of the actual class of this object
     * <li>": " (a colon and a space)
     * <li>The result of the {@link #getMessage} method for this object
     * </ul>
     * If this <code>Throwable</code> object was {@link #Throwable() created}
     * with no error message string, then the name of the actual class of
     * this object is returned.
     *
     * @return  a string representation of this <code>Throwable</code>.
     */
    public String toString() {
        if (throwable == null) {
            return super.toString();
        }

        return super.toString() + " with nested exception " + throwable.toString();
    }
}
