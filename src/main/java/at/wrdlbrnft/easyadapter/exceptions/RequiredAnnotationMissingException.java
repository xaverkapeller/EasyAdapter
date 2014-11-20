package at.wrdlbrnft.easyadapter.exceptions;

/**
 * Created by Xaver on 16/11/14.
 */
public class RequiredAnnotationMissingException extends RuntimeException {

    public RequiredAnnotationMissingException() {
    }

    public RequiredAnnotationMissingException(String detailMessage) {
        super(detailMessage);
    }

    public RequiredAnnotationMissingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RequiredAnnotationMissingException(Throwable throwable) {
        super(throwable);
    }
}
