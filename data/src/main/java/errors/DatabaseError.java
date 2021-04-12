package errors;

public class DatabaseError extends Error {
    public DatabaseError(String _err) {
        super(_err);
    }
}
