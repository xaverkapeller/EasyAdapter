package at.wrdlbrnft.easyadapter.exceptions;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 16/11/14
 */
public class AnnotationMismatchException extends RuntimeException {

    public AnnotationMismatchException() {
    }

    public AnnotationMismatchException(String detailMessage) {
        super(detailMessage);
    }

    public AnnotationMismatchException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AnnotationMismatchException(Throwable throwable) {
        super(throwable);
    }
}
