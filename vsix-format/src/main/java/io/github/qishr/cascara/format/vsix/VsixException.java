package io.github.qishr.cascara.format.vsix;

import io.github.qishr.cascara.common.i18n.exception.LocalizableRuntimeException;
import io.github.qishr.cascara.common.i18n.LocalizableMessage;

public class VsixException extends LocalizableRuntimeException {

    public VsixException(LocalizableMessage code, Object... details) {
        this(null, code, details);
    }

    public VsixException(Throwable cause, LocalizableMessage code, Object... details) {
        super(cause, code, details);
    }
}
